package com.example.tluofficehours.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Student implements Serializable {
    @SerializedName("UserId")
    private String userId;
    @SerializedName("email")
    private String email;
    @SerializedName("student_profile")
    private StudentProfile studentProfile;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public StudentProfile getStudentProfile() { return studentProfile; }
    public void setStudentProfile(StudentProfile studentProfile) { this.studentProfile = studentProfile; }
} 