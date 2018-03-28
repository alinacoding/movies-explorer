package com.movies.explorer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import com.squareup.okhttp.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;

@Path("/movie-search")
public class MovieSearchResource {

    private static final Logger log = LoggerFactory.getLogger(MovieSearchResource.class);

    private final OkHttpClient okHttpClient;

    public MovieSearchResource(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String getMovies(MovieSearch movieSearch) {
        System.out.println("Received: " + movieSearch.fromYear().get() + " " + movieSearch.toYear().get());

        String SEARCH_DB = "SELECT * FROM moviedb WHERE year BETWEEN ? and ?;";
        try {
            Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:aname", "sa", "");
            PreparedStatement preparedStatement = connection.prepareStatement(SEARCH_DB);
            preparedStatement.setInt(1, movieSearch.fromYear().get());
            preparedStatement.setInt(2, movieSearch.toYear().get());
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
