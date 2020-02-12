package de.mbe.tutorials.aws.serverless.moviesstats.functions.addmovies;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutRequest;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;


public final class FnAddMovies implements RequestHandler<S3Event, Integer> {

    private final int BATCH_SIZE = 1000;

//    private final ClientOverrideConfiguration clientOverrideConfiguration = ClientOverrideConfiguration.builder()
//            .addExecutionInterceptor(new TracingInterceptor())
//            .build();

    private final S3Client s3Client = S3Client.builder()
            //.overrideConfiguration(clientOverrideConfiguration)
            .build();

    private final DynamoDbClient dynamoDBClient = DynamoDbClient.builder()
            //.overrideConfiguration(clientOverrideConfiguration)
            .build();

    @Override
    public Integer handleRequest(final S3Event s3Event, final Context context) {

        final var logger = context.getLogger();
        final var awsRequestId = context.getAwsRequestId();

        logger.log(String.format("AWS_REQUEST_ID: %s, RemainingTimeInMillis: %d", awsRequestId, context.getRemainingTimeInMillis()));

        try {
            this.uploadMovies(s3Event, System.getenv("MOVIES_BUCKET"), System.getenv("MOVIES_TABLE"));
            return 200;
        } catch (IOException error) {
            logger.log(String.format("AWS_REQUEST_ID: %s, IOException: %s", awsRequestId, error.getMessage()));
            return 500;
        } catch (SdkException error) {
            logger.log(String.format("AWS_REQUEST_ID: %s, %s: %s", awsRequestId, error.getClass().getSimpleName(), error.getMessage()));
            return 500;
        }
    }

    private void uploadMovies(final S3Event s3Event, final String moviesBucketName, final String moviesTableName) throws IOException {

        var lines = new ArrayList<String>();

        for (final var record : s3Event.getRecords()) {

            final var s3Entity = record.getS3();
            final var bucket = s3Entity.getBucket().getName();

            if (!bucket.equalsIgnoreCase(moviesBucketName)) {
                continue;
            }

            final var key = s3Entity.getObject().getUrlDecodedKey();

            final var request = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            final var responseInputStream = this.s3Client.getObject(request, ResponseTransformer.toInputStream());

            String line;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(responseInputStream, StandardCharsets.UTF_8))) {
                while ((line = reader.readLine()) != null) {
                    lines.add(line);

                    if (lines.size() == BATCH_SIZE) {
                        saveMovies(lines, moviesTableName);
                        lines.clear();
                    }
                }
            }
        }

        if (!lines.isEmpty()) {
            saveMovies(lines, moviesTableName);
        }
    }

    private void saveMovies(final List<String> lines, final String moviesTableName) {

        if (lines.isEmpty()) {
            return;
        }

        final var writeRequests = new ArrayList<WriteRequest>();

        for (var line : lines) {
            final var parts = line.split(",");
            writeRequests.add(WriteRequest.builder()
                    .putRequest(PutRequest.builder()
                            .item(Map.ofEntries(
                                    new AbstractMap.SimpleEntry<>("id", AttributeValue.builder().s(parts[0]).build()),
                                    new AbstractMap.SimpleEntry<>("name", AttributeValue.builder().s(parts[1]).build()),
                                    new AbstractMap.SimpleEntry<>("country_of_origin", AttributeValue.builder().s(parts[2]).build()),
                                    new AbstractMap.SimpleEntry<>("production_date", AttributeValue.builder().s(parts[3]).build()),
                                    new AbstractMap.SimpleEntry<>("budget", AttributeValue.builder().n(parts[4]).build())
                            ))
                            .build())
                    .build());
        }

        var batchItemRequest = BatchWriteItemRequest.builder()
                .requestItems(Collections.singletonMap(moviesTableName, writeRequests))
                .build();

        this.dynamoDBClient.batchWriteItem(batchItemRequest);
    }
}
