package de.mbe.tutorials.aws.serverless.moviesstatsapp.getmovieandstatsvc;

import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.getmovieandstatsvc.repositories.MoviesStatsRepository;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.MovieAndStat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.mbe.tutorials.aws.serverless.moviesstatsapp.utils.APIGatewayResponses.*;

public final class FnGetMovieAndStat implements RequestHandler<APIGatewayV2ProxyRequestEvent, APIGatewayV2ProxyResponseEvent> {

    private static final Injector INJECTOR = Guice.createInjector(new GuiceModule());
    private static final Logger LOGGER = LoggerFactory.getLogger(FnGetMovieAndStat.class);

    private final ObjectMapper objectMapper;
    private MoviesStatsRepository repository;

    public FnGetMovieAndStat() {

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        INJECTOR.injectMembers(this);
    }

    @Inject
    public void setRepository(final MoviesStatsRepository repository) {
        this.repository = repository;
    }

    @Override
    public APIGatewayV2ProxyResponseEvent handleRequest(final APIGatewayV2ProxyRequestEvent request, final Context context) {

        if (!request.getHttpMethod().equalsIgnoreCase("get")) {
            return methodNotAllowed();
        }

        if (!request.getPathParameters().containsKey("id")) {
            return badRequest();
        }

        final var id = request.getPathParameters().get("id");
        LOGGER.info("retrieving movie with stats for # {}", id);

        final MovieAndStat movieAndStat;

        try {
            movieAndStat = this.repository.getById(id);
        } catch (AmazonDynamoDBException error) {
            LOGGER.error(error.getMessage(), error);
            return amazonServiceError(error);
        }

        if (movieAndStat == null) {
            return notFound();
        }

        try {
            return success(this.objectMapper.writeValueAsString(movieAndStat));
        } catch (JsonProcessingException error) {
            return internalServerError(error.getMessage());
        }
    }
}
