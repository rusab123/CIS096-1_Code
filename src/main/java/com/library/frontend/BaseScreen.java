package com.library.frontend;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Abstract base class for all screens in the application
 */
public abstract class BaseScreen {
    protected Stage stage;
    protected BorderPane root;
    protected Scene scene;
    
    public BaseScreen(Stage stage) {
        this.stage = stage;
        this.root = new BorderPane();
        this.scene = new Scene(root, 800, 600);
    }
    
    /**
     * Shows this screen on the stage
     */
    public void show() {
        stage.setScene(scene);
        stage.show();
    }
    
    /**
     * Initializes the screen by setting up UI elements
     * This should be called after any necessary services are initialized
     */
    protected void initialize() {
        setupUI();
    }
    
    /**
     * Sets up the UI elements for this screen
     * Subclasses should override this method to create their specific UI
     */
    protected abstract void setupUI();
} 