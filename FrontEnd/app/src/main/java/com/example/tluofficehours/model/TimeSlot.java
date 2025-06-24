package com.example.tluofficehours.model;

import java.io.Serializable;

public class TimeSlot implements Serializable {
    private String startTime;
    private String endTime;
    private boolean isSelected;

    public TimeSlot() {
    }

    public TimeSlot(String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.isSelected = false;
    }

    public TimeSlot(String startTime, String endTime, boolean isSelected) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.isSelected = isSelected;
    }

    // Getters and Setters
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "TimeSlot{" +
                "startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }

    // Utility method to check if time slot is valid
    public boolean isValid() {
        if (startTime == null || endTime == null) {
            return false;
        }

        try {
            String[] startParts = startTime.split(":");
            String[] endParts = endTime.split(":");

            int startHour = Integer.parseInt(startParts[0]);
            int startMinute = Integer.parseInt(startParts[1]);
            int endHour = Integer.parseInt(endParts[0]);
            int endMinute = Integer.parseInt(endParts[1]);

            int startTotalMinutes = startHour * 60 + startMinute;
            int endTotalMinutes = endHour * 60 + endMinute;

            return endTotalMinutes > startTotalMinutes;
        } catch (Exception e) {
            return false;
        }
    }

    // Get duration in minutes
    public int getDurationInMinutes() {
        if (!isValid()) {
            return 0;
        }

        try {
            String[] startParts = startTime.split(":");
            String[] endParts = endTime.split(":");

            int startHour = Integer.parseInt(startParts[0]);
            int startMinute = Integer.parseInt(startParts[1]);
            int endHour = Integer.parseInt(endParts[0]);
            int endMinute = Integer.parseInt(endParts[1]);

            int startTotalMinutes = startHour * 60 + startMinute;
            int endTotalMinutes = endHour * 60 + endMinute;

            return endTotalMinutes - startTotalMinutes;
        } catch (Exception e) {
            return 0;
        }
    }
}
