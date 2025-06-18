package com.example.tluofficehours.model;

import com.google.gson.annotations.SerializedName;

public class Department {
    @SerializedName("DepartmentId")
    private String departmentId;

    @SerializedName("Name")
    private String name;

    public Department(String departmentId, String name) {
        this.departmentId = departmentId;
        this.name = name;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name; // Hiển thị tên bộ môn trong spinner
    }
} 