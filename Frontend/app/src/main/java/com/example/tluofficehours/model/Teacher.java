package com.example.tluofficehours.model;

import com.google.gson.annotations.SerializedName;

public class Teacher {
    @SerializedName("faculty_user_id")
    private String facultyUserId;
    
    @SerializedName("faculty_name")
    private String facultyName;
    
    @SerializedName("degree")
    private String degree;
    
    @SerializedName("phone_number")
    private String phoneNumber;
    
    @SerializedName("office_location")
    private String officeLocation;
    
    @SerializedName("avatar")
    private String avatar;
    
    @SerializedName("department_name")
    private String departmentName;
    
    @SerializedName("avatar_url")
    private String avatarUrl;
    
    @SerializedName("email")
    private String email;
    
    public String getFacultyUserId() { return facultyUserId; }
    public String getFacultyName() { return facultyName; }
    public String getDegree() { return degree; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getOfficeLocation() { return officeLocation; }
    public String getAvatar() { return avatar; }
    public String getDepartmentName() { return departmentName; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getEmail() { return email; }
} 