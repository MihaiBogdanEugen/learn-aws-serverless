package de.mbe.tutorials.aws.serverless.moviesstats.functions.addmovies;

import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import de.mbe.tutorials.aws.serverless.moviesstats.functions.addmovies.repositories.MoviesStatsRepository;
import de.mbe.tutorials.aws.serverless.moviesstats.functions.addmovies.services.MoviesStorageService;

import java.io.IOException;

public final class FnAddMovies implements RequestHandler<S3Event, Integer> {

    private static final Injector INJECTOR = Guice.createInjector(new GuiceModule());

    private MoviesStorageService storageService;
    private MoviesStatsRepository repository;

    public FnAddMovies() {
        INJECTOR.injectMembers(this);
    }

    @Inject
    public void setStorageService(final MoviesStorageService storageService) {
        this.storageService = storageService;
    }

    @Inject
    public void setRepository(final MoviesStatsRepository repository) {
        this.repository = repository;
    }

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

        final var movies = this.storageService.getMovies(s3Event, System.getenv("MOVIES_BUCKET"));
        logger.log(String.format("AWS_REQUEST_ID: %s, %d movies read from the file", awsRequestId, movies.size()));

        final var noOfPersistedMovies = this.repository.saveMovies(movies, System.getenv("MOVIES_TABLE"));
        logger.log(String.format("AWS_REQUEST_ID: %s, %d movies wrote to the database", awsRequestId, noOfPersistedMovies));
    }
}
