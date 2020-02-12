package de.mbe.tutorials.aws.serverless.moviesstats.functions.addmovies;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;

import java.io.IOException;

public final class FnAddMovies implements RequestHandler<S3Event, Integer> {

    private final UploadFromS3ToDynamoDBService uploadService;

    public FnAddMovies() {

        final var s3Client = AmazonS3ClientBuilder
                .standard()
                .build();

        final var dynamoDBClient = AmazonDynamoDBClientBuilder
                .standard()
                .build();

        this.uploadService = new UploadFromS3ToDynamoDBService(
                s3Client,
                System.getenv("MOVIES_BUCKET"),
                dynamoDBClient,
                System.getenv("MOVIES_TABLE"));
    }

    @Override
    public Integer handleRequest(final S3Event s3Event, final Context context) {

        final var logger = context.getLogger();
        final var awsRequestId = context.getAwsRequestId();

        logger.log(String.format("AWS_REQUEST_ID: %s, RemainingTimeInMillis: %d", awsRequestId, context.getRemainingTimeInMillis()));

        try {
            this.uploadService.uploadMovies(s3Event);
            return 200;
        } catch (IOException error) {
            logger.log(String.format("AWS_REQUEST_ID: %s, IOException: %s", awsRequestId, error.getMessage()));
            return 500;
        } catch (AmazonS3Exception | AmazonDynamoDBException error) {
            logger.log(String.format("AWS_REQUEST_ID: %s, AmazonS3Exception/AmazonDynamoDBException: %s", awsRequestId, error.getMessage()));
            return error.getStatusCode();
        }
    }
}
