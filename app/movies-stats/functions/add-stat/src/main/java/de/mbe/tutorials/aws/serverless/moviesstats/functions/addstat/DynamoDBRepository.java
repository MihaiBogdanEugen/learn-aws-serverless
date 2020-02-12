package de.mbe.tutorials.aws.serverless.moviesstats.functions.addstat;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.Stat;

public final class DynamoDBRepository {

    private final DynamoDBMapper mapper;
    private final String statsTable;

    public DynamoDBRepository(final AmazonDynamoDB dynamoDB, final String statsTable) {
        this.mapper = new DynamoDBMapper(dynamoDB);
        this.statsTable = statsTable;
    }

    public void saveStat(final Stat stat) throws AmazonDynamoDBException {
        this.mapper.save(stat, DynamoDBMapperConfig.builder()
                .withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(this.statsTable))
                .withSaveBehavior(DynamoDBMapperConfig.SaveBehavior.UPDATE)
                .build());
    }
}