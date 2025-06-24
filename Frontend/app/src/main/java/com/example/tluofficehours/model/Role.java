package com.example.tluofficehours.model;

import com.google.gson.annotations.SerializedName;

public class Role {
    @SerializedName("RoleId")
    private String roleId;
    
    @SerializedName("RoleName")
    private String roleName;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("updated_at")
    private String updatedAt;
    
    // Getters
    public String getRoleId() { return roleId; }
    public String getRoleName() { return roleName; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    
    // Setters
    public void setRoleId(String roleId) { this.roleId = roleId; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
} 