package de.mbe.tutorials.aws.serverless.moviesstats.functions.addmovies;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.Movie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class UploadFromS3ToDynamoDBService {

    private final AmazonS3 s3Client;
    private final DynamoDBMapper mapper;
    private final String moviesTable;
    private final String moviesBucket;

    public UploadFromS3ToDynamoDBService(final AmazonS3 s3Client, final String moviesBucket, final AmazonDynamoDB dynamoDB, final String moviesTable) {
        this.s3Client = s3Client;
        this.mapper = new DynamoDBMapper(dynamoDB);
        this.moviesTable = moviesTable;
        this.moviesBucket = moviesBucket;
    }

    public void uploadMovies(final S3Event s3Event) throws IOException, AmazonS3Exception, AmazonDynamoDBException {

        final var movies = new ArrayList<Movie>();

        for (final var record : s3Event.getRecords()) {

            final var s3Entity = record.getS3();

            final var bucketName = s3Entity.getBucket().getName();
            if (!bucketName.equalsIgnoreCase(this.moviesBucket)) {
                continue;
            }

            final var key = s3Entity.getObject().getUrlDecodedKey();

            final var s3Object = this.s3Client.getObject(bucketName, key);

            String line;

            try (final var inputStream = s3Object.getObjectContent()) {
                try (final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                    while ((line = bufferedReader.readLine()) != null) {
                        final var parts = line.split(",");
                        movies.add(new Movie(
                                parts[0],
                                parts[1],
                                parts[2],
                                parts[3],
                                Double.parseDouble(parts[4])));

                        if (movies.size() == 25) {
                            dumpMovies(movies);
                            movies.clear();
                        }
                    }
                }
            }
        }

        dumpMovies(movies);
    }

    private void dumpMovies(final List<Movie> movies) {
        if (movies.isEmpty()) {
            return;
        }

        this.mapper.batchWrite(movies, Collections.emptyList(), DynamoDBMapperConfig.builder()
                .withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(this.moviesTable))
                .withSaveBehavior(DynamoDBMapperConfig.SaveBehavior.PUT)
                .build());
    }
}
