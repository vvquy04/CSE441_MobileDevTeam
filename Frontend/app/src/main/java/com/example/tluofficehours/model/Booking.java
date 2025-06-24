package com.example.tluofficehours.model;

import com.google.gson.annotations.SerializedName;

public class Booking {
    @SerializedName("BookingId")
    private String bookingId;
    
    @SerializedName("SlotId")
    private String slotId;
    
    @SerializedName("StudentUserId")
    private String studentUserId;
    
    @SerializedName("BookingTime")
    private String bookingTime;
    
    @SerializedName("Purpose")
    private String purpose;
    
    @SerializedName("Status")
    private String status;
    
    @SerializedName("CancellationTime")
    private String cancellationTime;
    
    @SerializedName("CancellationReason")
    private String cancellationReason;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("updated_at")
    private String updatedAt;
    
    // Getters
    public String getBookingId() { return bookingId; }
    public String getSlotId() { return slotId; }
    public String getStudentUserId() { return studentUserId; }
    public String getBookingTime() { return bookingTime; }
    public String getPurpose() { return purpose; }
    public String getStatus() { return status; }
    public String getCancellationTime() { return cancellationTime; }
    public String getCancellationReason() { return cancellationReason; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    
    // Setters
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public void setSlotId(String slotId) { this.slotId = slotId; }
    public void setStudentUserId(String studentUserId) { this.studentUserId = studentUserId; }
    public void setBookingTime(String bookingTime) { this.bookingTime = bookingTime; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public void setStatus(String status) { this.status = status; }
    public void setCancellationTime(String cancellationTime) { this.cancellationTime = cancellationTime; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
} 