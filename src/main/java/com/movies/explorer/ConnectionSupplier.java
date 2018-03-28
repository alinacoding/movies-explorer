package com.movies.explorer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Supplier;

public class ConnectionSupplier implements Supplier<Connection> {

    @Override
    public Connection get() {
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            return DriverManager.getConnection("jdbc:hsqldb:mem:aname", "sa", "");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
