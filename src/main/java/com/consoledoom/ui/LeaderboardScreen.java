package com.consoledoom.ui;

import com.consoledoom.db.Database;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

public class LeaderboardScreen {
    private final java.util.List<Database.LeaderboardRow> top = Database.topSessions(10);

    public void render(Screen screen) throws Exception {
        screen.clear();
        var g = screen.newTextGraphics();
        g.putString(2, 2, "=== LEADERBOARD TOP 10 ===");
        g.putString(2, 4, String.format("%-16s %6s %5s %5s %6s %5s %6s", "NICK", "SCORE", "K", "D", "KD", "W", "TIME"));
        int y = 6;
        for (int i = 0; i < top.size() && y <= 20; i++) {
            var row = top.get(i);
            String time = String.format("%02d:%02d", row.timeSurvivedSec / 60, row.timeSurvivedSec % 60);
            String kdStr = row.kd != null ? row.kd.toPlainString() : "0";
            g.putString(2, y++, String.format(
                    "%2d) %-16s %6d %5d %5d %6s %5d %6s",
                    i + 1, row.nickname, row.score, row.kills, row.deaths, kdStr, row.wave, time));
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
