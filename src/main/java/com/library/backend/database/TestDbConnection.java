package com.library.backend.database;

import java.sql.Connection;
import java.sql.SQLException;

public class TestDbConnection {

    public static void main(String[] args) {
        try {
            Connection connection = DbConnection.getConnection();
            System.out.println("Connected to the database!");
            DbConnection.closeConnection();
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
    }
}
