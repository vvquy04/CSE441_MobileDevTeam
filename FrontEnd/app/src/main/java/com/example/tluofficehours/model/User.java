package com.example.tluofficehours.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class User implements Serializable {
    @SerializedName("UserId")
    private int userId;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("faculty_profile")
    private FacultyProfile facultyProfile;
    
    @SerializedName("student_profile")
    private StudentProfile studentProfile;

    // Constructors
    public User() {}
    
    public User(int userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public FacultyProfile getFacultyProfile() { return facultyProfile; }
    public void setFacultyProfile(FacultyProfile facultyProfile) { this.facultyProfile = facultyProfile; }
    
    public StudentProfile getStudentProfile() { return studentProfile; }
    public void setStudentProfile(StudentProfile studentProfile) { this.studentProfile = studentProfile; }
    
    // Helper methods
    public String getDisplayName() {
        if (facultyProfile != null) {
            return facultyProfile.getFacultyName();
        } else if (studentProfile != null) {
            return studentProfile.getStudentName();
        }
        return email;
    }
    
    public String getAvatar() {
        if (facultyProfile != null) {
            return facultyProfile.getAvatar();
        } else if (studentProfile != null) {
            return studentProfile.getAvatar();
        }
        return null;
    }
} 