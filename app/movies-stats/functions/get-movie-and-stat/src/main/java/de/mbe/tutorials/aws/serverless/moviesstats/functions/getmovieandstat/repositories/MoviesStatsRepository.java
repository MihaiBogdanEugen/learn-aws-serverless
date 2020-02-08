package de.mbe.tutorials.aws.serverless.moviesstats.functions.getmovieandstat.repositories;

import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.MovieAndStat;

public interface MoviesStatsRepository {

    MovieAndStat getById(final String id, final String moviesTableName, final String statsTableName);
}
