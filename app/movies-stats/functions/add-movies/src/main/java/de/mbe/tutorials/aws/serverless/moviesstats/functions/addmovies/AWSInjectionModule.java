package de.mbe.tutorials.aws.serverless.moviesstats.functions.addmovies;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import dagger.Module;
import dagger.Provides;

@Module
public final class AWSInjectionModule {

    private static AmazonS3 s3Client;
    private static AmazonDynamoDB dynamoDBClient;

    @Provides
    static AmazonS3 provideAmazonS3(){

        if (s3Client != null) {
            return s3Client;
        }

        s3Client = AmazonS3ClientBuilder
                .standard()
                .build();

        return s3Client;
    }

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
