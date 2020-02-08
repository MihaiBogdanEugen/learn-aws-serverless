package de.mbe.tutorials.aws.serverless.moviesstatsapp.getmovieandstatsvc.repositories;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.Movie;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.MovieAndStat;
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
    public MovieAndStat getById(final String id) {

        final var result = new MovieAndStat();

        final var movie = this.mapper.load(Movie.class, id, DynamoDBMapperConfig.ConsistentReads.CONSISTENT.config());
        if (movie != null) {
            result.setMovie(movie);
        }

        final var stat = this.mapper.load(Stat.class, id, DynamoDBMapperConfig.ConsistentReads.CONSISTENT.config());
        if (stat != null) {
            result.setStat(stat);
        }

        result.setId(id);
        return result;
    }
}
