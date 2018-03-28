package com.movies.explorer;

import java.io.IOException;
import java.sql.*;
import java.util.List;

public class MovieDatabase {

    public static void main(String[] args) throws IOException {

        int year = 2018;
        List<MovieData> movies = WikipediaParser.getMoviesForYear(year);
        System.out.println(movies.size());
        Connection connection = populateDatabase(year, movies);
    }

    public static Connection populateDatabase(int year, List<MovieData> movies) {
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:aname", "sa", "");
            Statement statement = connection.createStatement();

            String CREATE_DB = "CREATE TABLE moviedb (" +
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
            statement.executeUpdate(CREATE_DB);

            String INSERT_RECORD = "INSERT INTO moviedb VALUES (?,?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_RECORD);

            movies.forEach(movie -> {
                try {
                    preparedStatement.setString(1, movie.title());
                    preparedStatement.setInt(2, year);
                    preparedStatement.setArray(3, sqlArrayOf(connection, movie.companies()));
                    preparedStatement.setArray(4, sqlArrayOf(connection, movie.peopleRoles().directors()));
                    preparedStatement.setArray(5, sqlArrayOf(connection, movie.peopleRoles().screenwriters()));
                    preparedStatement.setArray(6, sqlArrayOf(connection, movie.peopleRoles().actors()));
                    preparedStatement.setArray(7, sqlArrayOf(connection, movie.genres()));
                    preparedStatement.setArray(8, sqlArrayOf(connection, movie.countries()));
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            return connection;

        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        return null;
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