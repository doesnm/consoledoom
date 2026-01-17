package com.consoledoom.core;

import com.consoledoom.arena.Arena;
import com.consoledoom.entities.Bullet;
import com.consoledoom.entities.Player;
import com.consoledoom.render.Renderer;
import com.consoledoom.systems.*;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private GameState state = GameState.PLAYING;

    private final Player player;
    private final Arena arena;
    private final List<Bullet> bullets = new ArrayList<>();
    private final WeaponSystem weaponSystem = new WeaponSystem(2);

    private int wave = 1;
    private int timeSeconds = 0;

    private static final long FRAME_MS = 1000L / Config.TARGET_FPS;

    public Game(String playerName) {
        this.player = new Player(new com.consoledoom.utils.Vec2(5, 8));
        this.arena = new Arena();
    }

    public void start() throws Exception {
        Screen screen = new DefaultTerminalFactory().createScreen();
        screen.startScreen();
        screen.setCursorPosition(null);

        long lastSecondMark = System.currentTimeMillis();

        try {
            while (state == GameState.PLAYING) {
                long frameStart = System.currentTimeMillis();

                if (System.currentTimeMillis() - lastSecondMark >= 1000) {
                    timeSeconds++;
                    lastSecondMark += 1000;
                }

                KeyStroke key;
                while ((key = screen.pollInput()) != null) {
                    var input = InputHandler.handleKey(key, player);
                    switch (input.action) {
                        case MOVE -> MovementSystem.movePlayer(player,
                                new com.consoledoom.utils.Vec2(input.dx, input.dy), arena);
                        case SHOOT -> weaponSystem.shoot(player, arena, bullets);
                        case QUIT -> state = GameState.GAME_OVER;
                        case NONE -> {
                        }
                    }
                }

                tick();

                Renderer.render(screen, player, arena.getWalls(), bullets, wave, timeSeconds);

                long elapsed = System.currentTimeMillis() - frameStart;
                if (elapsed < FRAME_MS) {
                    Thread.sleep(FRAME_MS - elapsed);
                }
            }
        } finally {
            screen.stopScreen();
        }

        System.out.println("💀 Game over! Final score: " + player.getScore());
    }

    private void tick() {
        weaponSystem.tick();
        MovementSystem.updateBullets(bullets, arena);
        // TODO: monster AI, combat, wave logic
    }
}
