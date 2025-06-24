package com.example.tluofficehours.model;

import com.google.gson.annotations.SerializedName;

public class FacultyProfile {
    @SerializedName("faculty_user_id")
    private String facultyUserId;
    
    @SerializedName("faculty_name")
    private String facultyName;
    
    @SerializedName("department_id")
    private String departmentId;
    
    @SerializedName("department_name")
    private String departmentName;
    
    @SerializedName("degree")
    private String degree;
    
    @SerializedName("phone_number")
    private String phoneNumber;
    
    @SerializedName("office_location")
    private String officeLocation;
    
    @SerializedName("avatar")
    private String avatar;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("updated_at")
    private String updatedAt;
    
    @SerializedName("avatar_url")
    private String avatarUrl;
    
    // Getters
    public String getFacultyUserId() { return facultyUserId; }
    public String getFacultyName() { return facultyName; }
    public String getDepartmentId() { return departmentId; }
    public String getDepartmentName() { return departmentName; }
    public String getDegree() { return degree; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getOfficeLocation() { return officeLocation; }
    public String getAvatar() { return avatar; }
    public String getEmail() { return email; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public String getAvatarUrl() { return avatarUrl; }
    
    // Setters
    public void setFacultyUserId(String facultyUserId) { this.facultyUserId = facultyUserId; }
    public void setFacultyName(String facultyName) { this.facultyName = facultyName; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public void setDegree(String degree) { this.degree = degree; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setOfficeLocation(String officeLocation) { this.officeLocation = officeLocation; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public void setEmail(String email) { this.email = email; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
} 