package com.consoledoom.core;

import com.consoledoom.ui.GameOverScreen;
import com.consoledoom.ui.GameScreen;
import com.consoledoom.ui.LeaderboardScreen;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

public class Game {
    private GameState state = GameState.PLAYING;
    private GameScreen gameScreen;
    private GameOverScreen gameOverScreen;
    private LeaderboardScreen leaderboardScreen;

    private static final long FRAME_MS = 1000L / Config.TARGET_FPS;

    public void start() throws Exception {
        Screen screen = new DefaultTerminalFactory().createScreen();
        screen.startScreen();
        screen.setCursorPosition(null);

        gameScreen = new GameScreen();
        long lastSecondMark = System.currentTimeMillis();

        try {
            while (true) {
                long frameStart = System.currentTimeMillis();

                // Time update
                if (state == GameState.PLAYING) {
                    if (System.currentTimeMillis() - lastSecondMark >= 1000) {
                        gameScreen.incrementTime();
                        lastSecondMark += 1000;
                    }
                }

                // Input
                KeyStroke key;
                while ((key = screen.pollInput()) != null) {
                    if (state == GameState.PLAYING) {
                        if (isQuitKey(key)) {
                            state = GameState.GAME_OVER_NAME;
                            gameOverScreen = new GameOverScreen(
                                    gameScreen.getScore(),
                                    gameScreen.getKills(),
                                    gameScreen.getWave(),
                                    gameScreen.getTimeSeconds());
                        } else {
                            gameScreen.handleInput(key);
                        }
                    } else if (state == GameState.GAME_OVER_NAME) {
                        gameOverScreen.handleInput(key);
                        if (gameOverScreen.isDone()) {
                            state = GameState.LEADERBOARD;
                            leaderboardScreen = new LeaderboardScreen();
                        }
                    } else if (state == GameState.LEADERBOARD) {
                        if (leaderboardScreen.shouldExit(key)) {
                            return;
                        }
                    }
                }

                // Update logic
                if (state == GameState.PLAYING) {
                    gameScreen.update();
                    if (gameScreen.isGameOver()) {
                        state = GameState.GAME_OVER_NAME;
                        gameOverScreen = new GameOverScreen(
                                gameScreen.getScore(),
                                gameScreen.getKills(),
                                gameScreen.getWave(),
                                gameScreen.getTimeSeconds());
                    }
                }

                // Render
                if (state == GameState.PLAYING) {
                    gameScreen.render(screen);
                } else if (state == GameState.GAME_OVER_NAME) {
                    gameOverScreen.render(screen);
                } else if (state == GameState.LEADERBOARD) {
                    leaderboardScreen.render(screen);
                }

                // FPS cap
                long elapsed = System.currentTimeMillis() - frameStart;
                if (elapsed < FRAME_MS)
                    Thread.sleep(FRAME_MS - elapsed);
            }
        } finally {
            screen.stopScreen();
        }
    }

    private boolean isQuitKey(KeyStroke key) {
        return key.getKeyType() == com.googlecode.lanterna.input.KeyType.Character &&
                Character.toLowerCase(key.getCharacter()) == 'q';
    }
}
