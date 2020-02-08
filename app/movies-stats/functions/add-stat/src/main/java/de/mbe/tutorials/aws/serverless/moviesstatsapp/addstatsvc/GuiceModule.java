package de.mbe.tutorials.aws.serverless.moviesstatsapp.addstatsvc;

import com.google.inject.AbstractModule;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.addstatsvc.repositories.MoviesStatsDynamoDBRepository;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.addstatsvc.repositories.MoviesStatsRepository;

public final class GuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MoviesStatsRepository.class).to(MoviesStatsDynamoDBRepository.class);
    }
}
