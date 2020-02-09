package de.mbe.tutorials.aws.serverless.moviesstats.functions.getmovieandstat;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import dagger.Module;
import dagger.Provides;

@Module
public final class AWSInjectionModule {

    private static AmazonDynamoDB dynamoDBClient;

    @Provides
    static AmazonDynamoDB provideAmazonDynamoDB(){

        if (dynamoDBClient != null) {
            return dynamoDBClient;
        }

        dynamoDBClient = AmazonDynamoDBClientBuilder
                .standard()
                .build();

        return dynamoDBClient;
    }
}
