package com.library.backend.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages database connections
 */
public class DbConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/library_management_cc";
    private static final String USER = "root"; // Change to your MySQL username
    private static final String PASS = "9744378819"; // Change to your MySQL password

    private static Connection connection;

    /**
     * Gets a database connection (creates one if it doesn't exist)
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        }
        return connection;
    }

    /**
     * Closes the database connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}