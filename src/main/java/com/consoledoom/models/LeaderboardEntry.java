package com.consoledoom.models;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class LeaderboardEntry {
    private final int sessionId;
    private final String nickname;
    private final int score;
    private final int kills;
    private final int deaths;
    private final BigDecimal kd;
    private final int wave;
    private final int timeSurvivedSec;
    private final Timestamp playedAt;

    public LeaderboardEntry(int sessionId, String nickname, int score, int kills,
            int deaths, BigDecimal kd, int wave,
            int timeSurvivedSec, Timestamp playedAt) {
        this.sessionId = sessionId;
        this.nickname = nickname;
        this.score = score;
        this.kills = kills;
        this.deaths = deaths;
        this.kd = kd;
        this.wave = wave;
        this.timeSurvivedSec = timeSurvivedSec;
        this.playedAt = playedAt;
    }

    public int getSessionId() {
        return sessionId;
    }

    public String getNickname() {
        return nickname;
    }

    public int getScore() {
        return score;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public BigDecimal getKd() {
        return kd;
    }

    public int getWave() {
        return wave;
    }

    public int getTimeSurvivedSec() {
        return timeSurvivedSec;
    }

    public Timestamp getPlayedAt() {
        return playedAt;
    }
}
