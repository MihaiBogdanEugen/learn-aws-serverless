package de.mbe.tutorials.aws.serverless.moviesstats.functions.addstat.repositories;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.Stat;

public final class MoviesStatsDynamoDBRepository implements MoviesStatsRepository {

    private final DynamoDBMapper mapper;

    public MoviesStatsDynamoDBRepository() {

        final var dynamoDBClient = AmazonDynamoDBClientBuilder
                .standard()
                .build();

        this.mapper = new DynamoDBMapper(dynamoDBClient);
    }

    @Override
    public void saveStat(final Stat stat, final String statsTableName) {

        final var config = DynamoDBMapperConfig.builder()
                .withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(statsTableName))
                .withSaveBehavior(DynamoDBMapperConfig.SaveBehavior.UPDATE)
                .build();

        this.mapper.save(stat, config);
    }
}