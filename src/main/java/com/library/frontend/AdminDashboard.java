package com.library.frontend;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

import com.library.backend.BookService;
import com.library.backend.UserService;
import com.library.middleware.AuthenticationService;
import com.library.models.Book;
import com.library.models.User;
import com.library.models.Student;
import com.library.models.Teacher;
import com.library.models.Admin;

import java.util.List;
import java.util.Optional;

/**
 * Dashboard for admin users
 */
public class AdminDashboard extends BaseScreen {
    private final AuthenticationService authService;
    private final UserService userService;
    private final BookService bookService;
    
    private TableView<User> usersTable;
    private TableView<Book> booksTable;
    private int nextUserId = 1;
    private int nextBookId = 1;
    
    public AdminDashboard(Stage stage) {
        super(stage);
        // Initialize services first
        authService = AuthenticationService.getInstance();
        userService = UserService.getInstance();
        bookService = BookService.getInstance();
        
        // Verify that the current user is an admin
        if (!authService.isCurrentUserAdmin()) {
            showAlert("Access Denied", "You must be an administrator to access this page.");
            // Redirect back to login
            LoginScreen loginScreen = new LoginScreen(stage);
            loginScreen.show();
            return;
        }
        
        // Initialize UI after service initialization
        initialize();
    }
    
    @Override
    protected void setupUI() {
        // Set up the dashboard with a TabPane
        TabPane tabPane = new TabPane();
        
        // Create tabs
        Tab usersTab = new Tab("Users Management");
        usersTab.setClosable(false);
        
        Tab booksTab = new Tab("Books Management");
        booksTab.setClosable(false);
        
        // Set up users tab content
        usersTab.setContent(createUsersTabContent());
        
        // Set up books tab content
        booksTab.setContent(createBooksTabContent());
        
        // Add tabs to the pane
        tabPane.getTabs().addAll(usersTab, booksTab);
        
        // Create a header with title and logout button
        BorderPane header = createHeader();
        
        // Add components to the main layout
        root.setTop(header);
        root.setCenter(tabPane);
    }
    
