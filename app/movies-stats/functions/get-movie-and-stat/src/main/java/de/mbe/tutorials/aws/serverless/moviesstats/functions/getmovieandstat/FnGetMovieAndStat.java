package de.mbe.tutorials.aws.serverless.moviesstats.functions.getmovieandstat;

import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

public final class FnGetMovieAndStat implements RequestHandler<APIGatewayV2ProxyRequestEvent, APIGatewayV2ProxyResponseEvent> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final MoviesStatsDynamoDBRepository REPOSITORY = new MoviesStatsDynamoDBRepository();

    @Override
    public APIGatewayV2ProxyResponseEvent handleRequest(final APIGatewayV2ProxyRequestEvent request, final Context context) {

        final var logger = context.getLogger();
        final var awsRequestId = context.getAwsRequestId();

        logger.log(String.format("AWS_REQUEST_ID: %s, RemainingTimeInMillis: %d", awsRequestId, context.getRemainingTimeInMillis()));

        try {
            final var result = doWork(request, context);
            return reply(result.getKey(), result.getValue());
        } catch (IOException error) {
            logger.log(String.format("AWS_REQUEST_ID: %s, IOException: %s", awsRequestId, error.getMessage()));
            return reply(500, error.getMessage());
        } catch (AmazonDynamoDBException error) {
            logger.log(String.format("AWS_REQUEST_ID: %s, AmazonDynamoDBException: %s", awsRequestId, error.getMessage()));
            return reply(error.getStatusCode(), error.getMessage());
        }
    }

    private Map.Entry<Integer, String> doWork(final APIGatewayV2ProxyRequestEvent request, final Context context) throws IOException, AmazonDynamoDBException {

        final var logger = context.getLogger();
        final var awsRequestId = context.getAwsRequestId();

        final var requestHttpMethod = request.getHttpMethod();
        if (!requestHttpMethod.equalsIgnoreCase("get")) {
            return Map.entry(405, String.format("Unsupported http method %s", requestHttpMethod));
        }

        if (!request.getPathParameters().containsKey("id")) {
            return Map.entry(400, "Missing parameter id");
        }

        final var id = request.getPathParameters().get("id");
        logger.log(String.format("AWS_REQUEST_ID: %s, Retrieving movie with identifier: %s", awsRequestId, id));
        final var movieAndStat = REPOSITORY.getById(id, System.getenv("MOVIES_TABLE"), System.getenv("STATS_TABLE"));

        if (movieAndStat == null) {
            return Map.entry(404, String.format("No records for %s", id));
        }

        final var resultAsString = OBJECT_MAPPER.writeValueAsString(movieAndStat);
        return Map.entry(200, resultAsString);
    }

    private static APIGatewayV2ProxyResponseEvent reply(final int statusCode, final String body) {
        final var response = new APIGatewayV2ProxyResponseEvent();
        response.setStatusCode(statusCode);
        response.setBody(body);
        return response;
    }
}
