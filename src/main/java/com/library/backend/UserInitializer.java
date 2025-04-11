package com.library.backend;

import com.library.backend.database.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserInitializer {
    private static final String ADMIN_EMAIL = "admin@library.com";
    private static final String ADMIN_PASSWORD = "admin123"; // Plain-text password
    private static final String ADMIN_NAME = "System Admin";
    private static final String ADMIN_USER_TYPE = "ADMIN";

    private static final String TEACHER_EMAIL = "teacher@library.com";
    private static final String TEACHER_PASSWORD = "teacher123"; // Plain-text password
    private static final String TEACHER_NAME = "Default Teacher";
    private static final String TEACHER_USER_TYPE = "TEACHER";
    private static final String TEACHER_ID = "T001";

    private static final String STUDENT_EMAIL = "student@library.com";
    private static final String STUDENT_PASSWORD = "student123"; // Plain-text password
    private static final String STUDENT_NAME = "Default Student";
    private static final String STUDENT_USER_TYPE = "STUDENT";
    private static final String STUDENT_ID = "S001";

    public static void main(String[] args) {
        initializeUsers();
    }

    public static void initializeUsers() {
        if (!adminExists()) {
            insertAdminIntoDatabase();
            System.out.println("Default admin account created");
        } else {
            System.out.println("Admin account already exists");
        }

        if (!teacherExists()) {
            insertTeacherIntoDatabase();
            System.out.println("Default teacher account created");
        } else {
            System.out.println("Teacher account already exists");
        }

        if (!studentExists()) {
            insertStudentIntoDatabase();
            System.out.println("Default student account created");
        } else {
            System.out.println("Student account already exists");
        }
    }

    private static boolean adminExists() {
        String sql = "SELECT id FROM users WHERE email = ? AND user_type = 'ADMIN'";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ADMIN_EMAIL);
            ResultSet rs = pstmt.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error checking admin existence: " + e.getMessage());
            return false;
        }
    }

    private static boolean teacherExists() {
        String sql = "SELECT id FROM users WHERE email = ? AND user_type = 'TEACHER'";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, TEACHER_EMAIL);
            ResultSet rs = pstmt.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error checking teacher existence: " + e.getMessage());
            return false;
        }
    }

    private static boolean studentExists() {
        String sql = "SELECT id FROM users WHERE email = ? AND user_type = 'STUDENT'";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, STUDENT_EMAIL);
            ResultSet rs = pstmt.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error checking student existence: " + e.getMessage());
            return false;
        }
    }

    private static void insertAdminIntoDatabase() {
        String sql = "INSERT INTO users (name, email, password, user_type) VALUES (?, ?, ?, ?)";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ADMIN_NAME);
            pstmt.setString(2, ADMIN_EMAIL);
            pstmt.setString(3, ADMIN_PASSWORD); // Plain-text password
            pstmt.setString(4, ADMIN_USER_TYPE);

            pstmt.executeUpdate();
            System.out.println("Admin inserted successfully");
        } catch (SQLException e) {
            System.err.println("Error inserting admin into database: " + e.getMessage());
        }
    }

    private static void insertTeacherIntoDatabase() {
        String sql = "INSERT INTO users (name, email, password, user_type, teacher_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, TEACHER_NAME);
            pstmt.setString(2, TEACHER_EMAIL);
            pstmt.setString(3, TEACHER_PASSWORD); // Plain-text password
            pstmt.setString(4, TEACHER_USER_TYPE);
            pstmt.setString(5, TEACHER_ID);

            pstmt.executeUpdate();
            System.out.println("Teacher inserted successfully");
        } catch (SQLException e) {
            System.err.println("Error inserting teacher into database: " + e.getMessage());
        }
    }

    private static void insertStudentIntoDatabase() {
        String sql = "INSERT INTO users (name, email, password, user_type, student_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, STUDENT_NAME);
            pstmt.setString(2, STUDENT_EMAIL);
            pstmt.setString(3, STUDENT_PASSWORD); // Plain-text password
            pstmt.setString(4, STUDENT_USER_TYPE);
            pstmt.setString(5, STUDENT_ID);

            pstmt.executeUpdate();
            System.out.println("Student inserted successfully");
        } catch (SQLException e) {
            System.err.println("Error inserting student into database: " + e.getMessage());
        }
    }
}
