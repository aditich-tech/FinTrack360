package com.fintrack.model;

import java.sql.Timestamp;

public class SecurityLog {
    private int id;
    private int userId;
    private String eventType;
    private String description;
    private String ipAddress;
    private Timestamp timestamp;

    public SecurityLog() {
    }

    public SecurityLog(int userId, String eventType, String description, String ipAddress) {
        this.userId = userId;
        this.eventType = eventType;
        this.description = description;
        this.ipAddress = ipAddress;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
