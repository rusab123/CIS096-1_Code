package com.library.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a teacher user in the library system
 */
public class Teacher extends User {
    private String teacherId;
    private String department;
    private String designation;
    private final List<String> borrowedBooks;
    private static final int MAX_BOOKS_ALLOWED = 5;
    
    public Teacher(String name, String email, String password, String teacherId, String department, String designation) {
        super(name, email, password);
        this.teacherId = teacherId;
        this.department = department;
        this.designation = designation;
        this.borrowedBooks = new ArrayList<>();
    }
    
    public String getTeacherId() {
        return teacherId;
    }
    
    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getDesignation() {
        return designation;
    }
    
    public void setDesignation(String designation) {
        this.designation = designation;
    }
    
    public List<String> getBorrowedBooks() {
        return new ArrayList<>(borrowedBooks);
    }
    
    public boolean canBorrowBooks() {
        return borrowedBooks.size() < MAX_BOOKS_ALLOWED;
    }
    
    public boolean borrowBook(String bookId) {
        if (canBorrowBooks()) {
            borrowedBooks.add(bookId);
            return true;
        }
        return false;
    }
    
    public boolean returnBook(String bookId) {
        return borrowedBooks.remove(bookId);
    }
    
    public int getRemainingBookQuota() {
        return MAX_BOOKS_ALLOWED - borrowedBooks.size();
    }
    
    @Override
    public String getUserType() {
        return "TEACHER";
    }
    
    @Override
    public String toString() {
        return "Teacher{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", teacherId='" + teacherId + '\'' +
                ", department='" + department + '\'' +
                ", designation='" + designation + '\'' +
                ", borrowedBooks=" + borrowedBooks +
                '}';
    }
} 