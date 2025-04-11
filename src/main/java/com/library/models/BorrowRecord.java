package com.library.models;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a record of a book being borrowed
 */
public class BorrowRecord {
    private final String id;
    private final String bookId;
    private final String userId;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;
    private boolean returned;
    private double fine;

    // Constructor for new borrow (user borrows now)
    public BorrowRecord(String bookId, String userId, int borrowDays) {
        this.id = UUID.randomUUID().toString();
        this.bookId = bookId;
        this.userId = userId;
        this.borrowDate = LocalDate.now();
        this.dueDate = borrowDate.plusDays(borrowDays);
        this.returnDate = null;
        this.returned = false;
        this.fine = 0.0;
    }

    // Constructor for loading from database
    public BorrowRecord(String id, String bookId, String userId,
                        LocalDate borrowDate, LocalDate dueDate,
                        LocalDate returnDate, boolean returned, double fine) {
        this.id = id;
        this.bookId = bookId;
        this.userId = userId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.returned = returned;
        this.fine = fine;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getBookId() {
        return bookId;
    }

    public String getUserId() {
        return userId;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public boolean isReturned() {
        return returned;
    }

    public double getFine() {
        return fine;
    }

    // Setters
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    public void setFine(double fine) {
        this.fine = fine;
    }

    /**
     * Sets returnDate to now and calculates fine if overdue
     */
    public void returnBook() {
        this.returnDate = LocalDate.now();
        this.returned = true;

        if (returnDate.isAfter(dueDate)) {
            long daysLate = returnDate.toEpochDay() - dueDate.toEpochDay();
            this.fine = daysLate * 1.0;
        } else {
            this.fine = 0.0;
        }
    }

    @Override
    public String toString() {
        return "BorrowRecord{" +
                "id='" + id + '\'' +
                ", bookId='" + bookId + '\'' +
                ", userId='" + userId + '\'' +
                ", borrowDate=" + borrowDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                ", returned=" + returned +
                ", fine=" + fine +
                '}';
    }
}
