package com.library.frontend;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import com.library.middleware.AuthenticationService;
import com.library.models.User;

/**
 * Login screen for user authentication
 */
public class LoginScreen extends BaseScreen {
    private final AuthenticationService authService;
    
    public LoginScreen(Stage stage) {
        super(stage);
        authService = AuthenticationService.getInstance();
        initialize();
    }
    
    @Override
    protected void setupUI() {
        // Create the form grid
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        
        // Add title
        Text title = new Text("Library Management System");
        title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(title, 0, 0, 2, 1);
        
        // Add login form fields
        Label emailLabel = new Label("Email:");
        grid.add(emailLabel, 0, 1);
        
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");
        grid.add(emailField, 1, 1);
        
        Label passwordLabel = new Label("Password:");
        grid.add(passwordLabel, 0, 2);
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        grid.add(passwordField, 1, 2);
        
        // Add login button
        Button loginButton = new Button("Login");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(loginButton);
        grid.add(hbBtn, 1, 4);
        
        // Add message text area
        final Text actionTarget = new Text();
        grid.add(actionTarget, 1, 6);
        
        // Handle login button action
        loginButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();
            
            if (email.isEmpty() || password.isEmpty()) {
                showAlert("Login Error", "Please enter both email and password.");
                return;
            }
            
            boolean loginSuccess = authService.login(email, password);
            
            if (loginSuccess) {
                User user = authService.getCurrentUser().get();
                
                // Redirect to appropriate dashboard based on user type
                if (authService.isCurrentUserAdmin()) {
                    AdminDashboard adminDashboard = new AdminDashboard(stage);
                    adminDashboard.show();
                } else if (authService.isCurrentUserStudent()) {
                    StudentDashboard studentDashboard = new StudentDashboard(stage);
                    studentDashboard.show();
                } else if (authService.isCurrentUserTeacher()) {
                    TeacherDashboard teacherDashboard = new TeacherDashboard(stage);
                    teacherDashboard.show();
                }
            } else {
                showAlert("Login Failed", "Invalid email or password. Please try again.");
            }
        });
        
        // Set up demo credentials for testing
        VBox demoBox = new VBox(10);
        demoBox.setPadding(new Insets(20, 0, 0, 0));
        demoBox.setAlignment(Pos.CENTER);
        
        Text demoTitle = new Text("Demo Credentials");
        demoTitle.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));
        
        Text adminCred = new Text("Admin: admin@library.com / admin123");
        Text studentCred = new Text("Student: john@university.edu / pass123");
        Text teacherCred = new Text("Teacher: johnson@university.edu / prof123");
        
        demoBox.getChildren().addAll(demoTitle, adminCred, studentCred, teacherCred);
        
        // Create main layout and add components
        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.getChildren().addAll(grid, demoBox);
        
        root.setCenter(mainLayout);
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 