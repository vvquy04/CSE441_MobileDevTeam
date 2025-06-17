package com.example.tluofficehours.model;

public class RegisterFacultyRequest {
    private String email;
    private String password;
    private String password_confirmation;
    private String FacultyName;
    private String DepartmentId;
    private String Biography;
    private String PhoneNumber;
    private String OfficeRoom;

    public RegisterFacultyRequest(String email, String password, String password_confirmation, String FacultyName, String DepartmentId, String Biography, String PhoneNumber, String OfficeRoom) {
        this.email = email;
        this.password = password;
        this.password_confirmation = password_confirmation;
        this.FacultyName = FacultyName;
        this.DepartmentId = DepartmentId;
        this.Biography = Biography;
        this.PhoneNumber = PhoneNumber;
        this.OfficeRoom = OfficeRoom;
    }

    // Getters if needed
} 