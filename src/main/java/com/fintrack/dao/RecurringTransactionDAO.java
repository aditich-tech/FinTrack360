package com.fintrack.dao;

import com.fintrack.model.RecurringTransaction;
import com.fintrack.util.DBConnection;
import com.fintrack.util.EncryptionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecurringTransactionDAO {

    public boolean create(RecurringTransaction rt) {
        String sql = "INSERT INTO recurring_transactions (user_id, amount, category, description, frequency, next_run_date, type) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rt.getUserId());
            stmt.setString(2, EncryptionUtil.encrypt(String.valueOf(rt.getAmount())));
            stmt.setString(3, rt.getCategory());
            stmt.setString(4, EncryptionUtil.encrypt(rt.getDescription()));
            stmt.setString(5, rt.getFrequency());
            stmt.setDate(6, rt.getNextRunDate());
            stmt.setString(7, rt.getType());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<RecurringTransaction> getByUserId(int userId) {
        List<RecurringTransaction> list = new ArrayList<>();
        String sql = "SELECT * FROM recurring_transactions WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                RecurringTransaction rt = new RecurringTransaction();
                rt.setId(rs.getInt("id"));
                rt.setUserId(rs.getInt("user_id"));
                rt.setAmount(Double.parseDouble(EncryptionUtil.decrypt(rs.getString("amount"))));
                rt.setCategory(rs.getString("category"));
                rt.setDescription(EncryptionUtil.decrypt(rs.getString("description")));
                rt.setFrequency(rs.getString("frequency"));
                rt.setNextRunDate(rs.getDate("next_run_date"));
                rt.setType(rs.getString("type"));
                rt.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(rt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<RecurringTransaction> getDueTransactions(Date date) {
        List<RecurringTransaction> list = new ArrayList<>();
        String sql = "SELECT * FROM recurring_transactions WHERE next_run_date <= ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, date);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                RecurringTransaction rt = new RecurringTransaction();
                rt.setId(rs.getInt("id"));
                rt.setUserId(rs.getInt("user_id"));
                rt.setAmount(Double.parseDouble(EncryptionUtil.decrypt(rs.getString("amount"))));
                rt.setCategory(rs.getString("category"));
                rt.setDescription(EncryptionUtil.decrypt(rs.getString("description")));
                rt.setFrequency(rs.getString("frequency"));
                rt.setNextRunDate(rs.getDate("next_run_date"));
                rt.setType(rs.getString("type"));
                list.add(rt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void updateNextRunDate(int id, Date nextDate) {
        String sql = "UPDATE recurring_transactions SET next_run_date = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, nextDate);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM recurring_transactions WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
