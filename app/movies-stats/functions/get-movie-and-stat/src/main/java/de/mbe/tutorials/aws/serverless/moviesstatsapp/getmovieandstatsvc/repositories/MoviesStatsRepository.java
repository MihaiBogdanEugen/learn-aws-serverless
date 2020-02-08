package de.mbe.tutorials.aws.serverless.moviesstatsapp.getmovieandstatsvc.repositories;

import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.MovieAndStat;

public interface MoviesStatsRepository {

    MovieAndStat getById(final String id);
}
