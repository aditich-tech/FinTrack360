package com.fintrack.dao;

import com.fintrack.model.Budget;
import com.fintrack.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetDAO {

    public boolean addBudget(Budget budget) {
        String sql = "INSERT INTO budgets (user_id, category, amount, start_date, end_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, budget.getUserId());
            stmt.setString(2, budget.getCategory());
            stmt.setBigDecimal(3, budget.getAmount());
            stmt.setDate(4, budget.getStartDate());
            stmt.setDate(5, budget.getEndDate());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Budget> getBudgetsByUser(int userId) {
        List<Budget> budgets = new ArrayList<>();
        String sql = "SELECT * FROM budgets WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Budget budget = new Budget();
                budget.setId(rs.getInt("id"));
                budget.setUserId(rs.getInt("user_id"));
                budget.setCategory(rs.getString("category"));
                budget.setAmount(rs.getBigDecimal("amount"));
                budget.setStartDate(rs.getDate("start_date"));
                budget.setEndDate(rs.getDate("end_date"));
                budget.setCreatedAt(rs.getTimestamp("created_at"));
                budgets.add(budget);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return budgets;
    }

    public int getBudgetCount() {
        String sql = "SELECT COUNT(*) FROM budgets";
        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
