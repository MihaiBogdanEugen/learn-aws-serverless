package de.mbe.tutorials.aws.serverless.moviesstats.functions.addmovies;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.Movie;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class MoviesStatsDynamoDBRepository {

    private final DynamoDBMapper mapper;

    public MoviesStatsDynamoDBRepository() {

        final var dynamoDBClient = AmazonDynamoDBClientBuilder
                .standard()
                .build();

        this.mapper = new DynamoDBMapper(dynamoDBClient);
    }

    public long saveMovies(final List<Movie> movies, final String moviesTableName) {

        final var config = DynamoDBMapperConfig.builder()
                .withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(moviesTableName))
                .withSaveBehavior(DynamoDBMapperConfig.SaveBehavior.CLOBBER)
                .build();

        final var result = this.mapper.batchWrite(movies, Collections.emptyList(), config);

        return result.stream()
                .map(x -> x.getUnprocessedItems().entrySet())
                .flatMap(Collection::stream)
                .map(Map.Entry::getValue)
                .mapToLong(Collection::size)
                .sum();
    }
}
