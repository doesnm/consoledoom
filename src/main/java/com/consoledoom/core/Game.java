package com.consoledoom.core;

import com.consoledoom.config.GameConfig;
import com.consoledoom.security.Permission;
import com.consoledoom.security.SecurityContext;
import com.consoledoom.service.AdminService;
import com.consoledoom.ui.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.AWTTerminalFontConfiguration;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;

import java.awt.Font;
import java.awt.Frame;

public class Game {
    private boolean running = true;
    private GameState state = GameState.AUTH;

    private AuthScreen authScreen;
    private MainMenuScreen mainMenuScreen;
    private GameScreen gameScreen;
    private GameOverScreen gameOverScreen;
    private LeaderboardScreen leaderboardScreen;
    private AdminScreen adminScreen;

    private final GameConfig config = GameConfig.INSTANCE;
    private final long frameMs;

    public Game() {
        this.frameMs = 1000L / config.getTargetFps();
    }

    public void start() throws Exception {
        DefaultTerminalFactory factory = new DefaultTerminalFactory()
                .setPreferTerminalEmulator(true)
                .setForceAWTOverSwing(true);

        Font font = new Font("Monospaced", Font.PLAIN, 26);
        factory.setTerminalEmulatorFontConfiguration(
                AWTTerminalFontConfiguration.newInstance(font));

        Terminal terminal = factory.createTerminalEmulator();
        if (terminal instanceof SwingTerminalFrame frame) {
            frame.setTitle("Console Doom");
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);
            frame.setVisible(true);
        }

        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();
        screen.setCursorPosition(null);

        authScreen = new AuthScreen();
        long lastSecondMark = System.currentTimeMillis();

        try {
            while (running) {
                long frameStart = System.currentTimeMillis();
                screen.doResizeIfNecessary();

                if (state == GameState.PLAYING) {
                    if (System.currentTimeMillis() - lastSecondMark >= 1000) {
                        gameScreen.incrementTime();
                        lastSecondMark += 1000;
                    }
                }

                KeyStroke key;
                while ((key = screen.pollInput()) != null) {
                    if (key.getKeyType() == KeyType.Escape && state == GameState.AUTH) {
                        running = false;
                        break;
                    }
                    handleInput(key);
                }

                update();

                render(screen);

                long elapsed = System.currentTimeMillis() - frameStart;
                if (elapsed < frameMs) {
                    Thread.sleep(frameMs - elapsed);
                }
            }
        } finally {
            screen.stopScreen();
        }
    }

    private void handleInput(KeyStroke key) {
        switch (state) {
            case AUTH -> {
                if (key.getKeyType() == KeyType.Escape) {
                    running = false;
                } else {
                    authScreen.handleInput(key);
                    if (authScreen.isComplete()) {
                        state = GameState.MENU;
                        mainMenuScreen = new MainMenuScreen();
                    }
                }
            }
            case MENU -> {
                mainMenuScreen.handleInput(key);
                MainMenuScreen.MenuAction action = mainMenuScreen.getSelectedAction();
                if (action != null) {
                    switch (action) {
                        case PLAY -> {
                            state = GameState.PLAYING;
                            gameScreen = new GameScreen();
                        }
                        case LEADERBOARD -> {
                            state = GameState.LEADERBOARD;
                            leaderboardScreen = new LeaderboardScreen();
                        }
                        case ADMIN -> {
                            if (SecurityContext.getInstance()
                                    .hasPermission(Permission.VIEW_ADMIN_PANEL)) {
                                state = GameState.ADMIN;
                                adminScreen = new AdminScreen();
                            }
                        }
                        case LOGOUT -> {
                            SecurityContext.getInstance().logout();
                            state = GameState.AUTH;
                            authScreen = new AuthScreen();
                        }
                        case QUIT -> running = false;
                    }
                    mainMenuScreen.clearAction();
                }
            }
            case PLAYING -> {
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
            }
            case GAME_OVER_NAME -> {
                gameOverScreen.handleInput(key);
                if (gameOverScreen.isDone()) {
                    state = GameState.LEADERBOARD;
                    leaderboardScreen = new LeaderboardScreen();
                }
            }
            case LEADERBOARD -> {
                if (leaderboardScreen.shouldExit(key)) {
                    state = GameState.MENU;
                    mainMenuScreen = new MainMenuScreen();
                }
            }
            case ADMIN -> {
                adminScreen.handleInput(key);
                if (adminScreen.shouldExit()) {
                    state = GameState.MENU;
                    mainMenuScreen = new MainMenuScreen();
                }
            }
        }
    }

    private void update() {
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
    }

    private void render(Screen screen) throws Exception {
        switch (state) {
            case AUTH -> authScreen.render(screen);
            case MENU -> mainMenuScreen.render(screen);
            case PLAYING -> gameScreen.render(screen);
            case GAME_OVER_NAME -> gameOverScreen.render(screen);
            case LEADERBOARD -> leaderboardScreen.render(screen);
            case ADMIN -> adminScreen.render(screen);
        }
    }

    private boolean isQuitKey(KeyStroke key) {
        return key.getKeyType() == KeyType.Character &&
                Character.toLowerCase(key.getCharacter()) == 'q';
    }
}
