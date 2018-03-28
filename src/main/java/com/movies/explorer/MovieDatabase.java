package com.movies.explorer;

import java.sql.*;
import java.util.List;
import java.util.function.Supplier;

public class MovieDatabase {

    private static final String CREATE_TABLE = "CREATE TABLE moviedb (" +
            "title NVARCHAR(100) NOT NULL, " +
            "year INT NOT NULL, " +
            "companies NVARCHAR(50) ARRAY NOT NULL, " +
            "directors NVARCHAR(50) ARRAY NOT NULL, " +
            "screenwriters NVARCHAR(50) ARRAY NOT NULL, " +
            "actors NVARCHAR(50) ARRAY NOT NULL, " +
            "genres NVARCHAR(20) ARRAY NOT NULL, " +
            "countries NVARCHAR(20) ARRAY NOT NULL, " +
            "PRIMARY KEY(title)" +
            ");";

    private static final String INSERT_RECORD = "INSERT INTO moviedb VALUES (?,?,?,?,?,?,?,?)";

    private final Supplier<Connection> connectionSupplier;

    public MovieDatabase(Supplier<Connection> connectionSupplier) {
        this.connectionSupplier = connectionSupplier;
    }

    public void populateDatabase(List<MovieData> movies) {
        createTable();
        insertMovieData(movies);
    }

    private void insertMovieData(List<MovieData> movies) {
        try (Connection connection = connectionSupplier.get();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_RECORD)) {
            movies.forEach(movie -> insertRow(connection, preparedStatement, movie));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void createTable() {
        try (Connection connection = connectionSupplier.get();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_TABLE);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void insertRow(Connection connection, PreparedStatement preparedStatement, MovieData movie) {
        try {
            preparedStatement.setString(1, movie.title());
            preparedStatement.setInt(2, movie.year());
            preparedStatement.setArray(3, sqlArrayOf(connection, movie.companies()));
            preparedStatement.setArray(4, sqlArrayOf(connection, movie.peopleRoles().directors()));
            preparedStatement.setArray(5, sqlArrayOf(connection, movie.peopleRoles().screenwriters()));
            preparedStatement.setArray(6, sqlArrayOf(connection, movie.peopleRoles().actors()));
            preparedStatement.setArray(7, sqlArrayOf(connection, movie.genres()));
            preparedStatement.setArray(8, sqlArrayOf(connection, movie.countries()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public MovieSearchResult queryDatabase(MovieSearch movieSearch) {
        String QUERY = "SELECT * FROM moviedb WHERE year BETWEEN ? and ?;";
        try (Connection connection = connectionSupplier.get();
             PreparedStatement preparedStatement = connection.prepareStatement(QUERY)) {
            preparedStatement.setInt(1, movieSearch.fromYear().get());
            preparedStatement.setInt(2, movieSearch.toYear().get());
            ResultSet resultSet = preparedStatement.executeQuery();
            MovieSearchResult.Builder searchResult = MovieSearchResult.builder();

            while (resultSet.next()) {
                MovieData movie = MovieData.builder()
                        .title(resultSet.getString("title"))
                        .year(resultSet.getInt("year"))
                        .peopleRoles(PeopleRoles.builder().build())
                        .build();

                searchResult.addMovies(movie);
                System.out.println(resultSet.getString("title") + " " + resultSet.getInt("year"));
            }
            return searchResult.build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Array sqlArrayOf(Connection connection, List<String> list) {
        Object[] array = list.toArray(new Object[0]);
        try {
            return connection.createArrayOf("NVARCHAR", array);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}