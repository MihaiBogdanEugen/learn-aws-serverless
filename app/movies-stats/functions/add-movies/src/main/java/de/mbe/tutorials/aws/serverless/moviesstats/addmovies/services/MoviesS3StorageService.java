package de.mbe.tutorials.aws.serverless.moviesstats.addmovies.services;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.Movie;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.convertors.LocalDateConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public final class MoviesS3StorageService implements MoviesStorageService {

    private final AmazonS3 s3;

    public MoviesS3StorageService() {
        this.s3 = AmazonS3ClientBuilder
                .standard()
                .build();
    }

    @Override
    public List<Movie> getMovies(final S3Event s3Event) throws IOException {

        final List<Movie> movies = new ArrayList<>();

        for (final S3EventNotification.S3EventNotificationRecord record : s3Event.getRecords()) {

            final String bucket = record.getS3().getBucket().getName();
            final String key = record.getS3().getObject().getUrlDecodedKey();
            final S3Object s3Object = this.s3.getObject(bucket, key);

            String line;

            try (final S3ObjectInputStream inputStream = s3Object.getObjectContent()) {
                try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                    while ((line = bufferedReader.readLine()) != null) {
                        final String[] parts = line.split(",");
                        movies.add(new Movie(
                                parts[0],
                                parts[1],
                                parts[2],
                                LocalDateConverter.parseString(parts[3]),
                                Double.parseDouble(parts[4])));
                    }
                }
            }
        }

        return movies;
    }
}
