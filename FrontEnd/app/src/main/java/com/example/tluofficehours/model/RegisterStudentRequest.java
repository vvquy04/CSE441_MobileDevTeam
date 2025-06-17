package com.example.tluofficehours.model;

public class RegisterStudentRequest {
    private String email;
    private String password;
    private String password_confirmation;
    private String StudentName;
    private String StudentCode;
    private String DepartmentId;
    private String PhoneNumber;

    public RegisterStudentRequest(String email, String password, String password_confirmation, String StudentName, String StudentCode, String DepartmentId, String PhoneNumber) {
        this.email = email;
        this.password = password;
        this.password_confirmation = password_confirmation;
        this.StudentName = StudentName;
        this.StudentCode = StudentCode;
        this.DepartmentId = DepartmentId;
        this.PhoneNumber = PhoneNumber;
    }

    // Getters if needed
} 