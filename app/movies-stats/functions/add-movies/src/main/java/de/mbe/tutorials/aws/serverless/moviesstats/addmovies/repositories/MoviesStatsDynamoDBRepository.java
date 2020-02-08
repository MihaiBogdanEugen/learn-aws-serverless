package de.mbe.tutorials.aws.serverless.moviesstats.addmovies.repositories;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.Movie;

import java.util.List;

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
    public void saveMovies(final List<Movie> movies, final String moviesTableName) {

        final var config = DynamoDBMapperConfig.builder()
                .withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(moviesTableName))
                .withSaveBehavior(DynamoDBMapperConfig.SaveBehavior.CLOBBER)
                .build();

        this.mapper.batchWrite(movies, null, config);
    }
}
