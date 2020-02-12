package de.mbe.tutorials.aws.serverless.moviesstats.functions.getmovieandstat;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.Movie;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.MovieAndStat;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.Stat;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;

import java.io.IOException;
import java.util.Map;

public final class FnGetMovieAndStat implements RequestHandler<APIGatewayV2ProxyRequestEvent, APIGatewayV2ProxyResponseEvent> {

    private final ObjectMapper mapper;

//    private final ClientOverrideConfiguration clientOverrideConfiguration = ClientOverrideConfiguration.builder()
//            .addExecutionInterceptor(new TracingInterceptor())
//            .build();

    private final DynamoDbClient dynamoDBClient;

    public FnGetMovieAndStat() {
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
        if (!requestHttpMethod.equalsIgnoreCase("get")) {
            return reply(405, String.format("Unsupported http method %s", requestHttpMethod), logger, awsRequestId);
        }

        if (!request.getPathParameters().containsKey("id")) {
            return reply(400, "Missing parameter id", logger, awsRequestId);
        }

        final var id = request.getPathParameters().get("id");
        logger.log(String.format("AWS_REQUEST_ID: %s, Retrieving movie with identifier: %s", awsRequestId, id));

        MovieAndStat result;

        try {
            result = this.getById(id, System.getenv("MOVIES_TABLE"), System.getenv("STATS_TABLE"));
        } catch (SdkException error) {
            logger.log(String.format("AWS_REQUEST_ID: %s, %s: %s", awsRequestId, error.getClass().getSimpleName(), error.getMessage()));
            return reply(500, error.getMessage(), logger, awsRequestId);
        }

        if (result == null) {
            return reply(404, String.format("No records for %s", id), logger, awsRequestId);
        }

        try {
            return reply(200, this.mapper.writeValueAsString(result), logger, awsRequestId);
        } catch (IOException error) {
            logger.log(String.format("AWS_REQUEST_ID: %s, IOException: %s", awsRequestId, error.getMessage()));
            return reply(500, error.getMessage(), logger, awsRequestId);
        }
    }

    private static APIGatewayV2ProxyResponseEvent reply(final int statusCode, final String body, final LambdaLogger logger, final String awsRequestId) {
        logger.log(String.format("AWS_REQUEST_ID: %s, StatusCode: %d, Message: %s", awsRequestId, statusCode, body));
        final var response = new APIGatewayV2ProxyResponseEvent();
        response.setStatusCode(statusCode);
        response.setBody(body);
        return response;
    }

    private MovieAndStat getById(final String id, final String moviesTable, final String statsTable) {

        final var movie = this.getMovieById(id, moviesTable);
        final var stat = this.getStatById(id, statsTable);

        if (movie == null && stat == null) {
            return null;
        }

        final var result = new MovieAndStat();
        result.setId(id);

        if (movie != null) {
            result.setMovie(movie);
        }

        if (stat != null) {
            result.setStat(stat);
        }

        return result;
    }

    private Movie getMovieById(final String id, final String moviesTable) {

        final var getItemRequest = GetItemRequest.builder()
                .key(Map.of("id", AttributeValue.builder().s(id).build()))
                .tableName(moviesTable)
                .consistentRead(true)
                .build();

        final var getItemResponse = this.dynamoDBClient.getItem(getItemRequest);
        if (!getItemResponse.hasItem()) {
            return null;
        }

        final var movie = new Movie();

        getItemRequest.getValueForField("name", String.class).ifPresent(movie::setName);
        getItemRequest.getValueForField("country_of_origin", String.class).ifPresent(movie::setCountryOfOrigin);
        getItemRequest.getValueForField("production_date", String.class).ifPresent(movie::setProductionDate);
        getItemRequest.getValueForField("budget", Double.class).ifPresent(movie::setBudget);

        return movie;
    }

    private Stat getStatById(final String id, final String statsTable) {

        final var getItemRequest = GetItemRequest.builder()
                .key(Map.of("id", AttributeValue.builder().s(id).build()))
                .tableName(statsTable)
                .consistentRead(true)
                .build();

        final var getItemResponse = this.dynamoDBClient.getItem(getItemRequest);
        if (!getItemResponse.hasItem()) {
            return null;
        }

        final var stat = new Stat();

        getItemRequest.getValueForField("direct_to_streaming", Boolean.class).ifPresent(stat::setDirectToStreaming);
        getItemRequest.getValueForField("rotten_tomatoes_rating", Integer.class).ifPresent(stat::setRottenTomatoesRating);
        getItemRequest.getValueForField("imdb_rating", Integer.class).ifPresent(stat::setImdbRating);
        getItemRequest.getValueForField("box_office", Double.class).ifPresent(stat::setBoxOffice);
        getItemRequest.getValueForField("release_date", String.class).ifPresent(stat::setReleaseDate);

        return stat;
    }
}
