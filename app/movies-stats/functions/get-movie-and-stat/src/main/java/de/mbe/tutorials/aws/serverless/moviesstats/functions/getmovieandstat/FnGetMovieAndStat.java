package de.mbe.tutorials.aws.serverless.moviesstats.functions.getmovieandstat;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.MovieAndStat;

import java.io.IOException;

public final class FnGetMovieAndStat implements RequestHandler<APIGatewayV2ProxyRequestEvent, APIGatewayV2ProxyResponseEvent> {

    private final ObjectMapper mapper;
    private final DynamoDBRepository repository;

    public FnGetMovieAndStat() {

        this.mapper = new ObjectMapper();
        this.mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        final var dynamoDBClient = AmazonDynamoDBClientBuilder
                .standard()
                .build();

        this.repository = new DynamoDBRepository(
                dynamoDBClient,
                System.getenv("MOVIES_TABLE"),
                System.getenv("STATS_TABLE"));
    }

    @Override
    public APIGatewayV2ProxyResponseEvent handleRequest(final APIGatewayV2ProxyRequestEvent request, final Context context) {

        final var logger = context.getLogger();
        final var awsRequestId = context.getAwsRequestId();

        logger.log(String.format("AWS_REQUEST_ID: %s, RemainingTimeInMillis: %d", awsRequestId, context.getRemainingTimeInMillis()));

        final var requestHttpMethod = request.getHttpMethod();
        if (!requestHttpMethod.equalsIgnoreCase("get")) {
            return reply(405, String.format("Unsupported http method %s", requestHttpMethod));
        }

        if (!request.getPathParameters().containsKey("id")) {
            return reply(400, "Missing parameter id");
        }

        final var id = request.getPathParameters().get("id");
        logger.log(String.format("AWS_REQUEST_ID: %s, Retrieving movie with identifier: %s", awsRequestId, id));

        MovieAndStat movieAndStat;

        try {
            movieAndStat = this.repository.getById(id);
        } catch (AmazonDynamoDBException error) {
            logger.log(String.format("AWS_REQUEST_ID: %s, AmazonDynamoDBException: %s", awsRequestId, error.getMessage()));
            return reply(error.getStatusCode(), error.getMessage());
        }

        if (movieAndStat == null) {
            return reply(404, String.format("No records for %s", id));
        }

        try {
            return reply(200, this.mapper.writeValueAsString(movieAndStat));
        } catch (IOException error) {
            logger.log(String.format("AWS_REQUEST_ID: %s, IOException: %s", awsRequestId, error.getMessage()));
            return reply(500, error.getMessage());
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
