package com.movies.explorer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.List;

@Path("/movie-search")
public class MovieSearchResource {

    private static final Logger log = LoggerFactory.getLogger(MovieSearchResource.class);

    private final OkHttpClient okHttpClient;
    private final Connection connection;

    public MovieSearchResource(OkHttpClient okHttpClient, Connection connection) {
        this.okHttpClient = okHttpClient;
        this.connection = connection;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String getMovies(
            @PathParam("fromYear") int fromYear,
            @PathParam("toYear") int toYear,
            @PathParam("company") String company,
            @PathParam("director") String director,
            @PathParam("actor") String actor,
            @PathParam("screenwriter") String screenwriter,
            @PathParam("country") String country
            ) {
        System.out.println("Received: " + fromYear + " " + toYear);

        String SEARCH_DB = "SELECT * FROM moviedb WHERE year BETWEEN ? and ?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SEARCH_DB);
            preparedStatement.setInt(1, fromYear);
            preparedStatement.setInt(2, toYear);
            preparedStatement.executeQuery();

            Statement statement = connection.createStatement();
            ResultSet resSet = statement.executeQuery("SELECT * FROM moviedb");
            while (resSet.next()) {
                System.out.println(resSet.getString("title") + " " + resSet.getInt("year"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

       return "Endpoint works";
    }

}
