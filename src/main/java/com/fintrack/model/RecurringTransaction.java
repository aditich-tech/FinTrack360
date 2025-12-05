package com.fintrack.model;

import java.sql.Date;
import java.sql.Timestamp;

public class RecurringTransaction {
    private int id;
    private int userId;
    private double amount;
    private String category;
    private String description;
    private String frequency; // DAILY, WEEKLY, MONTHLY
    private Date nextRunDate;
    private String type; // INCOME, EXPENSE
    private Timestamp createdAt;

    public RecurringTransaction() {
    }

    public RecurringTransaction(int userId, double amount, String category, String description, String frequency,
            Date nextRunDate, String type) {
        this.userId = userId;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.frequency = frequency;
        this.nextRunDate = nextRunDate;
        this.type = type;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public Date getNextRunDate() {
        return nextRunDate;
    }

    public void setNextRunDate(Date nextRunDate) {
        this.nextRunDate = nextRunDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
