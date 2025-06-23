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
    
    public static class User {
        @SerializedName("UserId")
        private String userId;
        
        @SerializedName("email")
        private String email;
        
        public String getUserId() { return userId; }
        public String getEmail() { return email; }
    }
    
    public static class Profile {
        @SerializedName("StudentName")
        private String studentName;
        
        @SerializedName("StudentCode")
        private String studentCode;
        
        @SerializedName("ClassName")
        private String className;
        
        @SerializedName("PhoneNumber")
        private String phoneNumber;
        
        public String getStudentName() { return studentName; }
        public String getStudentCode() { return studentCode; }
        public String getClassName() { return className; }
        public String getPhoneNumber() { return phoneNumber; }
    }
    
    public User getUser() { return user; }
    public String[] getRoles() { return roles; }
    public Profile getProfile() { return profile; }
    public String getAvatarUrl() { return avatarUrl; }
} 