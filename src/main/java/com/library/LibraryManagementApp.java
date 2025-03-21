package com.library;

import javafx.application.Application;
import javafx.stage.Stage;

import com.library.frontend.LoginScreen;

/**
 * Main application class for the Library Management System
 */
public class LibraryManagementApp extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Library Management System");
        
        // Set up the login screen as the first screen
        LoginScreen loginScreen = new LoginScreen(primaryStage);
        loginScreen.show();
    }
    
    /**
     * Main method to launch the application
     */
    public static void main(String[] args) {
        launch(args);
    }
} 