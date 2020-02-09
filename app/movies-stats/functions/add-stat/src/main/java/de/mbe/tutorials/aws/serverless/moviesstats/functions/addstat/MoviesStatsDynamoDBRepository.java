package de.mbe.tutorials.aws.serverless.moviesstats.functions.addstat;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.Stat;

public final class MoviesStatsDynamoDBRepository {

    private final DynamoDBMapper mapper;

    public MoviesStatsDynamoDBRepository() {

        final var injector = DaggerAWSInjector.create();
        final var dynamoDBClient = injector.injectAmazonDynamoDB();
        this.mapper = new DynamoDBMapper(dynamoDBClient);
    }

    public void saveStat(final Stat stat, final String statsTableName) {

        final var config = DynamoDBMapperConfig.builder()
                .withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(statsTableName))
                .withSaveBehavior(DynamoDBMapperConfig.SaveBehavior.UPDATE)
                .build();

        this.mapper.save(stat, config);
    }
}