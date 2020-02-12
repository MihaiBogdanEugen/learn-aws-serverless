package de.mbe.tutorials.aws.serverless.moviesstats.functions.addstat;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.Stat;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class FnAddStat implements RequestHandler<APIGatewayV2ProxyRequestEvent, APIGatewayV2ProxyResponseEvent> {

    private final ObjectMapper mapper;

//    private final ClientOverrideConfiguration clientOverrideConfiguration = ClientOverrideConfiguration.builder()
//            .addExecutionInterceptor(new TracingInterceptor())
//            .build();

    private final DynamoDbClient dynamoDBClient;

    public FnAddStat() {
        this.mapper = new ObjectMapper();
        this.mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        this.dynamoDBClient = DynamoDbClient.builder()
                //.overrideConfiguration(clientOverrideConfiguration)
                .build();
    }

    @Override
    public APIGatewayV2ProxyResponseEvent handleRequest(final APIGatewayV2ProxyRequestEvent request, final Context context) {

        final var logger = context.getLogger();
        final var awsRequestId = context.getAwsRequestId();

        logger.log(String.format("AWS_REQUEST_ID: %s, RemainingTimeInMillis: %d", awsRequestId, context.getRemainingTimeInMillis()));

        final var requestHttpMethod = request.getHttpMethod();
        if (!requestHttpMethod.equalsIgnoreCase("patch")) {
            return reply(405, String.format("Unsupported http method %s", requestHttpMethod), logger, awsRequestId);
        }

        if (request.getBody().isBlank()) {
            return reply(400, "Empty body", logger, awsRequestId);
        }

        if (!request.getPathParameters().containsKey("id")) {
            return reply(400, "Missing parameter id", logger, awsRequestId);
        }

        final var id = request.getPathParameters().get("id");
        logger.log(String.format("AWS_REQUEST_ID: %s, Patching movie with the identifier: %s", awsRequestId, id));

        Stat stat;
        try {
            stat = this.mapper.readValue(request.getBody(), Stat.class);
        } catch (IOException error) {
            logger.log(String.format("AWS_REQUEST_ID: %s, IOException: %s", awsRequestId, error.getMessage()));
            return reply(500, error.getMessage(), logger, awsRequestId);
        }

        if (stat == null) {
            return reply(400, "Empty body", logger, awsRequestId);
        }

        try {
            this.updateStat(id, System.getenv("STATS_TABLE"), stat);
        } catch (SdkException error) {
            logger.log(String.format("AWS_REQUEST_ID: %s, %s: %s", awsRequestId, error.getClass().getSimpleName(), error.getMessage()));
            return reply(500, error.getMessage(), logger, awsRequestId);
        }

        return reply(200, "", logger, awsRequestId);
    }

    private static APIGatewayV2ProxyResponseEvent reply(final int statusCode, final String body, final LambdaLogger logger, final String awsRequestId) {
        logger.log(String.format("AWS_REQUEST_ID: %s, StatusCode: %d, Message: %s", awsRequestId, statusCode, body));
        final var response = new APIGatewayV2ProxyResponseEvent();
        response.setStatusCode(statusCode);
        if (!body.isEmpty()) {
            response.setBody(body);
        }
        return response;
    }

    private void updateStat(final String id, final String statsTable, final Stat stat) {

        final var attributeUpdates = new HashMap<String, AttributeValueUpdate>();

        if (stat.isDirectToStreaming() != null) {
            attributeUpdates.put("direct_to_streaming", AttributeValueUpdate.builder().value(x -> x.bool(stat.isDirectToStreaming())).build());
        }

        if (stat.getRottenTomatoesRating() != null) {
            attributeUpdates.put("rotten_tomatoes_rating", AttributeValueUpdate.builder().value(x -> x.n(stat.getRottenTomatoesRating().toString())).build());
        }

        if (stat.getImdbRating() != null) {
            attributeUpdates.put("imdb_rating", AttributeValueUpdate.builder().value(x -> x.n(stat.getImdbRating().toString())).build());
        }

        if (stat.getBoxOffice() != null) {
            attributeUpdates.put("box_office", AttributeValueUpdate.builder().value(x -> x.n(stat.getBoxOffice().toString())).build());
        }

        if (!stat.getReleaseDate().isEmpty()) {
            attributeUpdates.put("release_date", AttributeValueUpdate.builder().value(x -> x.s(stat.getReleaseDate())).build());
        }

        final var updateItemRequest = UpdateItemRequest.builder()
                .tableName(statsTable)
                .key(Map.of("id", AttributeValue.builder().s(id).build()))
                .attributeUpdates(attributeUpdates)
                .build();

        this.dynamoDBClient.updateItem(updateItemRequest);
    }
}
