package com.movies.explorer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.function.Supplier;

public class ConnectionSupplier implements Supplier<Connection> {

    @Override
    public Connection get() {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/moviedb", "alina", "12345");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
