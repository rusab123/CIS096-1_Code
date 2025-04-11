package com.library.frontend;

import com.library.backend.BookService;
import com.library.backend.BorrowService;
import com.library.backend.UserService;
import com.library.models.Book;
import com.library.models.BorrowRecord;
import com.library.models.Student;
import com.library.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class TeacherDashboard extends BaseScreen {

    private final BookService bookService;
    private final BorrowService borrowService;
    private final UserService userService;

    private TableView<Book> booksTable;
    private TableView<BorrowRecord> myBooksTable;
    private TableView<Student> studentsTable;

    public TeacherDashboard(Stage stage) {
        super(stage);
        bookService = BookService.getInstance();
        borrowService = BorrowService.getInstance();
        userService = UserService.getInstance();
        initialize();
    }

    @Override
    protected void setupUI() {
        BorderPane mainLayout = new BorderPane();

        TabPane tabPane = new TabPane();

        // Tab for available books
        Tab availableBooksTab = new Tab("Available Books");
        availableBooksTab.setClosable(false);
        availableBooksTab.setContent(createAvailableBooksTab());

        // Tab for borrowed books
        Tab myBooksTab = new Tab("My Books");
        myBooksTab.setClosable(false);
        myBooksTab.setContent(createMyBooksTab());

        // Tab for students
        Tab studentsTab = new Tab("Students");
        studentsTab.setClosable(false);
        studentsTab.setContent(createStudentsTab());

        tabPane.getTabs().addAll(availableBooksTab, myBooksTab, studentsTab);
        mainLayout.setCenter(tabPane);

        root = mainLayout;  // Ensure 'root' is properly initialized
        stage.setScene(new Scene(root, 800, 600));
    }

    private BorderPane createAvailableBooksTab() {
        BorderPane content = new BorderPane();

        booksTable = new TableView<>();
        setupBooksTable();
        refreshBooksList();

        Button borrowButton = new Button("Borrow Book");
        borrowButton.setOnAction(e -> borrowBook());

        Button returnButton = new Button("Return Book");
        returnButton.setOnAction(e -> returnBook());

        HBox buttonPanel = new HBox(10, borrowButton, returnButton);
        buttonPanel.setAlignment(Pos.CENTER);
        content.setBottom(buttonPanel);
        content.setCenter(booksTable);

        return content;
    }

    private BorderPane createMyBooksTab() {
        BorderPane content = new BorderPane();

        myBooksTable = new TableView<>();
        setupMyBooksTable();
        refreshMyBooksList();

        content.setCenter(myBooksTable);
        return content;
    }

    private BorderPane createStudentsTab() {
        BorderPane content = new BorderPane();

        studentsTable = new TableView<>();
        setupStudentsTable();
        refreshStudentsList();

        content.setCenter(studentsTable);
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

        booksTable.getColumns().addAll(idCol, titleCol, authorCol, categoryCol, quantityCol);
    }

    private void setupMyBooksTable() {
        TableColumn<BorrowRecord, String> bookIdCol = new TableColumn<>("Book ID");
        bookIdCol.setCellValueFactory(new PropertyValueFactory<>("bookId"));

        TableColumn<BorrowRecord, String> bookTitleCol = new TableColumn<>("Book Title");
        bookTitleCol.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));

        TableColumn<BorrowRecord, String> borrowDateCol = new TableColumn<>("Borrow Date");
        borrowDateCol.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));

        myBooksTable.getColumns().addAll(bookIdCol, bookTitleCol, borrowDateCol);
    }

    private void setupStudentsTable() {
        TableColumn<Student, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Student, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        studentsTable.getColumns().addAll(idCol, nameCol, emailCol);
    }

    private void refreshBooksList() {
        List<Book> books = bookService.getAllBooks();
        booksTable.setItems(FXCollections.observableArrayList(books));
    }

    private void refreshMyBooksList() {
        List<BorrowRecord> records = borrowService.getBorrowRecordsByUserId(getCurrentUserId());
        myBooksTable.setItems(FXCollections.observableArrayList(records));
    }

    private void refreshStudentsList() {
        List<User> users = userService.getAllUsers();
        ObservableList<Student> students = FXCollections.observableArrayList();

        // Filter out students from the list of users
        for (User user : users) {
            if (user instanceof Student) {
                students.add((Student) user);
            }
        }

        studentsTable.setItems(students);
    }

    private void borrowBook() {
        Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("Error", "Please select a book to borrow.");
            return;
        }

        String bookId = selectedBook.getId();  // Corrected this line for consistency
        String userId = getCurrentUserId();

        borrowService.borrowBook(userId, bookId);  // Assuming the signature is correct
        refreshBooksList();
        refreshMyBooksList();
        showAlert("Success", "Book borrowed successfully.");
    }

    private void returnBook() {
        BorrowRecord selectedRecord = myBooksTable.getSelectionModel().getSelectedItem();
        if (selectedRecord == null) {
            showAlert("Error", "Please select a borrowed book to return.");
            return;
        }

        String recordId = selectedRecord.getId();

        borrowService.returnBook(recordId);  // Correct usage of returnBook
        refreshBooksList();
        refreshMyBooksList();
        showAlert("Success", "Book returned successfully.");
    }

    private String getCurrentUserId() {
        // Replace with actual logic to fetch the teacher ID
        return userService.getLoggedInUserId();  // Replace this with actual fetching logic
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
