package de.mbe.tutorials.aws.serverless.moviesstats.addmovies;

import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import de.mbe.tutorials.aws.serverless.moviesstats.addmovies.repositories.MoviesStatsRepository;
import de.mbe.tutorials.aws.serverless.moviesstats.addmovies.services.MoviesStorageService;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.utils.APIGatewayResponses;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.Movie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

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

        final var moviesTableName = System.getenv("MOVIES_DYNAMODB_TABLE");
        if (moviesTableName == null || moviesTableName.trim().isEmpty()) {
            LOGGER.error("MOVIES_DYNAMODB_TABLE environment variable is not set up");
            return APIGatewayResponses.internalServerError("MOVIES_DYNAMODB_TABLE environment variable is not set up");
        }

        final var moviesBucketArn = System.getenv("MOVIES_S3_BUCKET");
        if (moviesBucketArn == null || moviesBucketArn.trim().isEmpty()) {
            LOGGER.error("MOVIES_S3_BUCKET environment variable is not set up");
            return APIGatewayResponses.internalServerError("MOVIES_S3_BUCKET environment variable is not set up");
        }

        final List<Movie> movies;

        try {
            movies = this.storageService.getMovies(s3Event, moviesBucketArn);
        } catch (IOException error) {
            LOGGER.error(error.getMessage());
            return APIGatewayResponses.internalServerError(error.getMessage());
        }

        LOGGER.info("Uploading {} movies", movies.size());

        try {
            this.repository.saveMovies(movies, moviesTableName);
        } catch (AmazonDynamoDBException error) {
            LOGGER.error(error.getMessage());
            return APIGatewayResponses.amazonServiceError(error);
        }

        return APIGatewayResponses.success();
    }
}
