package de.mbe.tutorials.aws.serverless.moviesstats.functions.addmovies;

import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.model.AmazonS3Exception;

import java.io.IOException;

public final class FnAddMovies implements RequestHandler<S3Event, Integer> {

    private final MoviesS3StorageService STORAGE_SERVICE = new MoviesS3StorageService();
    private final MoviesStatsDynamoDBRepository REPOSITORY = new MoviesStatsDynamoDBRepository();

    @Override
    public Integer handleRequest(final S3Event s3Event, final Context context) {

        final var logger = context.getLogger();
        final var awsRequestId = context.getAwsRequestId();

        logger.log(String.format("AWS_REQUEST_ID: %s, RemainingTimeInMillis: %d", awsRequestId, context.getRemainingTimeInMillis()));

        try {
            doWork(s3Event, context);
            return 200;
        } catch (IOException error) {
            logger.log(String.format("AWS_REQUEST_ID: %s, IOException: %s", awsRequestId, error.getMessage()));
            return 500;
        } catch (AmazonS3Exception | AmazonDynamoDBException error) {
            logger.log(String.format("AWS_REQUEST_ID: %s, AmazonS3Exception/AmazonDynamoDBException: %s", awsRequestId, error.getMessage()));
            return error.getStatusCode();
        }
    }

    public void doWork(final S3Event s3Event, final Context context) throws IOException, AmazonS3Exception, AmazonDynamoDBException {

        final var logger = context.getLogger();
        final var awsRequestId = context.getAwsRequestId();

        final var movies = STORAGE_SERVICE.getMovies(s3Event, System.getenv("MOVIES_BUCKET"));
        logger.log(String.format("AWS_REQUEST_ID: %s, %d movies read from the file", awsRequestId, movies.size()));

        final var noOfPersistedMovies = REPOSITORY.saveMovies(movies, System.getenv("MOVIES_TABLE"));
        logger.log(String.format("AWS_REQUEST_ID: %s, %d movies wrote to the database", awsRequestId, noOfPersistedMovies));
    }
}
