package de.mbe.tutorials.aws.serverless.moviesstatsapp.getmovieandstatsvc.repositories;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.Movie;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.MovieAndStat;
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
    public MovieAndStat getById(final String id, final String moviesTableName, final String statsTableName) {

        final var result = new MovieAndStat();

        final var moviesConfig = DynamoDBMapperConfig.builder()
                .withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(moviesTableName))
                .withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT)
                .build();

        final var movie = this.mapper.load(Movie.class, id, moviesConfig);
        if (movie != null) {
            result.setMovie(movie);
        }

        final var statsConfig = DynamoDBMapperConfig.builder()
                .withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(statsTableName))
                .withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT)
                .build();

        final var stat = this.mapper.load(Stat.class, id, statsConfig);
        if (stat != null) {
            result.setStat(stat);
        }

        result.setId(id);
        return result;
    }
}
