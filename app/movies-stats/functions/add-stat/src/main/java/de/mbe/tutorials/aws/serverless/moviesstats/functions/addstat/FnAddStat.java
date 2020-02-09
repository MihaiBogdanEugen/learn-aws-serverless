package de.mbe.tutorials.aws.serverless.moviesstats.functions.addstat;

import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import de.mbe.tutorials.aws.serverless.moviesstats.functions.addstat.repositories.MoviesStatsRepository;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.Stat;

import javax.inject.Inject;
import java.io.IOException;

import static de.mbe.tutorials.aws.serverless.moviesstatsapp.utils.APIGatewayResponses.*;

public final class FnAddStat implements RequestHandler<APIGatewayV2ProxyRequestEvent, APIGatewayV2ProxyResponseEvent> {

    private static final Injector INJECTOR = Guice.createInjector(new GuiceModule());
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private MoviesStatsRepository repository;

    public FnAddStat() {
        INJECTOR.injectMembers(this);
    }

    @Inject
    public void setRepository(final MoviesStatsRepository repository) {
        this.repository = repository;
    }

    @Override
    public APIGatewayV2ProxyResponseEvent handleRequest(APIGatewayV2ProxyRequestEvent apiGatewayRequestEvent, Context context) {

        final var logger = context.getLogger();
        final var statsTableName = System.getenv("STATS_TABLE");

        if (!apiGatewayRequestEvent.getHttpMethod().equalsIgnoreCase("patch")
                || !apiGatewayRequestEvent.getPathParameters().containsKey("id")
                || apiGatewayRequestEvent.getBody().isBlank()) {
            logger.log("Other method than PATCH used, {id} request parameter is missing or body is empty");
            return badRequest();
        }

        final var id = apiGatewayRequestEvent.getPathParameters().get("id");
        logger.log(String.format("saving stats for the movie # %s", id));

        try {

            final var stat = OBJECT_MAPPER.readValue(apiGatewayRequestEvent.getBody(), Stat.class);

            this.repository.saveStat(stat, statsTableName);

        } catch (IOException error) {
            logger.log(error.getMessage());
            return internalServerError(error.getMessage());

        } catch (AmazonDynamoDBException error) {
            logger.log(error.getMessage());
            return amazonServiceError(error);
        }

        return success();
    }
}
