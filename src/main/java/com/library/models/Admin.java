package com.library.models;

public class Admin extends User {
    private String adminId;
    private String role;

    // Constructor for new Admin (id will be set later, such as from DB after insertion)
    public Admin(String name, String email, String password, String adminId, String role) {
        super(null, name, email, password);  // id is set as null for now, it will be updated after DB insertion
        this.adminId = adminId;
        this.role = role;
    }

    // Constructor for loading from DB (id is provided by DB)
    public Admin(String id, String name, String email, String password, String adminId, String role) {
        super(id, name, email, password);  // Use the constructor with 4 arguments where ID is provided by DB
        this.adminId = adminId;
        this.role = role;
    }

    @Override
    public String getUserType() {
        return "ADMIN";
    }

    @Override
    public boolean canBorrowBooks() {
        return false; // Admins cannot borrow books
    }

    // Getters and Setters
    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminId='" + adminId + '\'' +
                ", role='" + role + '\'' +
                "} " + super.toString();
    }
}
