package de.mbe.tutorials.aws.serverless.moviesstats.addmovies.repositories;

import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.Movie;

import java.util.List;

public interface MoviesStatsRepository {

    long saveMovies(final List<Movie> movies, final String moviesTableName);
}