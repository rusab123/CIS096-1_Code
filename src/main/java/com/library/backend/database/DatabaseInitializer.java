package com.library.backend.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Handles database initialization and schema creation
 */
public class DatabaseInitializer {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "library_management_cc";
    private static final String USER = "root"; // Change to your MySQL username
    private static final String PASS = "9744378819"; // Change to your MySQL password

    /**
     * Initializes the database when the application starts
     */
    public static void initialize() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement()) {

            // Create database if it doesn't exist
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);

            // Use the database
            stmt.executeUpdate("USE " + DB_NAME);

            // Create tables
            createTables(conn);

            System.out.println("Database initialized successfully");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // Users table (all roles now in one table)
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " + // Set id to auto-increment
                            "name VARCHAR(100) NOT NULL, " +
                            "email VARCHAR(100) NOT NULL UNIQUE, " +
                            "password VARCHAR(100) NOT NULL, " +
                            "user_type ENUM('STUDENT', 'TEACHER', 'ADMIN') NOT NULL, " +
                            "student_id VARCHAR(20), " +  // For STUDENT type
                            "department VARCHAR(100), " +  // For STUDENT and TEACHER types
                            "teacher_id VARCHAR(20), " +   // For TEACHER type
                            "designation VARCHAR(100), " + // For TEACHER type
                            "admin_id VARCHAR(20), " +     // For ADMIN type
                            "role VARCHAR(100) " +         // For ADMIN type
                            ")"
            );

            // Books table
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS books (" +
                            "id VARCHAR(36) PRIMARY KEY, " +
                            "title VARCHAR(255) NOT NULL, " +
                            "author VARCHAR(100) NOT NULL, " +
                            "isbn VARCHAR(20) NOT NULL UNIQUE, " +
                            "category VARCHAR(100) NOT NULL, " +
                            "quantity INT NOT NULL, " +
                            "available BOOLEAN NOT NULL" +
                            ")"
            );

            // Borrow records table
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS borrow_records (" +
                            "id VARCHAR(36) PRIMARY KEY, " +
                            "book_id VARCHAR(36) NOT NULL, " +
                            "user_id INT NOT NULL, " +  // Change user_id type to INT
                            "borrow_date DATE NOT NULL, " +
                            "due_date DATE NOT NULL, " +
                            "return_date DATE, " +
                            "returned BOOLEAN NOT NULL DEFAULT FALSE, " +
                            "fine DOUBLE NOT NULL DEFAULT 0.0, " +
                            "FOREIGN KEY (book_id) REFERENCES books(id), " +
                            "FOREIGN KEY (user_id) REFERENCES users(id)" + // Match data types for the foreign key constraint
                            ")"
            );
        }
    }
}
