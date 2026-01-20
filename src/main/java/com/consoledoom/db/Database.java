package com.consoledoom.db;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    // TODO: put your real password OR move to properties/env (recommended)
    private static final String URL  = DbConfig.url();
    private static final String USER = DbConfig.user();
    private static final String PASS = DbConfig.pass();


    // ---------- SAVE SESSION ----------
    public static boolean saveGameSession(String nickname,
                                          int score,
                                          int kills,
                                          int deaths,
                                          int wave,
                                          int timeSurvivedSec) {

        String sql = """
            WITH upsert_player AS (
                INSERT INTO players(nickname)
                VALUES (?)
                ON CONFLICT (nickname) DO UPDATE SET nickname = EXCLUDED.nickname
                RETURNING player_id
            )
            INSERT INTO game_sessions(player_id, score, kills, deaths, wave, time_survived_sec, kd)
            SELECT player_id, ?, ?, ?, ?, ?, calc_kd(?, ?)
            FROM upsert_player;
        """;

        try (Connection c = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = c.prepareStatement(sql)) {

            int i = 1;
            ps.setString(i++, nickname);

            ps.setInt(i++, score);
            ps.setInt(i++, kills);
            ps.setInt(i++, deaths);
            ps.setInt(i++, wave);
            ps.setInt(i++, timeSurvivedSec);

            ps.setInt(i++, kills);   // calc_kd(kills, deaths)
            ps.setInt(i++, deaths);

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            // better than hiding: you can also log stacktrace while debugging
            System.out.println("DB error (saveGameSession): " + e.getMessage());
            return false;
        }
    }

    // ---------- LEADERBOARD ----------
    public static class LeaderboardRow {
        public final String nickname;
        public final int score;
        public final int kills;
        public final int deaths;
        public final BigDecimal kd;
        public final int wave;
        public final int timeSurvivedSec;
        public final Timestamp playedAt;

        public LeaderboardRow(String nickname, int score, int kills, int deaths,
                              BigDecimal kd, int wave, int timeSurvivedSec, Timestamp playedAt) {
            this.nickname = nickname;
            this.score = score;
            this.kills = kills;
            this.deaths = deaths;
            this.kd = kd;
            this.wave = wave;
            this.timeSurvivedSec = timeSurvivedSec;
            this.playedAt = playedAt;
        }
    }

    public static List<LeaderboardRow> topSessions(int limit) {
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

        List<LeaderboardRow> rows = new ArrayList<>();

        try (Connection c = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new LeaderboardRow(
                            rs.getString("nickname"),
                            rs.getInt("score"),
                            rs.getInt("kills"),
                            rs.getInt("deaths"),
                            rs.getBigDecimal("kd"),
                            rs.getInt("wave"),
                            rs.getInt("time_survived_sec"),
                            rs.getTimestamp("played_at")
                    ));
                }
            }

        } catch (SQLException e) {
            System.out.println("DB error (topSessions): " + e.getMessage());
        }

        return rows;
    }
}
