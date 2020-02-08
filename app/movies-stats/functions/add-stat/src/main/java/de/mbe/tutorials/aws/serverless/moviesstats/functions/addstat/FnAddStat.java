package de.mbe.tutorials.aws.serverless.moviesstats.functions.addstat;

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
import com.google.inject.Injector;
import de.mbe.tutorials.aws.serverless.moviesstats.functions.addstat.repositories.MoviesStatsRepository;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static de.mbe.tutorials.aws.serverless.moviesstatsapp.utils.APIGatewayResponses.*;

public final class FnAddStat implements RequestHandler<APIGatewayV2ProxyRequestEvent, APIGatewayV2ProxyResponseEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FnAddStat.class);
    private static final Injector INJECTOR = Guice.createInjector(new GuiceModule());

    private final ObjectMapper objectMapper;
    private MoviesStatsRepository repository;

    public FnAddStat() {

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
    public APIGatewayV2ProxyResponseEvent handleRequest(APIGatewayV2ProxyRequestEvent apiGatewayRequestEvent, Context context) {

        final var statsTableName = System.getenv("STATS_TABLE");

        if (apiGatewayRequestEvent.getHttpMethod().equalsIgnoreCase("patch")
                || !apiGatewayRequestEvent.getPathParameters().containsKey("id")
                || apiGatewayRequestEvent.getBody().isBlank()) {
            LOGGER.error("Other method than PATCH used, {id} request parameter is missing or body is empty");
            return badRequest();
        }

        final var id = apiGatewayRequestEvent.getPathParameters().get("id");
        LOGGER.info("saving stats for the movie # {}", id);

        try {

            final var stat = this.objectMapper.readValue(apiGatewayRequestEvent.getBody(), Stat.class);

            this.repository.saveStat(stat, statsTableName);

        } catch (JsonProcessingException error) {
            LOGGER.error(error.getMessage());
            return internalServerError(error.getMessage());

        } catch (AmazonDynamoDBException error) {
            LOGGER.error(error.getMessage(), error);
            return amazonServiceError(error);
        }

        return success();
    }
}
