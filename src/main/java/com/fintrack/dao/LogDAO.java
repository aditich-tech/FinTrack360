package com.fintrack.dao;

import com.fintrack.model.SecurityLog;
import com.fintrack.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LogDAO {

    public void logEvent(SecurityLog log) {
        String sql = "INSERT INTO security_logs (user_id, event_type, description, ip_address) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, log.getUserId());
            stmt.setString(2, log.getEventType());
            stmt.setString(3, log.getDescription());
            stmt.setString(4, log.getIpAddress());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<SecurityLog> getAllLogs() {
        List<SecurityLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM security_logs ORDER BY timestamp DESC";
        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                SecurityLog log = new SecurityLog();
                log.setId(rs.getInt("id"));
                log.setUserId(rs.getInt("user_id"));
                log.setEventType(rs.getString("event_type"));
                log.setDescription(rs.getString("description"));
                log.setIpAddress(rs.getString("ip_address"));
                log.setTimestamp(rs.getTimestamp("timestamp"));
                logs.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }
}
