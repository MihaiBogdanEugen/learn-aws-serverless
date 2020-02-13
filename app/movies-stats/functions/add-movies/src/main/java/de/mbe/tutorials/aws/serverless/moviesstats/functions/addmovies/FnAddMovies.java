package de.mbe.tutorials.aws.serverless.moviesstats.functions.addmovies;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.handlers.TracingHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public final class FnAddMovies implements RequestHandler<S3Event, Integer> {

    private static final Logger LOGGER = LogManager.getLogger(FnAddMovies.class);

    private final UploadFromS3ToDynamoDBService uploadService;

    public FnAddMovies() {

        final var s3Client = AmazonS3ClientBuilder
                .standard()
                .withRequestHandlers(new TracingHandler(AWSXRay.getGlobalRecorder()))
                .build();

        final var dynamoDBClient = AmazonDynamoDBClientBuilder
                .standard()
                .withRequestHandlers(new TracingHandler(AWSXRay.getGlobalRecorder()))
                .build();

        this.uploadService = new UploadFromS3ToDynamoDBService(
                s3Client,
                System.getenv("MOVIES_BUCKET"),
                dynamoDBClient,
                System.getenv("MOVIES_TABLE"));
    }

    @Override
    public Integer handleRequest(final S3Event s3Event, final Context context) {

        LOGGER.info("RemainingTimeInMillis {}", context.getRemainingTimeInMillis());

        try {
            this.uploadService.uploadMovies(s3Event);
            return 200;
        } catch (IOException error) {
            LOGGER.error(error.getMessage(), error);
            return 500;
        } catch (AmazonS3Exception | AmazonDynamoDBException error) {
            LOGGER.error(error.getMessage(), error);
            return error.getStatusCode();
        }
    }
}
