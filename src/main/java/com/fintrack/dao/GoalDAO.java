package com.fintrack.dao;

import com.fintrack.model.Goal;
import com.fintrack.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GoalDAO {

    public boolean addGoal(Goal goal) {
        String sql = "INSERT INTO goals (user_id, name, target_amount, current_amount, deadline, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, goal.getUserId());
            stmt.setString(2, goal.getName());
            stmt.setBigDecimal(3, goal.getTargetAmount());
            stmt.setBigDecimal(4, goal.getCurrentAmount());
            stmt.setDate(5, goal.getDeadline());
            stmt.setString(6, goal.getStatus());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Goal> getGoalsByUser(int userId) {
        List<Goal> goals = new ArrayList<>();
        String sql = "SELECT * FROM goals WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Goal goal = new Goal();
                goal.setId(rs.getInt("id"));
                goal.setUserId(rs.getInt("user_id"));
                goal.setName(rs.getString("name"));
                goal.setTargetAmount(rs.getBigDecimal("target_amount"));
                goal.setCurrentAmount(rs.getBigDecimal("current_amount"));
                goal.setDeadline(rs.getDate("deadline"));
                goal.setStatus(rs.getString("status"));
                goal.setCreatedAt(rs.getTimestamp("created_at"));
                goals.add(goal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return goals;
    }
}
