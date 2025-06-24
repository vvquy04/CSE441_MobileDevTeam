package com.example.tluofficehours.model;

import java.io.Serializable;

public class Notification implements Serializable {
    private String id;
    private String title;
    private String message;
    private String date;
    private String time;
    private String type; // "booking", "reminder", "system", etc.
    private boolean isRead;

    public Notification() {
    }

    public Notification(String id, String title, String message, String date, String time, String type) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.date = date;
        this.time = time;
        this.type = type;
        this.isRead = false;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
} 