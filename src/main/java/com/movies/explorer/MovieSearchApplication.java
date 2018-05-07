package com.movies.explorer;

import java.io.IOException;
import java.sql.Connection;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Supplier;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import org.eclipse.jetty.servlets.CrossOriginFilter;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;

public class MovieSearchApplication extends Application<Configuration> {

    public static void main(String[] args) throws Exception {
        new MovieSearchApplication().run(args);
    }

    @Override
    public void run(Configuration configuration, Environment environment) throws IOException, InterruptedException {
        final FilterRegistration.Dynamic cors = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        Supplier<Connection> connectionSupplier = new ConnectionSupplier();
        MovieDatabase movieDatabase = new MovieDatabase(connectionSupplier);
        int year = 2017;
        Set<MovieData> movies = WikipediaParser.getMovies(year);
        movieDatabase.populateDatabase(movies, year);
        MovieSearchResource movieSearchResource = new MovieSearchResource(movieDatabase);
        environment.jersey().register(movieSearchResource);
    }
}
