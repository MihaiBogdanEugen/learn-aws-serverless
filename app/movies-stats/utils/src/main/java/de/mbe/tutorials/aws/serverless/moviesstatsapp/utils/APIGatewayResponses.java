package de.mbe.tutorials.aws.serverless.moviesstatsapp.utils;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;

public class APIGatewayResponses {

    public static APIGatewayV2ProxyResponseEvent success() {
        return success("Success");
    }

    public static APIGatewayV2ProxyResponseEvent success(final String body) {
        return response(200, body);
    }

    public static APIGatewayV2ProxyResponseEvent badRequest() {
        return response(400, "Bad Request");
    }

    public static APIGatewayV2ProxyResponseEvent notFound() {
        return response(404, "Not Found");
    }

    public static APIGatewayV2ProxyResponseEvent methodNotAllowed() {
        return response(405, "Method Not Allowed");
    }

    public static APIGatewayV2ProxyResponseEvent internalServerError(final String body) {
        return response(500, body);
    }

    public static APIGatewayV2ProxyResponseEvent amazonServiceError(final AmazonServiceException error) {
        return response(error.getStatusCode(), error.getMessage());
    }

    private static APIGatewayV2ProxyResponseEvent response(final int statusCode, final String body) {
        final APIGatewayV2ProxyResponseEvent response = new APIGatewayV2ProxyResponseEvent();
        response.setStatusCode(statusCode);
        response.setBody(body);
        return response;
    }
}
