package de.mbe.tutorials.aws.serverless.moviesstats.addmovies.services;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.Movie;

import java.io.IOException;
import java.util.List;

public interface MoviesStorageService {

    List<Movie> getMovies(final S3Event s3Event, final String moviesBucketName) throws IOException;
}
