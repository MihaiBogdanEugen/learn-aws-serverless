package de.mbe.tutorials.aws.serverless.moviesstats.functions.getmovieandstat.repositories;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.Movie;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.MovieAndStat;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.Stat;

public final class MoviesStatsDynamoDBRepository {

    private final DynamoDBMapper mapper;

    public MoviesStatsDynamoDBRepository() {

        final var dynamoDBClient = AmazonDynamoDBClientBuilder
                .standard()
                .build();

        this.mapper = new DynamoDBMapper(dynamoDBClient);
    }

    public MovieAndStat getById(final String id, final String moviesTableName, final String statsTableName) {

        final var movie = this.getMovieById(id, moviesTableName);
        final var stat = this.getStatById(id, statsTableName);

        if (movie == null && stat == null) {
            return null;
        }

        final var result = new MovieAndStat();
        result.setId(id);

        if (movie != null) {
            result.setMovie(movie);
        }

        if (stat != null) {
            result.setStat(stat);
        }

        return result;
    }

    private Movie getMovieById(final String id, final String moviesTableName) {
        final var moviesConfig = DynamoDBMapperConfig.builder()
                .withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(moviesTableName))
                .withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT)
                .build();

        return this.mapper.load(Movie.class, id, moviesConfig);
    }

    private Stat getStatById(final String id, final String statsTableName) {
        final var statsConfig = DynamoDBMapperConfig.builder()
                .withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(statsTableName))
                .withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT)
                .build();

        return this.mapper.load(Stat.class, id, statsConfig);
    }
}
