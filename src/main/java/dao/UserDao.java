package com.scc.dao;

import com.scc.model.User;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

public class UserDao {

    public static String hash(String plain) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(plain.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean register(String username, String passwordHash, String fullName, String role) {
        String sql = "INSERT INTO users (username, password_hash, full_name, role, status) "
                + "VALUES (?, ?, ?, ?, 'PENDING')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, fullName);
            ps.setString(4, role);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("UNIQUE")) return false;
            throw new RuntimeException("Registration failed: " + e.getMessage(), e);
        }
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT id, username, password_hash, full_name, role, status "
                + "FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapUser(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Lookup failed: " + e.getMessage(), e);
        }
    }

    public Optional<User> authenticate(String username, String passwordHash) {
        return findByUsername(username).filter(u -> u.passwordHash().equals(passwordHash));
    }

    public List<User> getAllUsers() {
        return queryUsers("SELECT id, username, password_hash, full_name, role, status "
                + "FROM users ORDER BY id", null);
    }

    public List<User> getPendingUsers() {
        return queryUsers("SELECT id, username, password_hash, full_name, role, status "
                + "FROM users WHERE status = ? ORDER BY id", "PENDING");
    }

    public boolean approveUser(int userId) {
        String sql = "UPDATE users SET status = 'APPROVED' WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Approval failed: " + e.getMessage(), e);
        }
    }

    public void seedAdmin(String username, String passwordHash, String fullName) {
        String sql = "INSERT OR IGNORE INTO users (username, password_hash, full_name, role, status) "
                + "VALUES (?, ?, ?, 'ADMIN', 'APPROVED')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, fullName);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Admin seed failed: " + e.getMessage(), e);
        }
    }

    private List<User> queryUsers(String sql, String param) {
        List<User> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (param != null) ps.setString(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapUser(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Query failed: " + e.getMessage(), e);
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        return new User(rs.getInt("id"), rs.getString("username"),
                rs.getString("password_hash"), rs.getString("full_name"),
                rs.getString("role"), rs.getString("status"));
    }
}