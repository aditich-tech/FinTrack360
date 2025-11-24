package com.fintrack.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class Income {
    private int id;
    private int userId;
    private BigDecimal amount;
    private String source;
    private String description;
    private Date date;
    private Timestamp createdAt;

    public Income() {
    }

    public Income(int userId, BigDecimal amount, String source, String description, Date date) {
        this.userId = userId;
        this.amount = amount;
        this.source = source;
        this.description = description;
        this.date = date;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
