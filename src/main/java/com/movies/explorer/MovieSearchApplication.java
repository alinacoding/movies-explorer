package com.movies.explorer;

import com.squareup.okhttp.OkHttpClient;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;


public class MovieSearchApplication extends Application<Configuration> {

    private static final Logger log = LoggerFactory.getLogger(MovieSearchApplication.class);
    private final OkHttpClient okHttpClient = new OkHttpClient();

    public static void main(String[] args) throws Exception {
        new MovieSearchApplication().run(args);
    }

    @Override
    public void run(Configuration configuration, Environment environment) {
        environment.jersey().register(movieSearchResource());
    }

    private MovieSearchResource movieSearchResource() {
        try {
            int year = 2018;
            List<MovieData> movies = WikipediaParser.getMoviesForYear(year);
            Connection connection = MovieDatabase.populateDatabase(year, movies);
            return new MovieSearchResource(okHttpClient, connection);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
