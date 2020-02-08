package de.mbe.tutorials.aws.serverless.moviesstatsapp.getmovieandstatsvc;

import com.google.inject.AbstractModule;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.getmovieandstatsvc.repositories.MoviesStatsDynamoDBRepository;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.getmovieandstatsvc.repositories.MoviesStatsRepository;

public final class GuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MoviesStatsRepository.class).to(MoviesStatsDynamoDBRepository.class);
    }
}
