package com.example.tluofficehours.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Slot implements Serializable {
    @SerializedName("SlotId")
    private int slotId;
    @SerializedName("StartTime")
    private String startTime;
    @SerializedName("EndTime")
    private String endTime;
    @SerializedName("Room")
    private String room;

    public int getSlotId() { return slotId; }
    public void setSlotId(int slotId) { this.slotId = slotId; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
}