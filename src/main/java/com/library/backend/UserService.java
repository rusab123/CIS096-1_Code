package com.library.backend;

import com.library.backend.database.DatabaseInitializer;
import com.library.backend.database.DbConnection;
import com.library.models.Admin;
import com.library.models.Student;
import com.library.models.Teacher;
import com.library.models.User;

import java.sql.*;
import java.util.*;

public class UserService {
    private static UserService instance;
    private static User loggedInUser;

    private UserService() {
        DatabaseInitializer.initialize();
    }

    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public User addUser(User user) {
        String sql = "INSERT INTO users(name, email, password, user_type, student_id, department, teacher_id, designation, admin_id, role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getUserType());

            if (user instanceof Student student) {
                pstmt.setString(5, student.getStudentId());
                pstmt.setString(6, student.getDepartment());
                pstmt.setNull(7, Types.VARCHAR);
                pstmt.setNull(8, Types.VARCHAR);
                pstmt.setNull(9, Types.VARCHAR);
                pstmt.setNull(10, Types.VARCHAR);
            } else if (user instanceof Teacher teacher) {
                pstmt.setNull(5, Types.VARCHAR);
                pstmt.setString(6, teacher.getDepartment());
                pstmt.setString(7, teacher.getTeacherId());
                pstmt.setString(8, teacher.getDesignation());
                pstmt.setNull(9, Types.VARCHAR);
                pstmt.setNull(10, Types.VARCHAR);
            } else if (user instanceof Admin admin) {
                pstmt.setNull(5, Types.VARCHAR);
                pstmt.setNull(6, Types.VARCHAR);
                pstmt.setNull(7, Types.VARCHAR);
                pstmt.setNull(8, Types.VARCHAR);
                pstmt.setString(9, admin.getAdminId());
                pstmt.setString(10, admin.getRole());
            }

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                user.setId(String.valueOf(rs.getInt(1)));
            }

            return user;
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            return null;
        }
    }

    public Optional<User> getUserById(String id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.ofNullable(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user by ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<User> login(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("password");

                if (password.equals(dbPassword)) {
                    return Optional.ofNullable(mapResultSetToUser(rs));
                } else {
                    System.err.println("Incorrect password.");
                }
            } else {
                System.err.println("No user found with the provided email.");
            }
        } catch (SQLException e) {
            System.err.println("Error logging in: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = mapResultSetToUser(rs);
                if (user != null) {
                    users.add(user);
                } else {
                    System.err.println("Warning: Skipping user due to mapping failure (ID: " + rs.getString("id") + ")");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all users: " + e.getMessage());
        }

        return users;
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE users SET name=?, email=?, password=?, user_type=?, student_id=?, department=?, teacher_id=?, designation=?, admin_id=?, role=? WHERE id=?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getUserType());

            if (user instanceof Student student) {
                pstmt.setString(5, student.getStudentId());
                pstmt.setString(6, student.getDepartment());
                pstmt.setNull(7, Types.VARCHAR);
                pstmt.setNull(8, Types.VARCHAR);
                pstmt.setNull(9, Types.VARCHAR);
                pstmt.setNull(10, Types.VARCHAR);
            } else if (user instanceof Teacher teacher) {
                pstmt.setNull(5, Types.VARCHAR);
                pstmt.setString(6, teacher.getDepartment());
                pstmt.setString(7, teacher.getTeacherId());
                pstmt.setString(8, teacher.getDesignation());
                pstmt.setNull(9, Types.VARCHAR);
                pstmt.setNull(10, Types.VARCHAR);
            } else if (user instanceof Admin admin) {
                pstmt.setNull(5, Types.VARCHAR);
                pstmt.setNull(6, Types.VARCHAR);
                pstmt.setNull(7, Types.VARCHAR);
                pstmt.setNull(8, Types.VARCHAR);
                pstmt.setString(9, admin.getAdminId());
                pstmt.setString(10, admin.getRole());
            }

            pstmt.setString(11, user.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteUser(String id) {
        String sql = "DELETE FROM users WHERE id=?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String password = rs.getString("password");
        String userType = rs.getString("user_type");

        switch (userType.toUpperCase()) {
            case "STUDENT":
                return new Student(id, name, email, password,
                        rs.getString("student_id"), rs.getString("department"));
            case "TEACHER":
                return new Teacher(id, name, email, password,
                        rs.getString("teacher_id"), rs.getString("department"),
                        rs.getString("designation"));
            case "ADMIN":
                return new Admin(id, name, email, password,
                        rs.getString("admin_id"), rs.getString("role"));
            default:
                System.err.println("Unknown user type: " + userType);
                return null;
        }
    }

    public static String getLoggedInUserId() {
        if (loggedInUser != null) {
            return loggedInUser.getId();  // Assuming User has a getId() method.
        } else {
            return null;  // Or throw an exception if no user is logged in
        }
    }

    public static void setLoggedInUser(User user) {
        loggedInUser = user;  // Set the logged-in user
    }
}
