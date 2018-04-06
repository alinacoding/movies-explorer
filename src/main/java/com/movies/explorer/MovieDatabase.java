package com.movies.explorer;

import static java.util.stream.Collectors.toSet;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class MovieDatabase {

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS movies;";

    private static final String ENABLE_CITEXT = "CREATE EXTENSION IF NOT EXISTS citext WITH SCHEMA public;";

    private static final String CREATE_TABLE = "CREATE TABLE movies (" +
            "title CITEXT NOT NULL, " +
            "year INTEGER NOT NULL, " +
            "companies CITEXT[], " +
            "directors CITEXT[], " +
            "screenwriters CITEXT[], " +
            "actors CITEXT[], " +
            "genres CITEXT[], " +
            "countries CITEXT[], " +
            "PRIMARY KEY(title, year)" +
            ");";

    private static final String INSERT_RECORD = "INSERT INTO movies VALUES (?,?,?,?,?,?,?,?)";

    private final Supplier<Connection> connectionSupplier;

    public MovieDatabase(Supplier<Connection> connectionSupplier) {
        this.connectionSupplier = connectionSupplier;
    }

    public void populateDatabase(Set<MovieData> movies) {
        createTable();
        insertMovieData(movies);
    }

    private void insertMovieData(Set<MovieData> movies) {
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
            statement.executeUpdate(DROP_TABLE);
            statement.executeUpdate(ENABLE_CITEXT);
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

    private String partialSearchInArrayQuery(String field) {
        return "(EXISTS (SELECT * FROM UNNEST(" + field + ") "
                + "AS unnested(item) WHERE unnested.item like '%' || ? || '%')) AND ";
    }

    private String buildQueryFromSearchFields(MovieSearch movieSearch) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM movies WHERE ");

        movieSearch.title().ifPresent(title -> {
            query.append("title LIKE '%' || ? || '%' AND ");
        });

        movieSearch.fromYear().ifPresent(fromYear -> {
            query.append("year >= ? AND ");
        });

        movieSearch.toYear().ifPresent(toYear -> {
            query.append("year <= ? AND ");
        });

        movieSearch.company().ifPresent(company -> {
            query.append(partialSearchInArrayQuery("companies"));
        });

        movieSearch.director().ifPresent(director -> {
            query.append(partialSearchInArrayQuery("directors"));
        });

        movieSearch.screenwriter().ifPresent(screenwriter -> {
            query.append(partialSearchInArrayQuery("screenwriters"));
        });

        movieSearch.actor().ifPresent(actor -> {
            query.append(partialSearchInArrayQuery("actors"));
        });

        movieSearch.genre().ifPresent(genre -> {
            query.append(partialSearchInArrayQuery("genres"));
        });

        movieSearch.country().ifPresent(country -> {
            query.append(partialSearchInArrayQuery("countries"));
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

    private static Set<String> sqlArrayToList(Array sqlArray) {
        try {
            Object[] strArray = (Object[]) sqlArray.getArray();
            return Stream.of(strArray).map(Object::toString).collect(toSet());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Array sqlArrayOf(Connection connection, Collection<String> collection) {
        Object[] array = collection.toArray(new Object[0]);
        try {
            return connection.createArrayOf("VARCHAR", array);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}