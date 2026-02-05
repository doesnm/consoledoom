// src/main/java/com/consoledoom/db/GameSessionDAO.java
package com.consoledoom.db;

import com.consoledoom.models.LeaderboardEntry;
import com.consoledoom.security.Permission;
import com.consoledoom.security.Role;
import com.consoledoom.security.SecuredEndpoint;
import com.consoledoom.validation.GameDataValidator;
import com.consoledoom.validation.ValidationResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for game session operations with validation and security.
 */
public class GameSessionDAO {

    /**
     * Saves a game session with validation.
     */
    public void saveSession(int userId, int score, int kills, int deaths,
            int wave, int timeSurvivedSec) throws SQLException {
        // Validate data using lambda validators
        ValidationResult validation = GameDataValidator.validateGameSession(
                score, kills, wave, timeSurvivedSec);

        validation.ifInvalid(errors -> {
            throw new IllegalArgumentException("Invalid game data: " + errors.get(0));
        });

        String sql = """
                    INSERT INTO game_sessions(user_id, score, kills, deaths, wave, time_survived_sec, kd)
                    VALUES (?, ?, ?, ?, ?, ?, calc_kd(?, ?));
                """;

        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, score);
            ps.setInt(3, kills);
            ps.setInt(4, deaths);
            ps.setInt(5, wave);
            ps.setInt(6, timeSurvivedSec);
            ps.setInt(7, kills);
            ps.setInt(8, deaths);
            ps.executeUpdate();
        }
    }

    /**
     * Gets leaderboard - public endpoint.
     */
    public List<LeaderboardEntry> getLeaderboard(int limit) throws SQLException {
        String sql = """
                    SELECT u.nickname, s.score, s.kills, s.deaths, s.kd,
                           s.wave, s.time_survived_sec, s.played_at, s.session_id
                    FROM game_sessions s
                    JOIN users u ON u.user_id = s.user_id
                    ORDER BY s.score DESC
                    LIMIT ?;
                """;

        List<LeaderboardEntry> entries = new ArrayList<>();
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    entries.add(new LeaderboardEntry(
                            rs.getInt("session_id"),
                            rs.getString("nickname"),
                            rs.getInt("score"),
                            rs.getInt("kills"),
                            rs.getInt("deaths"),
                            rs.getBigDecimal("kd"),
                            rs.getInt("wave"),
                            rs.getInt("time_survived_sec"),
                            rs.getTimestamp("played_at")));
                }
            }
        }
        return entries;
    }

    /**
     * Deletes a game record - requires MANAGER permission.
     */
    @SecuredEndpoint(requiredPermissions = { Permission.DELETE_GAME_RECORDS }, minimumRole = Role.MANAGER)
    public boolean deleteGameRecord(int sessionId) throws SQLException {
        String sql = "DELETE FROM game_sessions WHERE session_id = ?";

        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sessionId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Gets all game records for a user.
     */
    public List<LeaderboardEntry> getUserRecords(int userId) throws SQLException {
        String sql = """
                    SELECT u.nickname, s.score, s.kills, s.deaths, s.kd,
                           s.wave, s.time_survived_sec, s.played_at, s.session_id
                    FROM game_sessions s
                    JOIN users u ON u.user_id = s.user_id
                    WHERE s.user_id = ?
                    ORDER BY s.played_at DESC;
                """;

        List<LeaderboardEntry> entries = new ArrayList<>();
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    entries.add(new LeaderboardEntry(
                            rs.getInt("session_id"),
                            rs.getString("nickname"),
                            rs.getInt("score"),
                            rs.getInt("kills"),
                            rs.getInt("deaths"),
                            rs.getBigDecimal("kd"),
                            rs.getInt("wave"),
                            rs.getInt("time_survived_sec"),
                            rs.getTimestamp("played_at")));
                }
            }
        }
        return entries;
    }
}
