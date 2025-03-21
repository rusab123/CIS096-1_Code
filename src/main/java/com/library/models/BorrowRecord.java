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
    private LocalDate dueDate;
    private LocalDate returnDate;
    private boolean returned;
    private double fine;
    
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
    
    // Getters and Setters
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
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
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
    
    public void setFine(double fine) {
        this.fine = fine;
    }
    
    public void returnBook() {
        this.returnDate = LocalDate.now();
        this.returned = true;
        
        // Calculate fine if returned after due date
        if (returnDate.isAfter(dueDate)) {
            long daysLate = returnDate.toEpochDay() - dueDate.toEpochDay();
            this.fine = daysLate * 1.0; // $1 per day late
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