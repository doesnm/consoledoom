package com.consoledoom.db;

import com.consoledoom.models.Player;

import java.sql.*;

public class PlayerDAO {

    public int upsertPlayer(String nickname) throws SQLException {
        String sql = """
                INSERT INTO players(nickname)
                VALUES (?)
                ON CONFLICT (nickname) DO UPDATE SET nickname = EXCLUDED.nickname
                RETURNING player_id;
                """;

        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nickname);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("player_id");
                }
                throw new SQLException("Failed to upsert player");
            }
        }
    }

    public Player findByNickname(String nickname) throws SQLException {
        String sql = "SELECT player_id, nickname FROM players WHERE nickname = ?";
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nickname);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Player(rs.getInt("player_id"), rs.getString("nickname"));
                }
                return null;
            }
        }
    }
}
