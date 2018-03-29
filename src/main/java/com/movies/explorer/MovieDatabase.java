package com.movies.explorer;

import static java.util.stream.Collectors.toList;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

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

    private String buildQueryFromSearchFields(MovieSearch movieSearch) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM moviedb WHERE ");

        movieSearch.title().ifPresent(title -> {
            query.append("title = ? AND ");
        });

        movieSearch.fromYear().ifPresent(fromYear -> {
            query.append("year >= ? AND ");
        });

        movieSearch.toYear().ifPresent(toYear -> {
            query.append("year <= ? AND ");
        });

        movieSearch.company().ifPresent(company -> {
            query.append("POSITION_ARRAY(? IN companies) > 0 AND ");
        });

        movieSearch.director().ifPresent(director -> {
            query.append("POSITION_ARRAY(? IN directors) > 0 AND ");
        });

        movieSearch.screenwriter().ifPresent(screenwriter -> {
            query.append("POSITION_ARRAY(? IN screenwriters) > 0 AND ");
        });

        movieSearch.actor().ifPresent(actor -> {
            query.append("POSITION_ARRAY(? IN actors) > 0 AND ");
        });

        movieSearch.genre().ifPresent(genre -> {
            query.append("POSITION_ARRAY(? IN genres) > 0 AND ");
        });

        movieSearch.country().ifPresent(country -> {
            query.append("POSITION_ARRAY(? IN countries) > 0 AND ");
        });
        query.append("TRUE;");

        return query.toString();

    }

    public MovieSearchResult queryDatabase(MovieSearch movieSearch) {
        String query = buildQueryFromSearchFields(movieSearch);
        try (Connection connection = connectionSupplier.get();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            int index = 1;
            if (movieSearch.title().isPresent()) {
                preparedStatement.setString(index++, movieSearch.title().get());
            }

            if (movieSearch.fromYear().isPresent()) {
                preparedStatement.setInt(index++, movieSearch.fromYear().get());
            }

            if (movieSearch.toYear().isPresent()) {
                preparedStatement.setInt(index++, movieSearch.toYear().get());
            }

            if (movieSearch.company().isPresent()) {
                preparedStatement.setString(index++, movieSearch.company().get());
            }

            if (movieSearch.director().isPresent()) {
                preparedStatement.setString(index++, movieSearch.director().get());
            }

            if (movieSearch.screenwriter().isPresent()) {
                preparedStatement.setString(index++, movieSearch.screenwriter().get());
            }

            if (movieSearch.actor().isPresent()) {
                preparedStatement.setString(index++, movieSearch.actor().get());
            }

            if (movieSearch.genre().isPresent()) {
                preparedStatement.setString(index++, movieSearch.genre().get());
            }

            if (movieSearch.country().isPresent()) {
                preparedStatement.setString(index++, movieSearch.country().get());
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            MovieSearchResult.Builder searchResult = MovieSearchResult.builder();

            while (resultSet.next()) {
                MovieData movie = getMovie(resultSet);
                searchResult.addMovies(movie);
            }
            return searchResult.build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private MovieData getMovie(ResultSet resultSet) {
        try {
            return MovieData.builder()
                    .title(resultSet.getString("title"))
                    .year(resultSet.getInt("year"))
                    .peopleRoles(PeopleRoles.builder()
                            .directors(sqlArrayToList(resultSet.getArray("directors")))
                            .screenwriters(sqlArrayToList(resultSet.getArray("screenwriters")))
                            .actors(sqlArrayToList(resultSet.getArray("actors")))
                            .build())
                    .companies(sqlArrayToList(resultSet.getArray("companies")))
                    .genres(sqlArrayToList(resultSet.getArray("genres")))
                    .countries(sqlArrayToList(resultSet.getArray("countries")))
                    .build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> sqlArrayToList(Array sqlArray) {
        try {
            Object[] strArray = (Object[]) sqlArray.getArray();
            return Stream.of(strArray).map(Object::toString).collect(toList());
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