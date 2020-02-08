package de.mbe.tutorials.aws.serverless.moviesstats.addmovies;

import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import de.mbe.tutorials.aws.serverless.moviesstats.addmovies.repositories.MoviesStatsRepository;
import de.mbe.tutorials.aws.serverless.moviesstats.addmovies.services.MoviesStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static de.mbe.tutorials.aws.serverless.moviesstatsapp.utils.APIGatewayResponses.*;

public final class FnAddMovies implements RequestHandler<S3Event, APIGatewayV2ProxyResponseEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FnAddMovies.class);
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
    public APIGatewayV2ProxyResponseEvent handleRequest(final S3Event s3Event, final Context context) {

        final var moviesTableName = System.getenv("MOVIES_TABLE");
        final var moviesBucketName = System.getenv("MOVIES_BUCKET");

        try {

            final var movies = this.storageService.getMovies(s3Event, moviesBucketName);
            LOGGER.info("{} movies read from the file", movies.size());

            final var noOfPersistedMovies = this.repository.saveMovies(movies, moviesTableName);
            LOGGER.info("{} movies wrote to the database", noOfPersistedMovies);

        } catch (IOException error) {
            LOGGER.error(error.getMessage());
            return internalServerError(error.getMessage());

        } catch (AmazonS3Exception | AmazonDynamoDBException error) {
            LOGGER.error(error.getMessage(), error);
            return amazonServiceError(error);
        }

        return success();
    }
}
