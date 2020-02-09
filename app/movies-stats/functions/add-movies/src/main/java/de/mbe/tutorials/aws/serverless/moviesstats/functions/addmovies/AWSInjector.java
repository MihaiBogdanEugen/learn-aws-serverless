package de.mbe.tutorials.aws.serverless.moviesstats.functions.addmovies;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.s3.AmazonS3;
import dagger.Component;

@Component(modules = AWSInjectionModule.class)
public interface AWSInjector {
    AmazonS3 injectAmazonS3();
    AmazonDynamoDB injectAmazonDynamoDB();
}
