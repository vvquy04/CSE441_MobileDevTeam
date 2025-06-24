package com.example.tluofficehours.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("UserId")
    private String userId;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("email_verified_at")
    private String emailVerifiedAt;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("updated_at")
    private String updatedAt;
    
    // Getters
    public String getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getEmailVerifiedAt() { return emailVerifiedAt; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    
    // Setters
    public void setUserId(String userId) { this.userId = userId; }
    public void setEmail(String email) { this.email = email; }
    public void setEmailVerifiedAt(String emailVerifiedAt) { this.emailVerifiedAt = emailVerifiedAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
} 