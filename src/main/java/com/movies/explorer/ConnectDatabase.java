package com.movies.explorer;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectDatabase {

    public static void main(String[] args) {
        Connection connection = null;
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            connection = DriverManager.getConnection("jdbc:hsqldb:mem:aname", "sa", "");
            if (connection != null){
                System.out.println("Connection created successfully.");
            } else {
                System.out.println("Unable to create connection.");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

}
