// src/main/java/com/consoledoom/db/UserDAO.java
package com.consoledoom.db;

import com.consoledoom.models.User;
import com.consoledoom.security.Role;
import com.consoledoom.security.SecuredEndpoint;
import com.consoledoom.security.Permission;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for User operations with secured endpoints.
 */
public class UserDAO {

    /**
     * Registers a new user with validation.
     */
    public int registerUser(String nickname, String passwordHash) throws SQLException {
        String sql = """
                    INSERT INTO users(nickname, password_hash, role)
                    VALUES (?, ?, 'PLAYER')
                    RETURNING user_id;
                """;

        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nickname);
            ps.setString(2, passwordHash);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
                throw new SQLException("Failed to register user");
            }
        }
    }

    /**
     * Authenticates user and returns User object if successful.
     */
    public Optional<User> authenticate(String nickname, String passwordHash) throws SQLException {
        String sql = """
                    UPDATE users SET last_login = NOW()
                    WHERE nickname = ? AND password_hash = ? AND is_active = TRUE
                    RETURNING user_id, nickname, password_hash, role, created_at, last_login, is_active;
                """;

        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nickname);
            ps.setString(2, passwordHash);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUser(rs));
                }
                return Optional.empty();
            }
        }
    }

    /**
     * Finds user by nickname.
     */
    public Optional<User> findByNickname(String nickname) throws SQLException {
        String sql = "SELECT * FROM users WHERE nickname = ?";

        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nickname);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUser(rs));
                }
                return Optional.empty();
            }
        }
    }

    /**
     * Gets all users - requires ADMIN permission.
     */
    @SecuredEndpoint(requiredPermissions = { Permission.VIEW_ALL_USERS }, minimumRole = Role.MANAGER)
    public List<User> getAllUsers() throws SQLException {
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        List<User> users = new ArrayList<>();

        try (Connection conn = Database.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        }
        return users;
    }

    /**
     * Deletes a user - requires ADMIN permission.
     */
    @SecuredEndpoint(requiredPermissions = { Permission.DELETE_USERS }, minimumRole = Role.ADMIN)
    public boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ? AND role != 'ADMIN'";

        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Updates user role - requires ADMIN permission.
     */
    @SecuredEndpoint(requiredPermissions = { Permission.MANAGE_ROLES }, minimumRole = Role.ADMIN)
    public boolean updateUserRole(int userId, Role newRole) throws SQLException {
        String sql = "UPDATE users SET role = ?::user_role WHERE user_id = ?";

        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newRole.name());
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Checks if nickname exists.
     */
    public boolean nicknameExists(String nickname) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE nickname = ?";

        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nickname);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getInt("user_id"))
                .nickname(rs.getString("nickname"))
                .passwordHash(rs.getString("password_hash"))
                .role(Role.valueOf(rs.getString("role")))
                .createdAt(rs.getTimestamp("created_at"))
                .lastLogin(rs.getTimestamp("last_login"))
                .active(rs.getBoolean("is_active"))
                .build();
    }
}
