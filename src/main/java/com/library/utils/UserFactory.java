package com.library.utils;

import com.library.models.Admin;
import com.library.models.Student;
import com.library.models.Teacher;
import com.library.models.User;

/**
 * Factory class for creating different types of users
 * Implemented using the Factory design pattern
 */
public class UserFactory {
    
    public enum UserType {
        STUDENT,
        TEACHER,
        ADMIN
    }
    
    /**
     * Creates a user based on the specified type
     * 
     * @param type the type of user to create
     * @param name the name of the user
     * @param email the email of the user
     * @param password the password of the user
     * @param id the ID specific to the user type (studentId, teacherId, adminId)
     * @param attribute1 first additional attribute (department for Student/Teacher, role for Admin)
     * @param attribute2 second additional attribute (designation for Teacher, null for others)
     * @return the created User object
     */
    public static User createUser(UserType type, String name, String email, String password, 
                                 String id, String attribute1, String attribute2) {
        switch (type) {
            case STUDENT:
                return new Student(name, email, password, id, attribute1);
                
            case TEACHER:
                return new Teacher(name, email, password, id, attribute1, attribute2);
                
            case ADMIN:
                return new Admin(name, email, password, id, attribute1);
                
            default:
                throw new IllegalArgumentException("Invalid user type: " + type);
        }
    }
    
    /**
     * Creates a student user
     * 
     * @param name the name of the student
     * @param email the email of the student
     * @param password the password of the student
     * @param studentId the student ID
     * @param department the department of the student
     * @return the created Student object
     */
    public static Student createStudent(String name, String email, String password, 
                                       String studentId, String department) {
        return new Student(name, email, password, studentId, department);
    }
    
    /**
     * Creates a teacher user
     * 
     * @param name the name of the teacher
     * @param email the email of the teacher
     * @param password the password of the teacher
     * @param teacherId the teacher ID
     * @param department the department of the teacher
     * @param designation the designation of the teacher
     * @return the created Teacher object
     */
    public static Teacher createTeacher(String name, String email, String password, 
                                       String teacherId, String department, String designation) {
        return new Teacher(name, email, password, teacherId, department, designation);
    }
    
    /**
     * Creates an admin user
     * 
     * @param name the name of the admin
     * @param email the email of the admin
     * @param password the password of the admin
     * @param adminId the admin ID
     * @param role the role of the admin
     * @return the created Admin object
     */
    public static Admin createAdmin(String name, String email, String password, 
                                   String adminId, String role) {
        return new Admin(name, email, password, adminId, role);
    }
} 