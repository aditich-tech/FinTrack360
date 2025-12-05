package com.fintrack.dao;

import com.fintrack.model.Income;
import com.fintrack.util.DBConnection;
import com.fintrack.util.EncryptionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IncomeDAO {

    public boolean addIncome(Income income) {
        String sql = "INSERT INTO incomes (user_id, amount, source, description, date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, income.getUserId());
            stmt.setString(2, EncryptionUtil.encrypt(String.valueOf(income.getAmount())));
            stmt.setString(3, income.getSource());
            stmt.setString(4, EncryptionUtil.encrypt(income.getDescription()));
            stmt.setDate(5, income.getDate());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Income> getIncomesByUser(int userId) {
        List<Income> incomes = new ArrayList<>();
        String sql = "SELECT * FROM incomes WHERE user_id = ? ORDER BY date DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Income income = new Income();
                income.setId(rs.getInt("id"));
                income.setUserId(rs.getInt("user_id"));
                income.setAmount(new java.math.BigDecimal(EncryptionUtil.decrypt(rs.getString("amount"))));
                income.setSource(rs.getString("source"));
                income.setDescription(EncryptionUtil.decrypt(rs.getString("description")));
                income.setDate(rs.getDate("date"));
                income.setCreatedAt(rs.getTimestamp("created_at"));
                incomes.add(income);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return incomes;
    }
}
