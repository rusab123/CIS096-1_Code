package com.library.middleware;

import com.library.backend.UserService;
import com.library.models.User;

import java.util.Optional;

/**
 * Service to handle user authentication
 * Implemented using the Singleton pattern
 */
public class AuthenticationService {
    private static AuthenticationService instance;
    private final UserService userService;
    private User currentUser;
    private boolean isLoggedIn;
    
    // Private constructor to prevent direct instantiation
    private AuthenticationService() {
        userService = UserService.getInstance();
        currentUser = null;
        isLoggedIn = false;
    }
    
    // Static method to get the singleton instance
    public static synchronized AuthenticationService getInstance() {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }
    
    /**
     * Attempts to log in a user with the provided credentials
     * 
     * @param email the user's email
     * @param password the user's password
     * @return true if login is successful, false otherwise
     */
    public boolean login(String email, String password) {
        Optional<User> userOpt = userService.login(email, password);
        
        if (userOpt.isPresent()) {
            currentUser = userOpt.get();
            isLoggedIn = true;
            return true;
        }
        
        return false;
    }
    
    /**
     * Logs out the current user
     */
    public void logout() {
        currentUser = null;
        isLoggedIn = false;
    }
    
    /**
     * Checks if a user is currently logged in
     * 
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return isLoggedIn;
    }
    
    /**
     * Gets the currently logged-in user
     * 
     * @return an Optional containing the current user, or empty if no user is logged in
     */
    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }
    
    /**
     * Checks if the current user is an administrator
     * 
     * @return true if the current user is an admin, false otherwise
     */
    public boolean isCurrentUserAdmin() {
        return isLoggedIn && currentUser.getUserType().equals("ADMIN");
    }
    
    /**
     * Checks if the current user is a student
     * 
     * @return true if the current user is a student, false otherwise
     */
    public boolean isCurrentUserStudent() {
        return isLoggedIn && currentUser.getUserType().equals("STUDENT");
    }
    
    /**
     * Checks if the current user is a teacher
     * 
     * @return true if the current user is a teacher, false otherwise
     */
    public boolean isCurrentUserTeacher() {
        return isLoggedIn && currentUser.getUserType().equals("TEACHER");
    }
} 