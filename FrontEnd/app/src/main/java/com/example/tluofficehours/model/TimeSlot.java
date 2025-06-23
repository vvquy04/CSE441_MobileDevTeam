package com.example.tluofficehours.model;

import android.widget.Button;
import com.google.gson.annotations.SerializedName;

// TimeSlot.java
public class TimeSlot {
    public enum SlotStatus {
        AVAILABLE,    // Giảng viên rảnh, sinh viên có thể đặt
        BOOKED,       // Giảng viên rảnh nhưng slot đã có sinh viên khác đặt (ví dụ: sinh viên khác đã đặt)
        UNAVAILABLE   // Giảng viên bận hoặc không cung cấp slot này (ví dụ: giảng viên đã đánh dấu bận)
    }

    private int id; // SlotId từ backend
    private String time;
    private SlotStatus status;
    private boolean isSelected;
    private Button buttonView; // Tham chiếu đến Button View tương ứng
    @SerializedName("MaxStudents")
    private int maxStudents = 1; // Mặc định 1 nếu không truyền từ backend

    public TimeSlot(int id, String time, SlotStatus status) {
        this.id = id;
        this.time = time;
        this.status = status;
        this.isSelected = false; // Mặc định không được chọn
    }

    public String getTime() {
        return time;
    }

    public SlotStatus getStatus() {
        return status;
    }

    public void setStatus(SlotStatus status) {
        this.status = status;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Button getButtonView() {
        return buttonView;
    }

    public void setButtonView(Button buttonView) {
        this.buttonView = buttonView;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMaxStudents() {
        return maxStudents;
    }

    public void setMaxStudents(int maxStudents) {
        this.maxStudents = maxStudents;
    }
}