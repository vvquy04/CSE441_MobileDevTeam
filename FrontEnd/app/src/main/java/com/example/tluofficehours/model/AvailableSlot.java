package com.example.tluofficehours.model;

import android.widget.Button;
import com.google.gson.annotations.SerializedName;

public class AvailableSlot {
    @SerializedName("SlotId")
    private String slotId;
    
    @SerializedName("faculty_user_id")
    private String facultyUserId;
    
    @SerializedName("StartTime")
    private String startTime;
    
    @SerializedName("EndTime")
    private String endTime;
    
    @SerializedName("MaxStudents")
    private int maxStudents;
    
    @SerializedName("IsAvailable")
    private boolean isAvailable;
    
    @SerializedName("DefinitionId")
    private String definitionId;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("updated_at")
    private String updatedAt;
    
    // UI state properties
    private boolean isSelected = false;
    private Button buttonView;
    
    // Getters
    public String getSlotId() { return slotId; }
    public String getFacultyUserId() { return facultyUserId; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public int getMaxStudents() { return maxStudents; }
    public boolean isAvailable() { return isAvailable; }
    public String getDefinitionId() { return definitionId; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public boolean isSelected() { return isSelected; }
    public Button getButtonView() { return buttonView; }
    
    // Utility method to get display time
    public String getTimeDisplay() {
        try {
            // Parse startTime và endTime từ ISO format
            String startTimeStr = startTime;
            String endTimeStr = endTime;
            
            // Nếu là ISO format (có T và Z), extract phần thời gian
            if (startTimeStr != null && startTimeStr.contains("T")) {
                startTimeStr = startTimeStr.substring(startTimeStr.indexOf("T") + 1, startTimeStr.indexOf("T") + 6);
            }
            if (endTimeStr != null && endTimeStr.contains("T")) {
                endTimeStr = endTimeStr.substring(endTimeStr.indexOf("T") + 1, endTimeStr.indexOf("T") + 6);
            }
            
            return startTimeStr + " - " + endTimeStr;
        } catch (Exception e) {
            // Fallback nếu có lỗi parsing
            return startTime + " - " + endTime;
        }
    }
    
    // Setters
    public void setSlotId(String slotId) { this.slotId = slotId; }
    public void setFacultyUserId(String facultyUserId) { this.facultyUserId = facultyUserId; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public void setMaxStudents(int maxStudents) { this.maxStudents = maxStudents; }
    public void setAvailable(boolean available) { isAvailable = available; }
    public void setDefinitionId(String definitionId) { this.definitionId = definitionId; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public void setSelected(boolean selected) { isSelected = selected; }
    public void setButtonView(Button buttonView) { this.buttonView = buttonView; }
}
