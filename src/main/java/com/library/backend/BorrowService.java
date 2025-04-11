package com.library.backend;

import com.library.backend.database.DbConnection;
import com.library.models.BorrowRecord;
import com.library.models.Student;
import com.library.models.Teacher;
import com.library.models.User;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class BorrowService {
    private static BorrowService instance;
    private final BookService bookService;
    private final UserService userService;

    private static final int STUDENT_BORROW_DAYS = 14;
    private static final int TEACHER_BORROW_DAYS = 30;

    private BorrowService() {
        bookService = BookService.getInstance();
        userService = UserService.getInstance();
    }

    public static synchronized BorrowService getInstance() {
        if (instance == null) {
            instance = new BorrowService();
        }
        return instance;
    }

    public Optional<BorrowRecord> borrowBook(String userId, String bookId) {
        Optional<User> userOpt = userService.getUserById(userId);
        if (!userOpt.isPresent()) return Optional.empty();  // User not found

        User user = userOpt.get();
        int borrowDays;

        // Check if user is a Student
        if (user instanceof Student student) {
            // Check if the student can borrow books (no quota check for unlimited students)
            if (!student.canBorrowBooks()) return Optional.empty();  // If the student can't borrow books

            borrowDays = STUDENT_BORROW_DAYS;  // Set borrow days for students
        } else if (user instanceof Teacher teacher) {
            if (!teacher.canBorrowBooks()) return Optional.empty();  // If the teacher can't borrow books
            borrowDays = TEACHER_BORROW_DAYS;  // Set borrow days for teachers
        } else {
            return Optional.empty(); // Admins or unsupported users can't borrow
        }

        // Check if the book is available
        if (!bookService.borrowBook(bookId)) return Optional.empty();  // If the book can't be borrowed

        String recordId = UUID.randomUUID().toString();
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(borrowDays);

        String sql = "INSERT INTO borrow_records(id, book_id, user_id, borrow_date, due_date, returned, fine) VALUES(?,?,?,?,?,false,0.0)";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, recordId);
            pstmt.setString(2, bookId);
            pstmt.setString(3, userId);
            pstmt.setDate(4, Date.valueOf(borrowDate));
            pstmt.setDate(5, Date.valueOf(dueDate));
            pstmt.executeUpdate();

            return Optional.of(new BorrowRecord(recordId, bookId, userId, borrowDate, dueDate, null, false, 0.0));
        } catch (SQLException e) {
            System.err.println("Error recording borrow: " + e.getMessage());
            return Optional.empty();  // Return empty if an error occurs
        }
    }

    public Optional<BorrowRecord> returnBook(String recordId) {
        Optional<BorrowRecord> opt = getBorrowRecordById(recordId);
        if (opt.isEmpty() || opt.get().isReturned()) return Optional.empty();

        BorrowRecord record = opt.get();

        // Proceed to return the book
        if (!bookService.returnBook(record.getBookId())) return Optional.empty();

        LocalDate returnDate = LocalDate.now();
        double fine = 0.0;

        if (returnDate.isAfter(record.getDueDate())) {
            long lateDays = returnDate.toEpochDay() - record.getDueDate().toEpochDay();
            fine = lateDays * 1.0;  // Consider modifying fine calculation as per your requirement
        }

        String sql = "UPDATE borrow_records SET return_date=?, returned=true, fine=? WHERE id=?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(returnDate));
            pstmt.setDouble(2, fine);
            pstmt.setString(3, recordId);
            pstmt.executeUpdate();

            return Optional.of(new BorrowRecord(
                    record.getId(),
                    record.getBookId(),
                    record.getUserId(),
                    record.getBorrowDate(),
                    record.getDueDate(),
                    returnDate,
                    true,
                    fine
            ));
        } catch (SQLException e) {
            System.err.println("Error returning book: " + e.getMessage());
            return Optional.empty();
        }
    }

    // Remaining methods for fetching borrow records and calculating fines
    public List<BorrowRecord> getAllBorrowRecords() {
        return fetchBorrowRecords("SELECT * FROM borrow_records");
    }

    public List<BorrowRecord> getBorrowRecordsByUserId(String userId) {
        return fetchBorrowRecords("SELECT * FROM borrow_records WHERE user_id = ?", userId);
    }

    public List<BorrowRecord> getActiveBorrowRecords() {
        return fetchBorrowRecords("SELECT * FROM borrow_records WHERE returned = false");
    }

    public List<BorrowRecord> getOverdueBorrowRecords() {
        return fetchBorrowRecords("SELECT * FROM borrow_records WHERE returned = false AND due_date < CURDATE()");
    }

    public Optional<BorrowRecord> getBorrowRecordById(String id) {
        List<BorrowRecord> list = fetchBorrowRecords("SELECT * FROM borrow_records WHERE id = ?", id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public double calculateFine(String recordId) {
        Optional<BorrowRecord> opt = getBorrowRecordById(recordId);
        if (opt.isEmpty()) return 0.0;

        BorrowRecord record = opt.get();

        if (record.isReturned()) return record.getFine();

        if (record.getDueDate().isBefore(LocalDate.now())) {
            long daysLate = LocalDate.now().toEpochDay() - record.getDueDate().toEpochDay();
            return daysLate * 1.0;  // Fine per day calculation; adjust as needed
        }

        return 0.0;
    }

    private List<BorrowRecord> fetchBorrowRecords(String sql, String... params) {
        List<BorrowRecord> records = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                pstmt.setString(i + 1, params[i]);
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                records.add(new BorrowRecord(
                        rs.getString("id"),
                        rs.getString("book_id"),
                        rs.getString("user_id"),
                        rs.getDate("borrow_date").toLocalDate(),
                        rs.getDate("due_date").toLocalDate(),
                        rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null,
                        rs.getBoolean("returned"),
                        rs.getDouble("fine")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching borrow records: " + e.getMessage());
        }

        return records;
    }
}
