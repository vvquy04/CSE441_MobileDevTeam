package com.example.tluofficehours.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class AvailableSlot implements Serializable {
    @SerializedName("slot_id")
    private int slotId;
    
    @SerializedName("faculty_user_id")
    private int facultyUserId;
    
    @SerializedName(value = "start_time", alternate = {"StartTime"})
    private String startTime;
    
    @SerializedName(value = "end_time", alternate = {"EndTime"})
    private String endTime;
    
    @SerializedName("max_students")
    private int maxStudents;
    
    @SerializedName("is_available")
    private boolean isAvailable;
    
    @SerializedName("definition_id")
    private Integer definitionId;
    
    @SerializedName("bookings")
    private List<Booking> bookings;

    // Constructors
    public AvailableSlot() {}
    
    public AvailableSlot(int slotId, int facultyUserId, String startTime, String endTime, 
                        int maxStudents, boolean isAvailable) {
        this.slotId = slotId;
        this.facultyUserId = facultyUserId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxStudents = maxStudents;
        this.isAvailable = isAvailable;
    }

    // Getters and Setters
    public int getSlotId() { return slotId; }
    public void setSlotId(int slotId) { this.slotId = slotId; }
    
    public int getFacultyUserId() { return facultyUserId; }
    public void setFacultyUserId(int facultyUserId) { this.facultyUserId = facultyUserId; }
    
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    
    public int getMaxStudents() { return maxStudents; }
    public void setMaxStudents(int maxStudents) { this.maxStudents = maxStudents; }
    
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    
    public Integer getDefinitionId() { return definitionId; }
    public void setDefinitionId(Integer definitionId) { this.definitionId = definitionId; }
    
    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }
    
    // Helper methods
    public int getCurrentBookingsCount() {
        if (bookings == null) return 0;
        return (int) bookings.stream()
                .filter(booking -> "pending".equals(booking.getStatus()) || 
                                 "confirmed".equals(booking.getStatus()))
                .count();
    }
    
    public int getAvailableSpots() {
        return Math.max(0, maxStudents - getCurrentBookingsCount());
    }
    
    public boolean isFull() {
        return getCurrentBookingsCount() >= maxStudents;
    }
} 