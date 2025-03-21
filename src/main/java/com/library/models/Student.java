package com.library.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a student user in the library system
 */
public class Student extends User {
    private String studentId;
    private String department;
    private final List<String> borrowedBooks;
    private static final int MAX_BOOKS_ALLOWED = 3;
    
    public Student(String name, String email, String password, String studentId, String department) {
        super(name, email, password);
        this.studentId = studentId;
        this.department = department;
        this.borrowedBooks = new ArrayList<>();
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
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
        return "STUDENT";
    }
    
    @Override
    public String toString() {
        return "Student{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", studentId='" + studentId + '\'' +
                ", department='" + department + '\'' +
                ", borrowedBooks=" + borrowedBooks +
                '}';
    }
} 