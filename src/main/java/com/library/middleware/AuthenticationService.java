package com.library.middleware;

import com.library.backend.UserService;
import com.library.models.User;

import java.util.Optional;

/**
 * Service to handle user authentication.
 * Implements the Singleton pattern.
 */
public class AuthenticationService {
    private static AuthenticationService instance;
    private final UserService userService;
    private User currentUser;

    // Private constructor to prevent direct instantiation
    private AuthenticationService() {
        userService = UserService.getInstance();
        currentUser = null;
    }

    // Static method to get the singleton instance
    public static synchronized AuthenticationService getInstance() {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }

    /**
     * Attempts to log in a user with the provided credentials.
     *
     * @param email    the user's email
     * @param password the user's password
     * @return true if login is successful, false otherwise
     */
    public boolean login(String email, String password) {
        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            return false;
        }

        Optional<User> userOpt = userService.login(email.trim(), password);
        if (userOpt.isPresent()) {
            currentUser = userOpt.get();
            return true;
        }
        return false;
    }

    /**
     * Logs out the current user.
     */
    public void logout() {
        currentUser = null;
    }

    /**
     * Checks if a user is currently logged in.
     *
     * @return true if logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Gets the currently logged-in user.
     *
     * @return Optional containing the user, or empty if not logged in
     */
    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    public boolean isCurrentUserAdmin() {
        return isLoggedIn() && "ADMIN".equalsIgnoreCase(currentUser.getUserType());
    }

    public boolean isCurrentUserStudent() {
        return isLoggedIn() && "STUDENT".equalsIgnoreCase(currentUser.getUserType());
    }

    public boolean isCurrentUserTeacher() {
        return isLoggedIn() && "TEACHER".equalsIgnoreCase(currentUser.getUserType());
    }
}
