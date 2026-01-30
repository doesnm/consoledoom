package com.consoledoom.core;

import com.consoledoom.ui.GameOverScreen;
import com.consoledoom.ui.GameScreen;
import com.consoledoom.ui.LeaderboardScreen;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

public class Game {
    private boolean running = true;
    private GameState state = GameState.PLAYING;
    private GameScreen gameScreen;
    private GameOverScreen gameOverScreen;
    private LeaderboardScreen leaderboardScreen;

    private static final long FRAME_MS = 1000L / Config.TARGET_FPS;

    public void start() throws Exception {
        DefaultTerminalFactory factory = new DefaultTerminalFactory()
                .setPreferTerminalEmulator(true)
                .setForceAWTOverSwing(true);

        // Bigger font
        java.awt.Font font = new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 26);
        factory.setTerminalEmulatorFontConfiguration(
                com.googlecode.lanterna.terminal.swing.AWTTerminalFontConfiguration.newInstance(font));

        com.googlecode.lanterna.terminal.Terminal terminal = factory.createTerminalEmulator();

        if (terminal instanceof com.googlecode.lanterna.terminal.swing.SwingTerminalFrame frame) {
            frame.setTitle("Console Doom");
            frame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
            frame.setVisible(true);
        }

        Screen screen = new com.googlecode.lanterna.screen.TerminalScreen(terminal);
        screen.startScreen();
        screen.setCursorPosition(null);
        screen.doResizeIfNecessary();

        gameScreen = new GameScreen();
        long lastSecondMark = System.currentTimeMillis();

        try {
            while (running) {
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

                    if (key.getKeyType() == com.googlecode.lanterna.input.KeyType.Escape) {
                        running = false;
                        break;
                    }

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
                            running = false;
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
