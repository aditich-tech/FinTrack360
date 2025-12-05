package com.fintrack.dao;

import com.fintrack.model.Expense;
import com.fintrack.util.DBConnection;
import com.fintrack.util.EncryptionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDAO {

    public boolean addExpense(Expense expense) {
        String sql = "INSERT INTO expenses (user_id, amount, category, description, date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, expense.getUserId());
            stmt.setString(2, EncryptionUtil.encrypt(String.valueOf(expense.getAmount())));
            stmt.setString(3, expense.getCategory());
            stmt.setString(4, EncryptionUtil.encrypt(expense.getDescription()));
            stmt.setDate(5, expense.getDate());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Expense> getExpensesByUser(int userId) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses WHERE user_id = ? ORDER BY date DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Expense expense = new Expense();
                expense.setId(rs.getInt("id"));
                expense.setUserId(rs.getInt("user_id"));
                expense.setAmount(new java.math.BigDecimal(EncryptionUtil.decrypt(rs.getString("amount"))));
                expense.setCategory(rs.getString("category"));
                expense.setDescription(EncryptionUtil.decrypt(rs.getString("description")));
                expense.setDate(rs.getDate("date"));
                expense.setCreatedAt(rs.getTimestamp("created_at"));
                expenses.add(expense);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expenses;
    }

    public int getExpenseCount() {
        String sql = "SELECT COUNT(*) FROM expenses";
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
