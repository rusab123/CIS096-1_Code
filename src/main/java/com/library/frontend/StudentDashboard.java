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
import com.library.middleware.AuthenticationService;
import com.library.models.Book;
import com.library.models.BorrowRecord;
import com.library.models.Student;

import java.util.List;
import java.util.Optional;

/**
 * Dashboard for student users
 */
public class StudentDashboard extends BaseScreen {
    private final AuthenticationService authService;
    private final BookService bookService;
    private final BorrowService borrowService;
    private final Student currentStudent;
    
    private TableView<Book> booksTable;
    private TableView<BorrowRecord> myBooksTable;
    
    public StudentDashboard(Stage stage) {
        super(stage);
        // Initialize services first
        authService = AuthenticationService.getInstance();
        bookService = BookService.getInstance();
        borrowService = BorrowService.getInstance();
        
        // Verify that the current user is a student
        if (!authService.isCurrentUserStudent()) {
            showAlert("Access Denied", "You must be a student to access this page.");
            // Redirect back to login
            LoginScreen loginScreen = new LoginScreen(stage);
            loginScreen.show();
            currentStudent = null;
            return;
        }
        
        // Cast to Student
        currentStudent = (Student) authService.getCurrentUser().get();
        
        // Initialize UI after service initialization
        initialize();
    }
    
    @Override
    protected void setupUI() {
        // Set up the dashboard with a TabPane
        TabPane tabPane = new TabPane();
        
        // Create tabs
        Tab booksTab = new Tab("Available Books");
        booksTab.setClosable(false);
        
        Tab myBooksTab = new Tab("My Borrowed Books");
        myBooksTab.setClosable(false);
        
        // Set up books tab content
        booksTab.setContent(createBooksTabContent());
        
        // Set up my books tab content
        myBooksTab.setContent(createMyBooksTabContent());
        
        // Add tabs to the pane
        tabPane.getTabs().addAll(booksTab, myBooksTab);
        
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
        Text title = new Text("Student Dashboard");
        title.setFont(Font.font("Tahoma", FontWeight.BOLD, 18));
        
        // Create logout button
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            authService.logout();
            LoginScreen loginScreen = new LoginScreen(stage);
            loginScreen.show();
        });
        
        // Create user info
        VBox userInfo = new VBox(5);
        Text userName = new Text("Student: " + currentStudent.getName());
        Text userDept = new Text("Department: " + currentStudent.getDepartment());
        Text booksQuota = new Text("Books Quota: " + currentStudent.getRemainingBookQuota() + " remaining");
        userInfo.getChildren().addAll(userName, userDept, booksQuota);
        
        // Add components to header
        header.setLeft(title);
        header.setCenter(userInfo);
        header.setRight(logoutButton);
        
        return header;
    }
    
    private BorderPane createBooksTabContent() {
        BorderPane content = new BorderPane();
        
        // Create table for books
        booksTable = new TableView<>();
        setupBooksTable();
        refreshBooksList();
        
        // Create a search bar
        HBox searchBar = new HBox(10);
        searchBar.setPadding(new Insets(10));
        searchBar.setAlignment(Pos.CENTER);
        
        TextField searchField = new TextField();
        searchField.setPromptText("Search by title, author, or category");
        searchField.setPrefWidth(300);
        
        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> {
            String query = searchField.getText().trim();
            if (!query.isEmpty()) {
                List<Book> searchResults = bookService.searchBooks(query);
                updateBooksTable(searchResults);
            } else {
                refreshBooksList();
            }
        });
        
        Button resetButton = new Button("Reset");
        resetButton.setOnAction(e -> {
            searchField.clear();
            refreshBooksList();
        });
        
        searchBar.getChildren().addAll(searchField, searchButton, resetButton);
        
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
                    Optional<BorrowRecord> result = borrowService.borrowBook(currentStudent.getId(), selectedBook.getId());
                    if (result.isPresent()) {
                        // Update book quantity
                        selectedBook.setQuantity(selectedBook.getQuantity() - 1);
                        if (selectedBook.getQuantity() == 0) {
                            selectedBook.setAvailable(false);
                        }
                        bookService.updateBook(selectedBook);
                        
                        // Update student's borrowed books count
                        currentStudent.borrowBook(selectedBook.getId());
                        
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
        content.setTop(searchBar);
        content.setCenter(booksTable);
        content.setRight(actionPanel);
        
        return content;
    }
    
    private BorderPane createMyBooksTabContent() {
        BorderPane content = new BorderPane();
        
        // Create table for borrowed books
        myBooksTable = new TableView<>();
        setupMyBooksTable();
        refreshMyBooksList();
        
        // Create action panel for returning books
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
                    
                    // Update student's borrowed books count
                    currentStudent.returnBook(selectedRecord.getBookId());
                    
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
    
    private void setupBooksTable() {
        TableColumn<Book, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        
        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        
        TableColumn<Book, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        
        TableColumn<Book, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        
        TableColumn<Book, Boolean> availableCol = new TableColumn<>("Available");
        availableCol.setCellValueFactory(new PropertyValueFactory<>("available"));
        
        booksTable.getColumns().addAll(idCol, titleCol, authorCol, categoryCol, quantityCol, availableCol);
    }
    
    private void setupMyBooksTable() {
        TableColumn<BorrowRecord, String> recordIdCol = new TableColumn<>("Record ID");
        recordIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<BorrowRecord, String> bookIdCol = new TableColumn<>("Book ID");
        bookIdCol.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        
        TableColumn<BorrowRecord, String> dueDateCol = new TableColumn<>("Due Date");
        dueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        
        TableColumn<BorrowRecord, Boolean> returnedCol = new TableColumn<>("Returned");
        returnedCol.setCellValueFactory(new PropertyValueFactory<>("returned"));
        
        TableColumn<BorrowRecord, Double> fineCol = new TableColumn<>("Fine");
        fineCol.setCellValueFactory(new PropertyValueFactory<>("fine"));
        
        myBooksTable.getColumns().addAll(recordIdCol, bookIdCol, dueDateCol, returnedCol, fineCol);
    }
    
    private void refreshBooksList() {
        List<Book> books = bookService.getAllBooks();
        updateBooksTable(books);
    }
    
    private void updateBooksTable(List<Book> books) {
        ObservableList<Book> bookList = FXCollections.observableArrayList(books);
        booksTable.setItems(bookList);
    }
    
    private void refreshMyBooksList() {
        List<BorrowRecord> records = borrowService.getBorrowRecordsByUserId(currentStudent.getId());
        ObservableList<BorrowRecord> recordList = FXCollections.observableArrayList(records);
        myBooksTable.setItems(recordList);
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