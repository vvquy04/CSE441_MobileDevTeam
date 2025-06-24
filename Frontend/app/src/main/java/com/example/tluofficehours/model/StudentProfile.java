package com.example.tluofficehours.model;

import com.google.gson.annotations.SerializedName;

public class StudentProfile {
    @SerializedName("user")
    private User user;
    
    @SerializedName("roles")
    private String[] roles;
    
    @SerializedName("profile")
    private Profile profile;
    
    @SerializedName("avatar_url")
    private String avatarUrl;
    
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
    
    public static class User {
        @SerializedName("UserId")
        private String userId;
        
        @SerializedName("email")
        private String email;
        
        public String getUserId() { return userId; }
        public String getEmail() { return email; }
    }
    
    public static class Profile {
        @SerializedName("StudentUserId")
        private String studentUserId;
        
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
        
        public String getStudentUserId() { return studentUserId; }
        public String getStudentName() { return studentName; }
        public String getStudentCode() { return studentCode; }
        public String getClassName() { return className; }
        public String getPhoneNumber() { return phoneNumber; }
        public String getAvatar() { return avatar; }
    }
    
    public User getUser() { return user; }
    public String[] getRoles() { return roles; }
    public Profile getProfile() { return profile; }
    public String getAvatarUrl() { return avatarUrl; }
    
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
} 