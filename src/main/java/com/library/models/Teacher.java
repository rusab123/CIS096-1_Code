package com.library.models;

public class Teacher extends User {
    private String teacherId;
    private String department;
    private String designation;

    // Constructor for new Teacher (id will be set later, such as from DB after insertion)
    public Teacher(String name, String email, String password, String teacherId, String department, String designation) {
        super(null, name, email, password);  // id is set as null for now, it will be updated after DB insertion
        this.teacherId = teacherId;
        this.department = department;
        this.designation = designation;
    }

    // Constructor for loading from DB (id is provided by DB)
    public Teacher(String id, String name, String email, String password, String teacherId, String department, String designation) {
        super(id, name, email, password);  // Use the constructor with 4 arguments where ID is provided by DB
        this.teacherId = teacherId;
        this.department = department;
        this.designation = designation;
    }

    @Override
    public String getUserType() {
        return "TEACHER";
    }

    @Override
    public boolean canBorrowBooks() {
        return true; // Teachers can borrow books without a limit
    }

    // Getters and Setters
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

    @Override
    public String toString() {
        return "Teacher{" +
                "teacherId='" + teacherId + '\'' +
                ", department='" + department + '\'' +
                ", designation='" + designation + '\'' +
                "} " + super.toString();
    }

    // Method to borrow a book (optional for teachers, if you want to add this functionality)
    public void borrowBook(String bookId) {
        System.out.println("Book borrowed by Teacher: " + bookId);
    }

    // Method to return a book (optional for teachers, if you want to add this functionality)
    public void returnBook(String bookId) {
        System.out.println("Book returned by Teacher: " + bookId);
    }
}
