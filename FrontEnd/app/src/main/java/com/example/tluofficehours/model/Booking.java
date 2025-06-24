package com.example.tluofficehours.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Booking implements Serializable {
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
    
    @SerializedName("booking_date")
    private String bookingDate;
    
    @SerializedName("booking_time_range")
    private String bookingTimeRange;
    
    @SerializedName("student")
    private Student student;
    
    @SerializedName("slot")
    private Slot slot;
    
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
    public String getBookingDate() { return bookingDate; }
    public String getBookingTimeRange() { return bookingTimeRange; }
    public Student getStudent() { return student; }
    public Slot getSlot() { return slot; }
    
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
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }
    public void setBookingTimeRange(String bookingTimeRange) { this.bookingTimeRange = bookingTimeRange; }
    public void setStudent(Student student) { this.student = student; }
    public void setSlot(Slot slot) { this.slot = slot; }
}