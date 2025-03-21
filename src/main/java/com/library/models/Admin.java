package com.library.models;

/**
 * Represents an admin user in the library system
 */
public class Admin extends User {
    private String adminId;
    private String role;
    
    public Admin(String name, String email, String password, String adminId, String role) {
        super(name, email, password);
        this.adminId = adminId;
        this.role = role;
    }
    
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
    public String getUserType() {
        return "ADMIN";
    }
    
    @Override
    public String toString() {
        return "Admin{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", adminId='" + adminId + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
} 