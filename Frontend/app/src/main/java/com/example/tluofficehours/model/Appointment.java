package com.example.tluofficehours.model;

import com.google.gson.annotations.SerializedName;

public class Appointment {
    private int id;
    private String teacherName;
    private String department;
    private String time;
    private String date;
    private String room;
    private String purpose;
    private String status; // CONFIRMED, CANCELLED, PENDING, COMPLETED
    private String cancellationReason;
    private String avatarUrl;
    @SerializedName("faculty_user_id")
    private String facultyUserId;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }

    public String getAvatarUrl() {
        return avatarUrl;
    }
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getFacultyUserId() { return facultyUserId; }
    public void setFacultyUserId(String facultyUserId) { this.facultyUserId = facultyUserId; }
} 