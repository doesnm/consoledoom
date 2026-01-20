package com.consoledoom.db;

import com.consoledoom.models.GameSession;
import com.consoledoom.models.LeaderboardEntry;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameSessionDAO {

    public void saveSession(int playerId, int score, int kills, int deaths, int wave, int timeSurvivedSec)
            throws SQLException {
        String sql = """
                INSERT INTO game_sessions(player_id, score, kills, deaths, wave, time_survived_sec, kd)
                VALUES (?, ?, ?, ?, ?, ?, calc_kd(?, ?));
                """;

        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
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

    public List<LeaderboardEntry> getLeaderboard(int limit) throws SQLException {
        String sql = """
                SELECT
                    p.nickname,
                    s.score,
                    s.kills,
                    s.deaths,
                    s.kd,
                    s.wave,
                    s.time_survived_sec,
                    s.played_at
                FROM game_sessions s
                JOIN players p ON p.player_id = s.player_id
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
        return entries; // ← Возвращает List<LeaderboardEntry>
    }
}
