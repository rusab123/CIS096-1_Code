package com.library.backend;

import com.library.models.BorrowRecord;
import com.library.models.Student;
import com.library.models.Teacher;
import com.library.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service to handle borrowing-related operations
 * Implemented using the Singleton pattern
 */
public class BorrowService {
    private static BorrowService instance;
    private final Map<String, BorrowRecord> borrowRecords;
    private final BookService bookService;
    private final UserService userService;
    
    // Default borrow days for different user types
    private static final int STUDENT_BORROW_DAYS = 14; // 2 weeks
    private static final int TEACHER_BORROW_DAYS = 30; // 1 month
    
    // Private constructor to prevent direct instantiation
    private BorrowService() {
        borrowRecords = new HashMap<>();
        bookService = BookService.getInstance();
        userService = UserService.getInstance();
    }
    
    // Static method to get the singleton instance
    public static synchronized BorrowService getInstance() {
        if (instance == null) {
            instance = new BorrowService();
        }
        return instance;
    }
    
    public Optional<BorrowRecord> borrowBook(String userId, String bookId) {
        // Check if user exists
        Optional<User> userOpt = userService.getUserById(userId);
        if (!userOpt.isPresent()) {
            return Optional.empty();
        }
        
        User user = userOpt.get();
        
        // Check if book exists and is available
        if (!bookService.borrowBook(bookId)) {
            return Optional.empty();
        }
        
        // Determine borrow days based on user type
        int borrowDays;
        if (user instanceof Student) {
            Student student = (Student) user;
            if (!student.canBorrowBooks()) {
                // Revert the book borrowing
                bookService.returnBook(bookId);
                return Optional.empty();
            }
            student.borrowBook(bookId);
            borrowDays = STUDENT_BORROW_DAYS;
        } else if (user instanceof Teacher) {
            Teacher teacher = (Teacher) user;
            if (!teacher.canBorrowBooks()) {
                // Revert the book borrowing
                bookService.returnBook(bookId);
                return Optional.empty();
            }
            teacher.borrowBook(bookId);
            borrowDays = TEACHER_BORROW_DAYS;
        } else {
            // Administrators cannot borrow books
            bookService.returnBook(bookId);
            return Optional.empty();
        }
        
        // Create and store the borrow record
        BorrowRecord record = new BorrowRecord(bookId, userId, borrowDays);
        borrowRecords.put(record.getId(), record);
        
        return Optional.of(record);
    }
    
    public Optional<BorrowRecord> returnBook(String recordId) {
        BorrowRecord record = borrowRecords.get(recordId);
        if (record == null || record.isReturned()) {
            return Optional.empty();
        }
        
        // Process book return
        if (!bookService.returnBook(record.getBookId())) {
            return Optional.empty();
        }
        
        // Update user's borrowed books
        Optional<User> userOpt = userService.getUserById(record.getUserId());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user instanceof Student) {
                ((Student) user).returnBook(record.getBookId());
            } else if (user instanceof Teacher) {
                ((Teacher) user).returnBook(record.getBookId());
            }
        }
        
        // Update borrow record
        record.returnBook();
        
        return Optional.of(record);
    }
    
    public List<BorrowRecord> getAllBorrowRecords() {
        return new ArrayList<>(borrowRecords.values());
    }
    
    public List<BorrowRecord> getBorrowRecordsByUserId(String userId) {
        return borrowRecords.values().stream()
                .filter(record -> record.getUserId().equals(userId))
                .collect(Collectors.toList());
    }
    
    public List<BorrowRecord> getActiveBorrowRecords() {
        return borrowRecords.values().stream()
                .filter(record -> !record.isReturned())
                .collect(Collectors.toList());
    }
    
    public List<BorrowRecord> getOverdueBorrowRecords() {
        return borrowRecords.values().stream()
                .filter(record -> !record.isReturned() && 
                        record.getDueDate().isBefore(java.time.LocalDate.now()))
                .collect(Collectors.toList());
    }
    
    public Optional<BorrowRecord> getBorrowRecordById(String id) {
        return Optional.ofNullable(borrowRecords.get(id));
    }
    
    public double calculateFine(String recordId) {
        BorrowRecord record = borrowRecords.get(recordId);
        if (record == null) {
            return 0.0;
        }
        
        if (record.isReturned()) {
            return record.getFine();
        } else if (record.getDueDate().isBefore(java.time.LocalDate.now())) {
            long daysLate = java.time.LocalDate.now().toEpochDay() - record.getDueDate().toEpochDay();
            return daysLate * 1.0; // $1 per day late
        }
        
        return 0.0;
    }
} 