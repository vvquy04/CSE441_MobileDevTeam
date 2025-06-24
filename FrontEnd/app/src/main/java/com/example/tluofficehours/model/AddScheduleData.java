package com.example.tluofficehours.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AddScheduleData implements Serializable {
    private boolean isSpecificDateMode;
    private String selectedDate;
    private List<TimeSlot> timeSlots;
    private List<DayTimeSlot> dayTimeSlots;
    private int slotDuration;
    private boolean groupBookingEnabled;
    private int maxStudents;
    private String notes;
    private boolean applyMonthly;
    private int month;
    private int year;

    public static class DayTimeSlot implements Serializable {
        private int dayOfWeek; // 1=Monday, 2=Tuesday, ..., 7=Sunday
        private List<TimeSlot> timeSlots;

        public DayTimeSlot(int dayOfWeek, List<TimeSlot> timeSlots) {
            this.dayOfWeek = dayOfWeek;
            this.timeSlots = timeSlots;
        }

        public int getDayOfWeek() { return dayOfWeek; }
        public void setDayOfWeek(int dayOfWeek) { this.dayOfWeek = dayOfWeek; }
        public List<TimeSlot> getTimeSlots() { return timeSlots; }
        public void setTimeSlots(List<TimeSlot> timeSlots) { this.timeSlots = timeSlots; }
    }

    // Getters and setters
    public boolean isSpecificDateMode() { return isSpecificDateMode; }
    public void setSpecificDateMode(boolean specificDateMode) { isSpecificDateMode = specificDateMode; }
    public String getSelectedDate() { return selectedDate; }
    public void setSelectedDate(String selectedDate) { this.selectedDate = selectedDate; }
    public List<TimeSlot> getTimeSlots() { return timeSlots; }
    public void setTimeSlots(List<TimeSlot> timeSlots) { this.timeSlots = timeSlots; }
    public List<DayTimeSlot> getDayTimeSlots() { return dayTimeSlots; }
    public void setDayTimeSlots(List<DayTimeSlot> dayTimeSlots) { this.dayTimeSlots = dayTimeSlots; }
    public int getSlotDuration() { return slotDuration; }
    public void setSlotDuration(int slotDuration) { this.slotDuration = slotDuration; }
    public boolean isGroupBookingEnabled() { return groupBookingEnabled; }
    public void setGroupBookingEnabled(boolean groupBookingEnabled) { this.groupBookingEnabled = groupBookingEnabled; }
    public int getMaxStudents() { return maxStudents; }
    public void setMaxStudents(int maxStudents) { this.maxStudents = maxStudents; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public boolean isApplyMonthly() { return applyMonthly; }
    public void setApplyMonthly(boolean applyMonthly) { this.applyMonthly = applyMonthly; }
    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AddScheduleData{");
        sb.append("isSpecificDateMode=").append(isSpecificDateMode);
        sb.append(", selectedDate='").append(selectedDate).append('\'');
        
        if (isSpecificDateMode) {
            sb.append(", timeSlots=").append(timeSlots != null ? timeSlots.size() : 0).append(" slots");
        } else {
            sb.append(", dayTimeSlots=").append(dayTimeSlots != null ? dayTimeSlots.size() : 0).append(" days");
        }
        
        sb.append(", slotDuration=").append(slotDuration);
        sb.append(", groupBookingEnabled=").append(groupBookingEnabled);
        sb.append(", maxStudents=").append(maxStudents);
        sb.append(", notes='").append(notes).append('\'');
        sb.append(", applyMonthly=").append(applyMonthly);
        sb.append('}');
        
        return sb.toString();
    }
} 