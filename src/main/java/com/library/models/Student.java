package com.library.models;

import java.util.HashSet;
import java.util.Set;

public class Student extends User {
    private String studentId;
    private String department;
    private Set<String> borrowedBooks;  // A set to store borrowed book IDs

    // Constructor for new Student (id will be set later, such as from DB after insertion)
    public Student(String name, String email, String password, String studentId, String department) {
        super(null, name, email, password);  // id is set as null for now, it will be updated after DB insertion
        this.studentId = studentId;
        this.department = department;
        this.borrowedBooks = new HashSet<>();
    }

    // Constructor for loading from DB
    public Student(String id, String name, String email, String password, String studentId, String department) {
        super(id, name, email, password);  // Use the constructor with 4 arguments where ID is provided by DB
        this.studentId = studentId;
        this.department = department;
        this.borrowedBooks = new HashSet<>();
    }

    @Override
    public String getUserType() {
        return "STUDENT";
    }

    @Override
    public boolean canBorrowBooks() {
        return true;  // No limit on book borrowing
    }

    // Method to borrow a book (no quota check)
    public boolean borrowBook(String bookId) {
        borrowedBooks.add(bookId);
        System.out.println("Book borrowed. Total borrowed books: " + borrowedBooks.size());  // Debug print
        return true;
    }

    // Method to return a book
    public boolean returnBook(String bookId) {
        if (borrowedBooks.contains(bookId)) {
            borrowedBooks.remove(bookId);  // Remove the book from the borrowed list
            return true;
        }
        return false;  // If the student has not borrowed this book
    }

    // Getter for borrowed books
    public Set<String> getBorrowedBooks() {
        return borrowedBooks;
    }

    // Getter for studentId
    public String getStudentId() {
        return studentId;
    }

    // Setter for studentId
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    // Getter for department
    public String getDepartment() {
        return department;
    }

    // Setter for department
    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId='" + studentId + '\'' +
                ", department='" + department + '\'' +
                ", borrowedBooks=" + borrowedBooks +
                "} " + super.toString();
    }
}
