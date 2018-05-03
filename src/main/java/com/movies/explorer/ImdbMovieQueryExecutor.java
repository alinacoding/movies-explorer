package com.movies.explorer;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.google.common.collect.Sets;

public class ImdbMovieQueryExecutor {

    private static final String QUERY_GENRES = "SELECT genres FROM imdb WHERE tconst = ? ;";

    private static final String QUERY_ACTORS = "SELECT ARRAY_AGG(primaryName) AS names FROM actors_names "
            + "WHERE nconst IN (SELECT nconst FROM actors_ids WHERE tconst = ? AND (category = 'actor' "
            + "OR category = 'actress'));";

    private final Supplier<Connection> connectionSupplier;
    private final String imdbMovieId;

    public ImdbMovieQueryExecutor(Supplier<Connection> connectionSupplier, String imdbMovieId) {
        this.connectionSupplier = connectionSupplier;
        this.imdbMovieId = imdbMovieId;
    }

    public Set<String> getGenres() {
        try (Connection connection = connectionSupplier.get();
                PreparedStatement preparedStatement = connection.prepareStatement(QUERY_GENRES)) {
            preparedStatement.setString(1, imdbMovieId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String genres = resultSet.getString("genres");
                return Sets.newHashSet(genres.split(","));
            }
            return emptySet();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<String> getActors() {
        try (Connection connection = connectionSupplier.get();
                PreparedStatement preparedStatement = connection.prepareStatement(QUERY_ACTORS)) {
            preparedStatement.setString(1, imdbMovieId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return sqlArrayToSet(resultSet.getArray("names"));
            }
            return emptySet();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Set<String> sqlArrayToSet(Array sqlArray) {
        try {
            if (sqlArray == null) {
                return emptySet();
            }
            Object[] strArray = (Object[]) sqlArray.getArray();
            return Stream.of(strArray).map(Object::toString).collect(toSet());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
