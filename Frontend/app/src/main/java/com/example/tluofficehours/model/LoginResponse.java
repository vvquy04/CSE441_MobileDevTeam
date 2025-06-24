package com.example.tluofficehours.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("access_token")
    private String accessToken;
    
    @SerializedName("token_type")
    private String tokenType;
    
    @SerializedName("user")
    private User user;
    
    @SerializedName("roles")
    private String[] roles;
    
    public static class User {
        @SerializedName("UserId")
        private String userId;
        
        @SerializedName("email")
        private String email;
        
        public String getUserId() { return userId; }
        public String getEmail() { return email; }
    }
    
    public String getAccessToken() { return accessToken; }
    public String getTokenType() { return tokenType; }
    public User getUser() { return user; }
    public String[] getRoles() { return roles; }
} 