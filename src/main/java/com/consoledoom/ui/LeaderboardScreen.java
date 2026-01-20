package com.consoledoom.ui;

import com.consoledoom.db.GameSessionDAO;
import com.consoledoom.models.LeaderboardEntry;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.sql.SQLException;
import java.util.List;

public class LeaderboardScreen {
    private final List<LeaderboardEntry> top;

    public LeaderboardScreen() {
        List<LeaderboardEntry> loaded;
        try {
            GameSessionDAO dao = new GameSessionDAO();
            loaded = dao.getLeaderboard(10);
        } catch (SQLException e) {
            e.printStackTrace();
            loaded = java.util.Collections.emptyList();
        }
        this.top = loaded;
    }

    public void render(Screen screen) throws Exception {
        screen.clear();
        var g = screen.newTextGraphics();

        g.putString(2, 2, "=== LEADERBOARD TOP 10 ===");
        g.putString(2, 4, String.format("%-16s %6s %5s %5s %6s %5s %6s",
                "NICK", "SCORE", "K", "D", "KD", "W", "TIME"));

        int y = 6;
        for (int i = 0; i < top.size() && y <= 20; i++) {
            LeaderboardEntry entry = top.get(i);
            String time = String.format("%02d:%02d",
                    entry.getTimeSurvivedSec() / 60,
                    entry.getTimeSurvivedSec() % 60);

            String kdStr = (entry.getKd() != null) ? entry.getKd().toPlainString() : "0";

            g.putString(2, y++, String.format(
                    "%2d) %-16s %6d %5d %5d %6s %5d %6s",
                    i + 1,
                    entry.getNickname(),
                    entry.getScore(),
                    entry.getKills(),
                    entry.getDeaths(),
                    kdStr,
                    entry.getWave(),
                    time));
        }

        g.putString(2, y + 2, "Press Q or ESC to quit");
        screen.refresh();
    }

    public boolean shouldExit(KeyStroke key) {
        return key.getKeyType() == KeyType.Escape ||
                (key.getKeyType() == KeyType.Character &&
                        (key.getCharacter() == 'q' || key.getCharacter() == 'Q'));
    }
}
