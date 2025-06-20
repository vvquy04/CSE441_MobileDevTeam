package com.example.tluofficehours.model;

import com.google.gson.annotations.SerializedName;

public class RegisterStudentRequest {
    @SerializedName("email")
    private String email;
    
    @SerializedName("password")
    private String password;
    
    @SerializedName("password_confirmation")
    private String password_confirmation;
    
    @SerializedName("StudentName")
    private String StudentName;
    
    @SerializedName("StudentCode")
    private String StudentCode;
    
    @SerializedName("ClassName")
    private String ClassName;
    
    @SerializedName("PhoneNumber")
    private String PhoneNumber;
    
    @SerializedName("avatar")
    private String avatar;

    public RegisterStudentRequest(String email, String password, String password_confirmation, String StudentName, String StudentCode, String ClassName, String PhoneNumber, String avatar) {
        this.email = email;
        this.password = password;
        this.password_confirmation = password_confirmation;
        this.StudentName = StudentName;
        this.StudentCode = StudentCode;
        this.ClassName = ClassName;
        this.PhoneNumber = PhoneNumber;
        this.avatar = avatar;
    }

    // Getters if needed
} 