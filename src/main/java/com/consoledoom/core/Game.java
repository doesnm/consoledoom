package com.consoledoom.core;

import com.consoledoom.arena.Arena;
import com.consoledoom.entities.Bullet;
import com.consoledoom.entities.Player;
import com.consoledoom.render.Renderer;
import com.consoledoom.utils.Vec2;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Game {

    private GameState state = GameState.PLAYING;

    private final Player player = new Player(new Vec2(5, 8));
    private final Arena arena = new Arena();
    private final List<Bullet> bullets = new ArrayList<>();

    private int wave = 1;
    private int timeSeconds = 0;

    private int shootCooldownTicks = 0;
    private static final long FRAME_MS = 1000L / Config.TARGET_FPS;

    public void start() throws Exception {
        Screen screen = new DefaultTerminalFactory().createScreen();
        screen.startScreen();
        screen.setCursorPosition(null);

        long lastSecondMark = System.currentTimeMillis();

        try {
            while (state == GameState.PLAYING) {
                long frameStart = System.currentTimeMillis();

                // seconds timer
                while (frameStart - lastSecondMark >= 1000) {
                    timeSeconds++;
                    lastSecondMark += 1000;
                }

                // handle all queued input
                KeyStroke key;
                while ((key = screen.pollInput()) != null) {
                    handleInput(key);
                }

                // update
                tick();

                // render TO LANTERNA WINDOW (no System.out spam)
                Renderer.render(screen, player, arena.getWalls(), bullets, wave, timeSeconds);

                // fps cap
                long frameTime = System.currentTimeMillis() - frameStart;
                if (frameTime < FRAME_MS) Thread.sleep(FRAME_MS - frameTime);
            }
        } finally {
            screen.stopScreen();
        }

        // after screen closes, you can print summary normally
        System.out.println("Game over. Final score: " + player.getScore());
    }

    private void handleInput(KeyStroke key) {
        if (key.getKeyType() == KeyType.Escape) {
            state = GameState.GAME_OVER;
            return;
        }

        if (key.getKeyType() != KeyType.Character) return;

        char c = key.getCharacter();
        char lower = Character.toLowerCase(c);

        switch (lower) {
            case 'q' -> state = GameState.GAME_OVER;

            case 'w' -> tryMove(new Vec2(0, -1));
            case 's' -> tryMove(new Vec2(0, 1));
            case 'a' -> tryMove(new Vec2(-1, 0));
            case 'd' -> tryMove(new Vec2(1, 0));

            case ' ' -> tryShoot(); // SPACE
        }
    }

    private void tryMove(Vec2 delta) {
        Vec2 newPos = new Vec2(
                player.getPosition().x + delta.x,
                player.getPosition().y + delta.y
        );

        player.setFacing(delta);

        if (arena.isWalkable(newPos)) {
            player.setPosition(newPos);
        }
    }

    private void tryShoot() {
        if (shootCooldownTicks > 0) return;

        Vec2 dir = player.getFacing();
        if (dir == null || (dir.x == 0 && dir.y == 0)) dir = new Vec2(1, 0);

        Vec2 spawn = new Vec2(
                player.getPosition().x + dir.x,
                player.getPosition().y + dir.y
        );

        if (arena.isInside(spawn) && !arena.isWall(spawn)) {
            bullets.add(new Bullet(spawn, dir));
            shootCooldownTicks = 2;
        }
    }

    private void tick() {
        if (shootCooldownTicks > 0) shootCooldownTicks--;

        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet b = it.next();
            Vec2 p = b.getPosition();
            Vec2 d = b.getDir();

            Vec2 next = new Vec2(p.x + d.x, p.y + d.y);

            if (!arena.isInside(next) || arena.isWall(next)) {
                it.remove();
                continue;
            }

            b.setPosition(next);
        }
    }
}
