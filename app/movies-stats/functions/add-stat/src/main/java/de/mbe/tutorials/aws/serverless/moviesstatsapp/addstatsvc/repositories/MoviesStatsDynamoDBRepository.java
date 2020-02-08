package de.mbe.tutorials.aws.serverless.moviesstatsapp.addstatsvc.repositories;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.Stat;

public final class MoviesStatsDynamoDBRepository implements MoviesStatsRepository {

    private final AmazonDynamoDB dynamoDB;
    private final DynamoDBMapper mapper;

    public MoviesStatsDynamoDBRepository() {

        this.dynamoDB = AmazonDynamoDBClientBuilder
                .standard()
                .build();

        this.mapper = new DynamoDBMapper(this.dynamoDB);
    }

    @Override
    public void saveStat(final Stat stat) {
        this.mapper.save(stat, DynamoDBMapperConfig.SaveBehavior.UPDATE.config());
    }
}
