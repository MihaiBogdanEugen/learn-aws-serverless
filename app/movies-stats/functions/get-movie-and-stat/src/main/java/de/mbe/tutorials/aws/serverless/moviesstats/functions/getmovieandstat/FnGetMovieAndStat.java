package de.mbe.tutorials.aws.serverless.moviesstats.functions.getmovieandstat;

import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import de.mbe.tutorials.aws.serverless.moviesstats.functions.getmovieandstat.repositories.MoviesStatsRepository;

import static de.mbe.tutorials.aws.serverless.moviesstatsapp.utils.APIGatewayResponses.*;

public final class FnGetMovieAndStat implements RequestHandler<APIGatewayV2ProxyRequestEvent, APIGatewayV2ProxyResponseEvent> {

    private static final Injector INJECTOR = Guice.createInjector(new GuiceModule());
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private MoviesStatsRepository repository;

    public FnGetMovieAndStat() {
        INJECTOR.injectMembers(this);
    }

    @Inject
    public void setRepository(final MoviesStatsRepository repository) {
        this.repository = repository;
    }

    @Override
    public APIGatewayV2ProxyResponseEvent handleRequest(final APIGatewayV2ProxyRequestEvent request, final Context context) {

        final var logger = context.getLogger();
        final var moviesTableName = System.getenv("MOVIES_TABLE");
        final var statsTableName = System.getenv("STATS_TABLE");

        if (!request.getHttpMethod().equalsIgnoreCase("get")
                || !request.getPathParameters().containsKey("id")) {
            logger.log("Other method than GET used or {id} request parameter is missing");
            return badRequest();
        }

        final var id = request.getPathParameters().get("id");
        logger.log(String.format("retrieving movie with stats # %s", id));

        try {

            final var movieAndStat = this.repository.getById(id, moviesTableName, statsTableName);

            if (movieAndStat == null) {
                logger.log(String.format("No records for # %s", id));
                return notFound();
            } else {
                return success(OBJECT_MAPPER.writeValueAsString(movieAndStat));
            }

        } catch (JsonProcessingException error) {
            logger.log(error.getMessage());
            return internalServerError(error.getMessage());

        } catch (AmazonDynamoDBException error) {
            logger.log(error.getMessage());
            return amazonServiceError(error);
        }
    }
}
