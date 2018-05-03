package com.movies.explorer;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Supplier;

public class ImdbDatabase {

    private static final String GENRES_FILE = "imdbData/title.basics.tsv";
    private static final String RATINGS_FILE = "imdbData/title.ratings.tsv";
    private static final String ACTORS_IDS_FILE = GzipUtils.gzip("imdbData/title.principals.tsv.gz");
    private static final String ACTORS_NAMES_FILE = "imdbData/name.basics.tsv";

    private static final String DROP_IMDB_TABLE = "DROP TABLE IF EXISTS imdb;";

    private static final String DROP_RATINGS_TABLE = "DROP TABLE IF EXISTS ratings;";

    private static final String DROP_ACTORS_IDS_TABLE = "DROP TABLE IF EXISTS actors_ids;";

    private static final String DROP_ACTORS_NAMES_TABLE = "DROP TABLE IF EXISTS actors_names;";

    private static final String CREATE_IMDB_TABLE = "CREATE TABLE imdb (" +
            "tconst VARCHAR(10) NOT NULL, " +
            "titleType VARCHAR(20), " +
            "primaryTitle TEXT, " +
            "originalTitle TEXT, " +
            "isAdult BOOLEAN, " +
            "startYear INTEGER, " +
            "endYear INTEGER, " +
            "runtimeMinutes INTEGER, " +
            "genres TEXT, " +
            "PRIMARY KEY(tconst)" +
            ");";

    private static final String CREATE_RATINGS_TABLE = "CREATE TABLE ratings ( " +
            "tconst VARCHAR(10) NOT NULL, " +
            "averageRating NUMERIC, " +
            "numVotes INTEGER, " +
            "PRIMARY KEY(tconst)" +
            ");";

    private static final String CREATE_ACTORS_IDS_TABLE = "CREATE TABLE actors_ids ( " +
            "id SERIAL, " +
            "tconst TEXT, " +
            "ordering TEXT, " +
            "nconst TEXT, " +
            "category TEXT, " +
            "job TEXT, " +
            "characters TEXT, " +
            "PRIMARY KEY(id)" +
            ");";

    private static final String CREATE_ACTORS_NAMES_TABLE = "CREATE TABLE actors_names ( " +
            "nconst VARCHAR(10) NOT NULL, " +
            "primaryName TEXT, " +
            "birthYear INTEGER, " +
            "deathYear INTEGER, " +
            "primaryProfession TEXT, " +
            "knownForTitles TEXT, " +
            "PRIMARY KEY(nconst)" +
            ");";

    private static final String COPY_ACTORS_NAMES_TSV = "COPY actors_names(nconst, primaryName, " +
            "birthYear, deathYear, primaryProfession, knownForTitles) FROM '/home/alina/Desktop/movies-explorer/" +
            ACTORS_NAMES_FILE + "' DELIMITER '\t' QUOTE '|' NULL '\\N' CSV HEADER;";

    private static final String COPY_IMDB_TSV = "COPY imdb(tconst, titleType, primaryTitle, originalTitle, "
            + "isAdult, startYear, endYear, runtimeMinutes, genres) FROM '/home/alina/Desktop/movies-explorer/"
            + GENRES_FILE
            + "' DELIMITER '\t' QUOTE '|' NULL '\\N' CSV HEADER;";

    private static final String COPY_RATINGS_TSV = "COPY ratings(tconst, averageRating, numVotes) "
            + "FROM '/home/alina/Desktop/movies-explorer/" + RATINGS_FILE +
            "' DELIMITER '\t' NULL '\\N' CSV HEADER;";

    private static final String COPY_ACTORS_IDS_TSV = "COPY actors_ids(tconst, ordering, nconst, category, job, characters) "
            + "FROM '/home/alina/Desktop/movies-explorer/" + ACTORS_IDS_FILE
            + "' DELIMITER '\t' NULL '\\N' QUOTE '~' CSV HEADER;";

    private static final String REMOVE_ACTORS_NAMES_FIELDS = "ALTER TABLE actors_names "
            + "DROP COLUMN birthYear, "
            + "DROP COLUMN deathYear, "
            + "DROP COLUMN primaryProfession, "
            + "DROP COLUMN knownForTitles; ";

    private static final String REMOVE_IMDB_FIELDS = "ALTER TABLE imdb "
            + "DROP COLUMN titleType, "
            + "DROP COLUMN primaryTitle, "
            + "DROP COLUMN originalTitle, "
            + "DROP COLUMN isAdult, "
            + "DROP COLUMN startYear, "
            + "DROP COLUMN endYear;";

    private static final String JOIN_IMDB_RATINGS = "SELECT COALESCE(imdb.tconst, ratings.tconst) AS movieId, "
            + "imdb.runtimeMinutes, imdb.genres, ratings.averageRating, ratings.numVotes "
            + "FROM imdb FULL OUTER JOIN ratings ON imdb.tconst = ratings.tconst;";

    private final Supplier<Connection> connectionSupplier;

    public ImdbDatabase(Supplier<Connection> connectionSupplier) {
        this.connectionSupplier = connectionSupplier;
    }

    public void populateDatabases() {
        createTables();
    }

    private void createImdbTable(Statement statement) {
        try {
            statement.executeUpdate(DROP_IMDB_TABLE);
            statement.executeUpdate(CREATE_IMDB_TABLE);
            statement.executeUpdate(COPY_IMDB_TSV);
            System.out.println("Copied imdb tsv file");
            statement.executeUpdate(REMOVE_IMDB_FIELDS);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createRatingsTable(Statement statement) {
        try {
            statement.executeUpdate(DROP_RATINGS_TABLE);
            statement.executeUpdate(CREATE_RATINGS_TABLE);
            statement.executeUpdate(COPY_RATINGS_TSV);
            System.out.println("Copied ratings tsv file");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createActorsIdsTable(Statement statement) {
        try {
            statement.executeUpdate(DROP_ACTORS_IDS_TABLE);
            statement.executeUpdate(CREATE_ACTORS_IDS_TABLE);
            statement.executeUpdate(COPY_ACTORS_IDS_TSV);
            System.out.println("Copied actors_ids tsv file");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createActorsNamesTable(Statement statement) {
        try {
            statement.executeUpdate(DROP_ACTORS_NAMES_TABLE);
            statement.executeUpdate(CREATE_ACTORS_NAMES_TABLE);
            statement.executeUpdate(COPY_ACTORS_NAMES_TSV);
            System.out.println("Copied actors_names tsv file");
            statement.executeUpdate(REMOVE_ACTORS_NAMES_FIELDS);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createTables() {
        try (Connection connection = connectionSupplier.get();
                Statement statement = connection.createStatement()) {
            createImdbTable(statement);
            createRatingsTable(statement);
            createActorsIdsTable(statement);
            createActorsNamesTable(statement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
