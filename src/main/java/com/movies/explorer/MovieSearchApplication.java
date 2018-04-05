package com.movies.explorer;

import java.io.IOException;
import java.sql.Connection;
import java.util.Set;
import java.util.function.Supplier;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;

public class MovieSearchApplication extends Application<Configuration> {

    public static void main(String[] args) throws Exception {
        new MovieSearchApplication().run(args);
    }

    @Override
    public void run(Configuration configuration, Environment environment) throws IOException {
        Supplier<Connection> connectionSupplier = new ConnectionSupplier();
        MovieDatabase movieDatabase = new MovieDatabase(connectionSupplier);
        Set<MovieData> movies = WikipediaParser.getMovies();
        movieDatabase.populateDatabase(movies);
        MovieSearchResource movieSearchResource = new MovieSearchResource(movieDatabase);
        environment.jersey().register(movieSearchResource);
    }
}
