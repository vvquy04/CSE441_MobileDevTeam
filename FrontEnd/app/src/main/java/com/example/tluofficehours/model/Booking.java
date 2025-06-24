package com.example.tluofficehours.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Booking implements Serializable {
    @SerializedName("BookingId")
    private int bookingId;
    
    @SerializedName("SlotId")
    private int slotId;
    
    @SerializedName("StudentUserId")
    private int studentUserId;
    
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
    
    @SerializedName("student")
    private User student;
    
    @SerializedName("slot")
    private AvailableSlot slot;

    // Constructors
    public Booking() {}
    
    public Booking(int bookingId, int slotId, int studentUserId, String bookingTime, 
                   String purpose, String status) {
        this.bookingId = bookingId;
        this.slotId = slotId;
        this.studentUserId = studentUserId;
        this.bookingTime = bookingTime;
        this.purpose = purpose;
        this.status = status;
    }

    // Getters and Setters
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    
    public int getSlotId() { return slotId; }
    public void setSlotId(int slotId) { this.slotId = slotId; }
    
    public int getStudentUserId() { return studentUserId; }
    public void setStudentUserId(int studentUserId) { this.studentUserId = studentUserId; }
    
    public String getBookingTime() { return bookingTime; }
    public void setBookingTime(String bookingTime) { this.bookingTime = bookingTime; }
    
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getCancellationTime() { return cancellationTime; }
    public void setCancellationTime(String cancellationTime) { this.cancellationTime = cancellationTime; }
    
    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }
    
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    
    public AvailableSlot getSlot() { return slot; }
    public void setSlot(AvailableSlot slot) { this.slot = slot; }
} 