package com.example.tluofficehours.model;

import com.google.gson.annotations.SerializedName;

public class RegisterFacultyRequest {
    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("password_confirmation")
    private String passwordConfirmation;

    @SerializedName("faculty_name")
    private String facultyName;

    @SerializedName("department_id")
    private String departmentId;

    @SerializedName("degree")
    private String degree;

    @SerializedName("phone_number")
    private String phoneNumber;

    @SerializedName("office_room")
    private String officeRoom;

    @SerializedName("avatar")
    private String avatar;

    public RegisterFacultyRequest(String email, String password, String passwordConfirmation,
                                String facultyName, String departmentId, String degree,
                                String phoneNumber, String officeRoom, String avatar) {
        this.email = email;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
        this.facultyName = facultyName;
        this.departmentId = departmentId;
        this.degree = degree;
        this.phoneNumber = phoneNumber;
        this.officeRoom = officeRoom;
        this.avatar = avatar;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
