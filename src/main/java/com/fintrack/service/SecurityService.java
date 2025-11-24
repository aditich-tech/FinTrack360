package com.fintrack.service;

import com.fintrack.dao.LogDAO;
import com.fintrack.model.SecurityLog;

import java.util.List;

public class SecurityService {
    private LogDAO logDAO = new LogDAO();

    public void log(int userId, String eventType, String description, String ipAddress) {
        SecurityLog log = new SecurityLog(userId, eventType, description, ipAddress);
        logDAO.logEvent(log);
    }

    public List<SecurityLog> getLogs() {
        return logDAO.getAllLogs();
    }
}
