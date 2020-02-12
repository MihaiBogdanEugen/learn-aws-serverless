package de.mbe.tutorials.aws.serverless.moviesstats.functions.getmovieandstat;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.Movie;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.MovieAndStat;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.Stat;

public final class DynamoDBRepository {

    private final DynamoDBMapper mapper;
    private final String moviesTable;
    private final String statsTable;

    public DynamoDBRepository(final AmazonDynamoDB dynamoDB, final String moviesTable, final String statsTable) {
        this.mapper = new DynamoDBMapper(dynamoDB);
        this.moviesTable = moviesTable;
        this.statsTable = statsTable;
    }

    public MovieAndStat getById(final String id) throws AmazonDynamoDBException {

        final var movie = this.mapper.load(Movie.class, id, DynamoDBMapperConfig.builder()
                .withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(this.moviesTable))
                .withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT)
                .build());

        final var stat = this.mapper.load(Stat.class, id, DynamoDBMapperConfig.builder()
                .withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(this.statsTable))
                .withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT)
                .build());

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
}
