package com.library.frontend;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
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
import com.library.backend.BorrowService;
import com.library.backend.UserService;
import com.library.middleware.AuthenticationService;
import com.library.models.Book;
import com.library.models.BorrowRecord;
import com.library.models.Teacher;
import com.library.models.User;

import java.util.List;
import java.util.Optional;

/**
 * Dashboard for teacher users
 */
public class TeacherDashboard extends BaseScreen {
    private final AuthenticationService authService;
    private final BookService bookService;
    private final BorrowService borrowService;
    private final UserService userService;
    private final Teacher currentTeacher;
    
    private TableView<Book> booksTable;
    private TableView<BorrowRecord> myBooksTable;
    private TableView<User> usersTable;
    
    public TeacherDashboard(Stage stage) {
        super(stage);
        authService = AuthenticationService.getInstance();
        bookService = BookService.getInstance();
        borrowService = BorrowService.getInstance();
        userService = UserService.getInstance();
        
        // Verify that the current user is a teacher
        if (!authService.isCurrentUserTeacher()) {
            showAlert("Access Denied", "You must be a teacher to access this page.");
            // Redirect back to login
            LoginScreen loginScreen = new LoginScreen(stage);
            loginScreen.show();
            currentTeacher = null;
            return;
        }
        
        // Cast to Teacher
        currentTeacher = (Teacher) authService.getCurrentUser().get();
        
        // Initialize UI after service initialization
        initialize();
    }
    
    @Override
    protected void setupUI() {
        // Create main layout
        BorderPane mainLayout = new BorderPane();
        
        // Create header with user info and logout
        BorderPane header = createHeader();
        
        // Create content area with tabs
        TabPane tabPane = new TabPane();
        
        // Create tabs
        Tab availableBooksTab = new Tab("Available Books");
        availableBooksTab.setClosable(false);
        availableBooksTab.setContent(createAvailableBooksTab());
        
        Tab myBooksTab = new Tab("My Books");
        myBooksTab.setClosable(false);
        myBooksTab.setContent(createMyBooksTab());
        
        Tab usersTab = new Tab("Users");
        usersTab.setClosable(false);
        usersTab.setContent(createUsersTab());
        
        // Add tabs to the pane
        tabPane.getTabs().addAll(availableBooksTab, myBooksTab, usersTab);
        
        // Add components to main layout
        mainLayout.setTop(header);
        mainLayout.setCenter(tabPane);
        
        // Set the main layout as the root
        root = mainLayout;
    }
    
