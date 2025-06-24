package com.example.tluofficehours.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class StudentProfile implements Serializable {
    @SerializedName("StudentUserId")
    private int studentUserId;
    
    @SerializedName("StudentName")
    private String studentName;
    
    @SerializedName("StudentCode")
    private String studentCode;
    
    @SerializedName("ClassName")
    private String className;
    
    @SerializedName("PhoneNumber")
    private String phoneNumber;
    
    @SerializedName("avatar")
    private String avatar;
    
    @SerializedName("department")
    private Department department;

    // Constructors
    public StudentProfile() {}
    
    public StudentProfile(int studentUserId, String studentName, String studentCode, 
                         String className, String phoneNumber) {
        this.studentUserId = studentUserId;
        this.studentName = studentName;
        this.studentCode = studentCode;
        this.className = className;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters
    public int getStudentUserId() { return studentUserId; }
    public void setStudentUserId(int studentUserId) { this.studentUserId = studentUserId; }
    
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    
    public String getStudentCode() { return studentCode; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }
    
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
} 