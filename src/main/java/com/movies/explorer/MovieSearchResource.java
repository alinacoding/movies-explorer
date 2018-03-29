package com.movies.explorer;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/movie-search")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MovieSearchResource {

    private final MovieDatabase movieDatabase;

    public MovieSearchResource(MovieDatabase movieDatabase) {
        this.movieDatabase = movieDatabase;
    }

    @POST
    public MovieSearchResult getMovies(MovieSearch movieSearch) {
        return movieDatabase.queryDatabase(movieSearch);
    }

}