    private BorderPane createHeader() {
        BorderPane header = new BorderPane();
        header.setPadding(new Insets(10));
        
        // Create user info panel
        VBox userInfo = createUserInfoPanel();
        
        // Create logout button
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            authService.logout();
            LoginScreen loginScreen = new LoginScreen(stage);
            loginScreen.show();
        });
        
        // Add components to header
        header.setLeft(userInfo);
        header.setRight(logoutButton);
        
        return header;
    }
    
    private VBox createUserInfoPanel() {
        VBox userInfo = new VBox(5);
        userInfo.setAlignment(Pos.CENTER_LEFT);
        
        // Add ID display field
        TextField idDisplayField = new TextField();
        idDisplayField.setPromptText("User ID");
        idDisplayField.setEditable(false);
        
        // Add selection listener to update ID field
        usersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                idDisplayField.setText(newSelection.getId());
            } else {
                idDisplayField.clear();
            }
        });
        
        Text userName = new Text("Welcome, " + currentTeacher.getName());
        userName.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        Text userDept = new Text("Department: " + currentTeacher.getDepartment());
        Text userDesignation = new Text("Designation: " + currentTeacher.getDesignation());
        Text booksQuota = new Text("Books Quota: " + currentTeacher.getRemainingBookQuota() + " remaining");
        
        userInfo.getChildren().addAll(idDisplayField, userName, userDept, userDesignation, booksQuota);
        
        return userInfo;
    }
    
    private BorderPane createAvailableBooksTab() {
        BorderPane content = new BorderPane();
        
        // Create table for available books
        booksTable = new TableView<>();
        setupBooksTable();
        refreshBooksList();
        
        // Create search panel
        VBox searchPanel = new VBox(10);
        searchPanel.setPadding(new Insets(10));
        
        TextField searchField = new TextField();
        searchField.setPromptText("Search books...");
        searchField.setOnAction(e -> {
            String searchText = searchField.getText().toLowerCase();
            ObservableList<Book> filteredBooks = FXCollections.observableArrayList();
            
            for (Book book : bookService.getAllBooks()) {
                if (book.getTitle().toLowerCase().contains(searchText) ||
                    book.getAuthor().toLowerCase().contains(searchText) ||
                    book.getIsbn().toLowerCase().contains(searchText) ||
                    book.getCategory().toLowerCase().contains(searchText)) {
                    filteredBooks.add(book);
                }
            }
            
            booksTable.setItems(filteredBooks);
        });
        
        searchPanel.getChildren().add(searchField);
        
        // Create action panel
        VBox actionPanel = new VBox(10);
        actionPanel.setPadding(new Insets(10));
        
        // Add book ID display field for borrowing
        TextField bookIdDisplayField = new TextField();
        bookIdDisplayField.setPromptText("Selected Book ID");
        bookIdDisplayField.setEditable(false);
        
        // Add selection listener to update book ID field
        booksTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                bookIdDisplayField.setText(newSelection.getId());
            } else {
                bookIdDisplayField.clear();
            }
        });
        
        Button borrowBookButton = new Button("Borrow Selected Book");
        borrowBookButton.setOnAction(e -> {
            Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
            if (selectedBook != null) {
                if (selectedBook.isAvailable() && selectedBook.getQuantity() > 0) {
                    Optional<BorrowRecord> result = borrowService.borrowBook(currentTeacher.getId(), selectedBook.getId());
                    if (result.isPresent()) {
                        // Update book quantity
                        selectedBook.setQuantity(selectedBook.getQuantity() - 1);
                        if (selectedBook.getQuantity() == 0) {
                            selectedBook.setAvailable(false);
                        }
                        bookService.updateBook(selectedBook);
                        
                        // Update teacher's borrowed books count
                        currentTeacher.borrowBook(selectedBook.getId());
                        
                        refreshBooksList();
                        refreshMyBooksList();
                        showAlert("Success", "Book borrowed successfully!");
                    } else {
                        showAlert("Error", "Failed to borrow book. You may have reached your book quota.");
                    }
                } else {
                    showAlert("Error", "This book is not available for borrowing.");
                }
            } else {
                showAlert("Selection Error", "Please select a book to borrow.");
            }
        });
        
        actionPanel.getChildren().addAll(bookIdDisplayField, borrowBookButton);
        
        // Add components to content
        content.setTop(searchPanel);
        content.setCenter(booksTable);
        content.setRight(actionPanel);
        
        return content;
    }
    
    private BorderPane createMyBooksTab() {
        BorderPane content = new BorderPane();
        
        // Create table for borrowed books
        myBooksTable = new TableView<>();
        setupMyBooksTable();
        refreshMyBooksList();
        
        // Create action panel
        VBox actionPanel = new VBox(10);
        actionPanel.setPadding(new Insets(10));
        
        // Add book ID display field for returning
        TextField returnBookIdDisplayField = new TextField();
        returnBookIdDisplayField.setPromptText("Selected Book ID");
        returnBookIdDisplayField.setEditable(false);
        
        // Add selection listener to update book ID field
        myBooksTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                returnBookIdDisplayField.setText(newSelection.getBookId());
            } else {
                returnBookIdDisplayField.clear();
            }
        });
        
        Button returnBookButton = new Button("Return Selected Book");
        returnBookButton.setOnAction(e -> {
            BorrowRecord selectedRecord = myBooksTable.getSelectionModel().getSelectedItem();
            if (selectedRecord != null) {
                if (borrowService.returnBook(selectedRecord.getId()).isPresent()) {
                    // Update book quantity
                    Book book = bookService.getBookById(selectedRecord.getBookId()).get();
                    book.setQuantity(book.getQuantity() + 1);
                    book.setAvailable(true);
                    bookService.updateBook(book);
                    
                    // Update teacher's borrowed books count
                    currentTeacher.returnBook(selectedRecord.getBookId());
                    
                    refreshMyBooksList();
                    refreshBooksList();
                    showAlert("Success", "Book returned successfully!");
                } else {
                    showAlert("Error", "Failed to return book.");
                }
            } else {
                showAlert("Selection Error", "Please select a book to return.");
            }
        });
        
        actionPanel.getChildren().addAll(returnBookIdDisplayField, returnBookButton);
        
        // Add components to content
        content.setCenter(myBooksTable);
        content.setRight(actionPanel);
        
        return content;
    }
    
    private BorderPane createUsersTab() {
        BorderPane content = new BorderPane();
        
        // Create table for users
        usersTable = new TableView<>();
        setupUsersTable();
        refreshUsersList();
        
        // Search panel
        HBox searchPanel = new HBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText("Search users...");
        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> {
            String searchText = searchField.getText().toLowerCase();
            ObservableList<User> filteredUsers = FXCollections.observableArrayList();
            
            for (User user : userService.getAllUsers()) {
                if (user.getName().toLowerCase().contains(searchText) ||
                    user.getEmail().toLowerCase().contains(searchText) ||
                    user.getId().toLowerCase().contains(searchText)) {
                    filteredUsers.add(user);
                }
            }
            
            usersTable.setItems(filteredUsers);
        });
        
        searchPanel.getChildren().addAll(searchField, searchButton);
        
        // Add components to content
        content.setTop(searchPanel);
        content.setCenter(usersTable);
        
        return content;
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
    
    private void setupMyBooksTable() {
        TableColumn<BorrowRecord, String> idCol = new TableColumn<>("Record ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<BorrowRecord, String> bookIdCol = new TableColumn<>("Book ID");
        bookIdCol.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        
        TableColumn<BorrowRecord, String> dueDateCol = new TableColumn<>("Due Date");
        dueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        
        TableColumn<BorrowRecord, Boolean> returnedCol = new TableColumn<>("Returned");
        returnedCol.setCellValueFactory(new PropertyValueFactory<>("returned"));
        
        TableColumn<BorrowRecord, Double> fineCol = new TableColumn<>("Fine");
        fineCol.setCellValueFactory(new PropertyValueFactory<>("fine"));
        
        myBooksTable.getColumns().addAll(idCol, bookIdCol, dueDateCol, returnedCol, fineCol);
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
        
        usersTable.getColumns().addAll(idCol, nameCol, emailCol, typeCol);
    }
    
    private void refreshBooksList() {
        List<Book> books = bookService.getAllBooks();
        ObservableList<Book> bookList = FXCollections.observableArrayList(books);
        booksTable.setItems(bookList);
    }
    
    private void refreshMyBooksList() {
        List<BorrowRecord> records = borrowService.getBorrowRecordsByUserId(currentTeacher.getId());
        ObservableList<BorrowRecord> recordList = FXCollections.observableArrayList(records);
        myBooksTable.setItems(recordList);
    }
    
    private void refreshUsersList() {
        List<User> users = userService.getAllUsers();
        ObservableList<User> userList = FXCollections.observableArrayList(users);
        usersTable.setItems(userList);
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