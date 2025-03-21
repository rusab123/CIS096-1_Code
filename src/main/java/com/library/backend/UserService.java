package com.library.backend;

import com.library.models.Admin;
import com.library.models.Student;
import com.library.models.Teacher;
import com.library.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service to handle user-related operations
 * Implemented using the Singleton pattern
 */
public class UserService {
    private static UserService instance;
    private final Map<String, User> users;
    
    // Private constructor to prevent direct instantiation
    private UserService() {
        users = new HashMap<>();
        initializeDefaultUsers();
    }
    
    // Static method to get the singleton instance
    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }
    
    private void initializeDefaultUsers() {
        // Add some default users
        Admin admin = new Admin("Admin User", "admin@library.com", "admin123", "ADM001", "System Administrator");
        Student student1 = new Student("John Doe", "john@university.edu", "pass123", "STU001", "Computer Science");
        Student student2 = new Student("Jane Smith", "jane@university.edu", "pass456", "STU002", "Literature");
        Teacher teacher = new Teacher("Prof. Johnson", "johnson@university.edu", "prof123", "TCH001", "Physics", "Professor");
        
        users.put(admin.getId(), admin);
        users.put(student1.getId(), student1);
        users.put(student2.getId(), student2);
        users.put(teacher.getId(), teacher);
    }
    
    public User addUser(User user) {
        users.put(user.getId(), user);
        return user;
    }
    
    public Optional<User> getUserById(String id) {
        return Optional.ofNullable(users.get(id));
    }
    
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
    
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        
        for (User user : users.values()) {
            if (user instanceof Student) {
                students.add((Student) user);
            }
        }
        
        return students;
    }
    
    public List<Teacher> getAllTeachers() {
        List<Teacher> teachers = new ArrayList<>();
        
        for (User user : users.values()) {
            if (user instanceof Teacher) {
                teachers.add((Teacher) user);
            }
        }
        
        return teachers;
    }
    
    public List<Admin> getAllAdmins() {
        List<Admin> admins = new ArrayList<>();
        
        for (User user : users.values()) {
            if (user instanceof Admin) {
                admins.add((Admin) user);
            }
        }
        
        return admins;
    }
    
    public boolean updateUser(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return true;
        }
        return false;
    }
    
    public boolean deleteUser(String id) {
        return users.remove(id) != null;
    }
    
    // Login method
    public Optional<User> login(String email, String password) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
} 