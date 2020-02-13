package de.mbe.tutorials.aws.serverless.moviesstats.functions.addstat;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;
import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.handlers.TracingHandler;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.Stat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public final class FnAddStat implements RequestHandler<APIGatewayV2ProxyRequestEvent, APIGatewayV2ProxyResponseEvent> {

    private static final Logger LOGGER = LogManager.getLogger(FnAddStat.class);

    private final ObjectMapper mapper;
    private final DynamoDBRepository repository;

    public FnAddStat() {

        this.mapper = new ObjectMapper();
        this.mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        final var dynamoDBClient = AmazonDynamoDBClientBuilder
                .standard()
                .withRequestHandlers(new TracingHandler(AWSXRay.getGlobalRecorder()))
                .build();

        this.repository = new DynamoDBRepository(
                dynamoDBClient,
                System.getenv("STATS_TABLE"));
    }

    @Override
    public APIGatewayV2ProxyResponseEvent handleRequest(final APIGatewayV2ProxyRequestEvent request, final Context context) {

        LOGGER.info("RemainingTimeInMillis {}", context.getRemainingTimeInMillis());

        final var requestHttpMethod = request.getHttpMethod();
        if (!requestHttpMethod.equalsIgnoreCase("patch")) {
            return reply(405, String.format("Unsupported http method %s", requestHttpMethod));
        }

        if (request.getBody().isBlank()) {
            return reply(400, "Empty body");
        }

        if (!request.getPathParameters().containsKey("id")) {
            return reply(400, "Missing parameter id");
        }

        final var id = request.getPathParameters().get("id");
        LOGGER.info("Patching movie with the identifier {}", id);

        Stat stat;

        try {
            stat = this.mapper.readValue(request.getBody(), Stat.class);
        } catch (IOException error) {
            LOGGER.error(error.getMessage(), error);
            return reply(500, error.getMessage());
        }

        if (stat == null) {
            return reply(400, "Empty body");
        }

        try {
            this.repository.saveStat(stat);
            return reply(200, "");
        } catch (AmazonDynamoDBException error) {
            LOGGER.error(error.getMessage(), error);
            return reply(error.getStatusCode(), error.getMessage());
        }
    }

    private static APIGatewayV2ProxyResponseEvent reply(final int statusCode, final String body) {
        final var response = new APIGatewayV2ProxyResponseEvent();
        response.setStatusCode(statusCode);
        if (body != null && !body.isEmpty()) {
            response.setBody(body);
        }
        return response;
    }
}
