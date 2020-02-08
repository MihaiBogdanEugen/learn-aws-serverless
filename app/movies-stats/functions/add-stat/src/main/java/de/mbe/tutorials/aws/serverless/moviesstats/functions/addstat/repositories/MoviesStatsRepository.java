package de.mbe.tutorials.aws.serverless.moviesstats.functions.addstat.repositories;

import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.Stat;

public interface MoviesStatsRepository {

    void saveStat(final Stat stat, final String statsTableName);
}