    private BorderPane createHeader() {
        BorderPane header = new BorderPane();
        header.setPadding(new Insets(10));
        
        // Create title text
        Text title = new Text("Admin Dashboard");
        title.setFont(Font.font("Tahoma", FontWeight.BOLD, 18));
        
        // Create logout button
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            authService.logout();
            LoginScreen loginScreen = new LoginScreen(stage);
            loginScreen.show();
        });
        
        // Add components to header
        header.setLeft(title);
        header.setRight(logoutButton);
        
        return header;
    }
    
    private BorderPane createUsersTabContent() {
        BorderPane content = new BorderPane();
        
        // Create table for users
        usersTable = new TableView<>();
        setupUsersTable();
        refreshUsersList();
        
        // Create user management panel
        VBox userPanel = new VBox(10);
        userPanel.setPadding(new Insets(10));
        userPanel.setAlignment(Pos.CENTER);
        
        Label userLabel = new Label("User Management");
        userLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        // Add user form
        VBox addUserForm = new VBox(5);
        addUserForm.setPadding(new Insets(10));
        
        // Add ID display field
        TextField idDisplayField = new TextField();
        idDisplayField.setPromptText("Selected User ID");
        idDisplayField.setEditable(false);
        
        // Add selection listener to update ID field
        usersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                idDisplayField.setText(newSelection.getId());
            } else {
                idDisplayField.clear();
            }
        });
        
        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        
        ComboBox<String> userTypeCombo = new ComboBox<>();
        userTypeCombo.getItems().addAll("STUDENT", "TEACHER", "ADMIN");
        userTypeCombo.setPromptText("User Type");
        
        TextField departmentField = new TextField();
        departmentField.setPromptText("Department");
        
        TextField designationField = new TextField();
        designationField.setPromptText("Designation (for teachers)");
        
        Button addUserButton = new Button("Add User");
        addUserButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();
            String userType = userTypeCombo.getValue();
            String department = departmentField.getText().trim();
            String designation = designationField.getText().trim();
            
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || userType == null) {
                showAlert("Input Error", "Please fill in all required fields.");
                return;
            }
            
            User newUser = null;
            switch (userType) {
                case "STUDENT":
                    newUser = new Student(name, email, password, String.format("STU%03d", nextUserId++), department);
                    break;
                case "TEACHER":
                    newUser = new Teacher(name, email, password, String.format("TCH%03d", nextUserId++), department, designation);
                    break;
                case "ADMIN":
                    newUser = new Admin(name, email, password, String.format("ADM%03d", nextUserId++), "System Administrator");
                    break;
            }
            
            if (newUser != null) {
                userService.addUser(newUser);
                refreshUsersList();
                clearUserForm(nameField, emailField, passwordField, userTypeCombo, departmentField, designationField);
                showAlert("Success", "User added successfully!");
            }
        });
        
        addUserForm.getChildren().addAll(idDisplayField, nameField, emailField, passwordField, userTypeCombo, 
                                       departmentField, designationField, addUserButton);
        
        // Delete user button
        Button deleteUserButton = new Button("Delete Selected User");
        deleteUserButton.setOnAction(e -> {
            User selectedUser = usersTable.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                if (userService.deleteUser(selectedUser.getId())) {
                    refreshUsersList();
                    showAlert("Success", "User deleted successfully!");
                } else {
                    showAlert("Error", "Failed to delete user.");
                }
            } else {
                showAlert("Selection Error", "Please select a user to delete.");
            }
        });
        
        userPanel.getChildren().addAll(userLabel, addUserForm, deleteUserButton);
        
        // Add components to content
        content.setCenter(usersTable);
        content.setRight(userPanel);
        
        return content;
    }
    
    private BorderPane createBooksTabContent() {
        BorderPane content = new BorderPane();
        
        // Create table for books
        booksTable = new TableView<>();
        setupBooksTable();
        refreshBooksList();
        
        // Create book management panel
        VBox bookPanel = new VBox(10);
        bookPanel.setPadding(new Insets(10));
        bookPanel.setAlignment(Pos.CENTER);
        
        Label bookLabel = new Label("Book Management");
        bookLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        // Add book form
        VBox addBookForm = new VBox(5);
        addBookForm.setPadding(new Insets(10));
        
        // Add ID display field
        TextField bookIdDisplayField = new TextField();
        bookIdDisplayField.setPromptText("Selected Book ID");
        bookIdDisplayField.setEditable(false);
        
        // Add selection listener to update ID field
        booksTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                bookIdDisplayField.setText(newSelection.getId());
            } else {
                bookIdDisplayField.clear();
            }
        });
        
        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        
        TextField authorField = new TextField();
        authorField.setPromptText("Author");
        
        TextField isbnField = new TextField();
        isbnField.setPromptText("ISBN");
        
        TextField categoryField = new TextField();
        categoryField.setPromptText("Category");
        
        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");
        
        Button addBookButton = new Button("Add Book");
        addBookButton.setOnAction(e -> {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String isbn = isbnField.getText().trim();
            String category = categoryField.getText().trim();
            String quantityStr = quantityField.getText().trim();
            
            if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() || category.isEmpty() || quantityStr.isEmpty()) {
                showAlert("Input Error", "Please fill in all fields.");
                return;
            }
            
            try {
                int quantity = Integer.parseInt(quantityStr);
                if (quantity <= 0) {
                    showAlert("Input Error", "Quantity must be greater than 0.");
                    return;
                }
                
                Book newBook = new Book(title, author, isbn, category, quantity);
                bookService.addBook(newBook);
                refreshBooksList();
                clearBookForm(titleField, authorField, isbnField, categoryField, quantityField);
                showAlert("Success", "Book added successfully!");
            } catch (NumberFormatException ex) {
                showAlert("Input Error", "Please enter a valid number for quantity.");
            }
        });
        
        addBookForm.getChildren().addAll(bookIdDisplayField, titleField, authorField, isbnField, categoryField, 
                                       quantityField, addBookButton);
        
        // Delete book button
        Button deleteBookButton = new Button("Delete Selected Book");
        deleteBookButton.setOnAction(e -> {
            Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
            if (selectedBook != null) {
                if (bookService.deleteBook(selectedBook.getId())) {
                    refreshBooksList();
                    showAlert("Success", "Book deleted successfully!");
                } else {
                    showAlert("Error", "Failed to delete book.");
                }
            } else {
                showAlert("Selection Error", "Please select a book to delete.");
            }
        });
        
        // Update book button
        Button updateBookButton = new Button("Update Selected Book");
        updateBookButton.setOnAction(e -> {
            Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
            if (selectedBook != null) {
                // Show update dialog
                Dialog<Book> dialog = createUpdateBookDialog(selectedBook);
                Optional<Book> result = dialog.showAndWait();
                if (result.isPresent()) {
                    Book updatedBook = result.get();
                    if (bookService.updateBook(updatedBook)) {
                        refreshBooksList();
                        showAlert("Success", "Book updated successfully!");
                    } else {
                        showAlert("Error", "Failed to update book.");
                    }
                }
            } else {
                showAlert("Selection Error", "Please select a book to update.");
            }
        });
        
        bookPanel.getChildren().addAll(bookLabel, addBookForm, deleteBookButton, updateBookButton);
        
        // Add components to content
        content.setCenter(booksTable);
        content.setRight(bookPanel);
        
        return content;
    }
    
    private void setupUsersTable() {
        TableColumn<User, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        TableColumn<User, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("userType"));
        
        TableColumn<User, String> departmentCol = new TableColumn<>("Department");
        departmentCol.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            if (user instanceof Student) {
                return new javafx.beans.property.SimpleStringProperty(((Student) user).getDepartment());
            } else if (user instanceof Teacher) {
                return new javafx.beans.property.SimpleStringProperty(((Teacher) user).getDepartment());
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        
        usersTable.getColumns().addAll(idCol, nameCol, emailCol, typeCol, departmentCol);
    }
    
    private void setupBooksTable() {
        TableColumn<Book, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        
        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        
        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN");
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        
        TableColumn<Book, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        
        TableColumn<Book, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        
        TableColumn<Book, Boolean> availableCol = new TableColumn<>("Available");
        availableCol.setCellValueFactory(new PropertyValueFactory<>("available"));
        
        booksTable.getColumns().addAll(idCol, titleCol, authorCol, isbnCol, categoryCol, quantityCol, availableCol);
    }
    
    private void refreshUsersList() {
        List<User> users = userService.getAllUsers();
        ObservableList<User> userList = FXCollections.observableArrayList(users);
        usersTable.setItems(userList);
    }
    
    private void refreshBooksList() {
        List<Book> books = bookService.getAllBooks();
        ObservableList<Book> bookList = FXCollections.observableArrayList(books);
        booksTable.setItems(bookList);
    }
    
    private void clearUserForm(TextField nameField, TextField emailField, PasswordField passwordField,
                             ComboBox<String> userTypeCombo, TextField departmentField, TextField designationField) {
        nameField.clear();
        emailField.clear();
        passwordField.clear();
        userTypeCombo.setValue(null);
        departmentField.clear();
        designationField.clear();
    }
    
    private void clearBookForm(TextField titleField, TextField authorField, TextField isbnField,
                             TextField categoryField, TextField quantityField) {
        titleField.clear();
        authorField.clear();
        isbnField.clear();
        categoryField.clear();
        quantityField.clear();
    }
    
    private Dialog<Book> createUpdateBookDialog(Book book) {
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Update Book");
        dialog.setHeaderText("Update book information");
        
        // Create the form fields
        TextField titleField = new TextField(book.getTitle());
        TextField authorField = new TextField(book.getAuthor());
        TextField isbnField = new TextField(book.getIsbn());
        TextField categoryField = new TextField(book.getCategory());
        TextField quantityField = new TextField(String.valueOf(book.getQuantity()));
        
        // Create the layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        
        grid.add(new Label("Author:"), 0, 1);
        grid.add(authorField, 1, 1);
        
        grid.add(new Label("ISBN:"), 0, 2);
        grid.add(isbnField, 1, 2);
        
        grid.add(new Label("Category:"), 0, 3);
        grid.add(categoryField, 1, 3);
        
        grid.add(new Label("Quantity:"), 0, 4);
        grid.add(quantityField, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Convert the result to a Book object
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    int quantity = Integer.parseInt(quantityField.getText().trim());
                    if (quantity <= 0) {
                        showAlert("Input Error", "Quantity must be greater than 0.");
                        return null;
                    }
                    
                    book.setTitle(titleField.getText().trim());
                    book.setAuthor(authorField.getText().trim());
                    book.setIsbn(isbnField.getText().trim());
                    book.setCategory(categoryField.getText().trim());
                    book.setQuantity(quantity);
                    return book;
                } catch (NumberFormatException ex) {
                    showAlert("Input Error", "Please enter a valid number for quantity.");
                    return null;
                }
            }
            return null;
        });
        
        return dialog;
    }
    
    private void showAlert(String title, String message) {
        Alert.AlertType type = title.equals("Success") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR;
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 