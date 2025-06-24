package com.example.tluofficehours.model;

import com.google.gson.annotations.SerializedName;

public class UserRole {
    @SerializedName("UserRoleId")
    private String userRoleId;
    
    @SerializedName("UserId")
    private String userId;
    
    @SerializedName("RoleId")
    private String roleId;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("updated_at")
    private String updatedAt;
    
    // Getters
    public String getUserRoleId() { return userRoleId; }
    public String getUserId() { return userId; }
    public String getRoleId() { return roleId; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    
    // Setters
    public void setUserRoleId(String userRoleId) { this.userRoleId = userRoleId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setRoleId(String roleId) { this.roleId = roleId; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
} 