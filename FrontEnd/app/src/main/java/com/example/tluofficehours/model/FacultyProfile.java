package com.example.tluofficehours.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class FacultyProfile implements Serializable {
    @SerializedName("faculty_user_id")
    private int facultyUserId;
    
    @SerializedName("faculty_name")
    private String facultyName;
    
    @SerializedName("department_id")
    private String departmentId;
    
    @SerializedName("degree")
    private String degree;
    
    @SerializedName("phone_number")
    private String phoneNumber;
    
    @SerializedName("office_location")
    private String officeLocation;
    
    @SerializedName("avatar")
    private String avatar;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("department")
    private Department department;

    // Constructors
    public FacultyProfile() {}
    
    public FacultyProfile(int facultyUserId, String facultyName, String departmentId, 
                         String degree, String phoneNumber, String officeLocation) {
        this.facultyUserId = facultyUserId;
        this.facultyName = facultyName;
        this.departmentId = departmentId;
        this.degree = degree;
        this.phoneNumber = phoneNumber;
        this.officeLocation = officeLocation;
    }
    
    public FacultyProfile(int facultyUserId, String facultyName, String departmentId, 
                         String degree, String phoneNumber, String officeLocation, String email) {
        this.facultyUserId = facultyUserId;
        this.facultyName = facultyName;
        this.departmentId = departmentId;
        this.degree = degree;
        this.phoneNumber = phoneNumber;
        this.officeLocation = officeLocation;
        this.email = email;
    }

    // Getters and Setters
    public int getFacultyUserId() { return facultyUserId; }
    public void setFacultyUserId(int facultyUserId) { this.facultyUserId = facultyUserId; }
    
    public String getFacultyName() { return facultyName; }
    public void setFacultyName(String facultyName) { this.facultyName = facultyName; }
    
    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
    
    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getOfficeLocation() { return officeLocation; }
    public void setOfficeLocation(String officeLocation) { this.officeLocation = officeLocation; }
    
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
    
    // Helper methods for compatibility
    public String getName() {
        return facultyName;
    }
    
    public String getFacultyId() {
        return String.valueOf(facultyUserId);
    }
    
    public String getPhone() {
        return phoneNumber;
    }
    
    public String getDepartmentName() {
        if (department != null) {
            return department.getName();
        }
        return departmentId; // Fallback to department ID if department object is null
    }
    
    public void setFacultyId(String facultyId) {
        try {
            this.facultyUserId = Integer.parseInt(facultyId);
        } catch (NumberFormatException e) {
            // Nếu không parse được thành int, có thể lưu vào một field khác
            // hoặc bỏ qua
        }
    }
    
    public void setDepartment(String departmentName) {
        // Tạo Department object mới hoặc set departmentId
        this.department = new Department("", departmentName);
    }
    
    public void setPhone(String phone) {
        this.phoneNumber = phone;
    }
    
    public void setName(String name) {
        this.facultyName = name;
    }
} 