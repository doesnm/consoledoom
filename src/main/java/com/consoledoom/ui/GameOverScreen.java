// src/main/java/com/consoledoom/ui/GameOverScreen.java
package com.consoledoom.ui;

import com.consoledoom.db.GameSessionDAO;
import com.consoledoom.security.SecurityContext;
import com.consoledoom.validation.GameDataValidator;
import com.consoledoom.validation.ValidationResult;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.sql.SQLException;

/**
 * Game over screen - saves to currently authenticated user.
 */
public class GameOverScreen {
    private final int score, kills, wave, timeSeconds;
    private boolean saved = false;
    private String statusMessage = "";

    public GameOverScreen(int score, int kills, int wave, int timeSeconds) {
        this.score = score;
        this.kills = kills;
        this.wave = wave;
        this.timeSeconds = timeSeconds;
    }

    public void handleInput(KeyStroke key) {
        if (key.getKeyType() == KeyType.Enter || key.getKeyType() == KeyType.Escape) {
            saveIfNeeded();
        }
    }

    private void saveIfNeeded() {
        if (saved)
            return;

        // Validate game data using lambda validators
        ValidationResult validation = GameDataValidator.validateGameSession(
                score, kills, wave, timeSeconds);

        if (!validation.isValid()) {
            statusMessage = "Invalid game data: " + validation.getFirstError();
            saved = true;
            return;
        }

        SecurityContext.getInstance().getCurrentUser().ifPresentOrElse(
                user -> {
                    try {
                        GameSessionDAO dao = new GameSessionDAO();
                        dao.saveSession(user.getId(), score, kills, 1, wave, timeSeconds);
                        statusMessage = "Score saved!";
                    } catch (SQLException e) {
                        statusMessage = "Failed to save: " + e.getMessage();
                    }
                },
                () -> statusMessage = "Not logged in - score not saved");

        saved = true;
    }

    public void render(Screen screen) throws Exception {
        screen.clear();
        TextGraphics g = screen.newTextGraphics();

        g.setForegroundColor(TextColor.ANSI.RED);
        g.putString(25, 3, "=== GAME OVER ===");

        g.setForegroundColor(TextColor.ANSI.WHITE);

        SecurityContext.getInstance().getCurrentUser()
                .ifPresent(user -> g.putString(2, 5, "Player: " + user.getNickname()));

        g.putString(2, 7, "Final Score: " + score);
        g.putString(2, 8, "Kills: " + kills);
        g.putString(2, 9, "Wave: " + wave);
        g.putString(2, 10, String.format("Time: %02d:%02d", timeSeconds / 60, timeSeconds % 60));

        if (!statusMessage.isEmpty()) {
            g.setForegroundColor(TextColor.ANSI.GREEN);
            g.putString(2, 13, statusMessage);
        }

        g.setForegroundColor(TextColor.ANSI.CYAN);
        g.putString(2, 16, "Press ENTER to continue to leaderboard...");

        screen.refresh();
    }

    public boolean isDone() {
        return saved;
    }
}
