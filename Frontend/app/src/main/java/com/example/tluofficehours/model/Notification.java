package com.example.tluofficehours.model;

import com.google.gson.annotations.SerializedName;

public class Notification {
    @SerializedName("id")
    private long id;

    @SerializedName("title")
    private String title;

    @SerializedName("message")
    private String message;

    @SerializedName("type")
    private String type;

    @SerializedName("is_read")
    private boolean isRead;

    @SerializedName("created_at")
    private String createdAt;

    public Notification(long id, String title, String message, String type, boolean isRead, String createdAt) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getType() { return type; }
    public boolean isRead() { return isRead; }
    public String getCreatedAt() { return createdAt; }

    public String getDate() {
        if (createdAt != null && createdAt.length() >= 10) {
            return createdAt.substring(0, 10);
        }
        return createdAt;
    }
    public String getTime() {
        if (createdAt != null && createdAt.length() >= 16) {
            return createdAt.substring(11, 16);
        }
        return "";
    }
} 