package com.consoledoom.ui;

import com.consoledoom.db.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import java.sql.SQLException;

public class GameOverScreen {
    private final StringBuilder nameBuffer = new StringBuilder();
    private final int score, kills, wave, timeSeconds;
    private boolean saved = false;
    private String finalNickname = null;

    public GameOverScreen(int score, int kills, int wave, int timeSeconds) {
        this.score = score;
        this.kills = kills;
        this.wave = wave;
        this.timeSeconds = timeSeconds;
    }

    public void handleInput(KeyStroke key) {
        if (key.getKeyType() == KeyType.Character) {
            char ch = key.getCharacter();
            if ((Character.isLetterOrDigit(ch) || ch == '_' || ch == '-') && nameBuffer.length() < 16) {
                nameBuffer.append(ch);
            }
        } else if (key.getKeyType() == KeyType.Backspace && nameBuffer.length() > 0) {
            nameBuffer.deleteCharAt(nameBuffer.length() - 1);
        } else if (key.getKeyType() == KeyType.Enter) {
            saveIfNeeded();
        } else if (key.getKeyType() == KeyType.Escape) {
            saveIfNeeded();
        }
    }

    private void saveIfNeeded() {
        if (!saved) {
            String nick = nameBuffer.toString().trim();
            if (nick.isEmpty())
                nick = "Player";
            try {
                PlayerDAO playerDao = new PlayerDAO();
                GameSessionDAO sessionDao = new GameSessionDAO();

                int playerId = playerDao.upsertPlayer(nick);
                sessionDao.saveSession(playerId, score, kills, 1, wave, timeSeconds);

                saved = true;
                finalNickname = nick;
            } catch (SQLException e) {
                e.printStackTrace();
                // Можно показать ошибку в UI
            }
            finalNickname = nick;
            saved = true;
        }
    }

    public void render(Screen screen) throws Exception {
        screen.clear();
        var g = screen.newTextGraphics();
        g.putString(2, 2, "=== GAME OVER ===");
        g.putString(2, 4, "Final score: " + score);
        g.putString(2, 5, "Kills: " + kills);
        g.putString(2, 6, "Wave: " + wave);
        g.putString(2, 7, String.format("Time: %02d:%02d", timeSeconds / 60, timeSeconds % 60));
        g.putString(2, 9, "Enter nickname (ENTER to save):");
        g.putString(2, 10, "> " + nameBuffer);
        g.putString(2, 12, "Backspace=delete | ESC=skip save");
        screen.refresh();
    }

    public boolean isDone() {
        return saved;
    }

    public String getNickname() {
        return finalNickname;
    }
}
