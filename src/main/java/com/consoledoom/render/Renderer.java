package com.consoledoom.render;

import com.consoledoom.core.Config;
import com.consoledoom.entities.Bullet;
import com.consoledoom.entities.Player;
import com.consoledoom.entities.Wall;
import com.consoledoom.entities.monsters.Monster;
import com.consoledoom.utils.Vec2;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;
import java.util.List;

public class Renderer {

    public static void render(Screen screen,
                              Player player,
                              List<Wall> walls,
                              List<Bullet> bullets,
                              List<Monster> monsters,
                              int wave,
                              int timeSeconds) throws IOException {

        screen.doResizeIfNecessary();
        screen.clear();

        TextGraphics g = screen.newTextGraphics();

        // Build buffer
        char[][] buf = new char[Config.ARENA_HEIGHT][Config.ARENA_WIDTH];
        for (int y = 0; y < Config.ARENA_HEIGHT; y++) {
            for (int x = 0; x < Config.ARENA_WIDTH; x++) {
                buf[y][x] = Config.EMPTY_SYMBOL;
            }
        }

        // Border
        for (int x = 0; x < Config.ARENA_WIDTH; x++) {
            buf[0][x] = Config.WALL_SYMBOL;
            buf[Config.ARENA_HEIGHT - 1][x] = Config.WALL_SYMBOL;
        }
        for (int y = 0; y < Config.ARENA_HEIGHT; y++) {
            buf[y][0] = Config.WALL_SYMBOL;
            buf[y][Config.ARENA_WIDTH - 1] = Config.WALL_SYMBOL;
        }

        // Walls
        for (Wall w : walls) {
            Vec2 p = w.getPosition();
            if (inBounds(p.x, p.y)) buf[p.y][p.x] = w.getSymbol();
        }

        // Bullets
        for (Bullet b : bullets) {
            Vec2 p = b.getPosition();
            if (inBounds(p.x, p.y)) buf[p.y][p.x] = b.getSymbol();
        }

        // Monsters
        for (Monster m : monsters) {
            Vec2 p = m.getPosition();
            if (inBounds(p.x, p.y)) buf[p.y][p.x] = m.getSymbol();
        }

        // Player (draw last)
        Vec2 pp = player.getPosition();
        if (inBounds(pp.x, pp.y)) buf[pp.y][pp.x] = player.getSymbol();

        // HUD
        String hearts = renderHealth(player.getHealth());
        String hud = String.format(
                "SCORE: %d   %s   WAVE: %d   KILLS: %d   MONSTERS: %d   TIME: %02d:%02d",
                player.getScore(),
                hearts,
                wave,
                player.getKills(),
                monsters.size(),
                timeSeconds / 60,
                timeSeconds % 60
        );
        g.putString(0, 0, hud);

        // Draw arena under HUD
        int offsetY = 2;
        for (int y = 0; y < Config.ARENA_HEIGHT; y++) {
            g.putString(0, offsetY + y, new String(buf[y]));
        }

        // Controls
        g.putString(0, offsetY + Config.ARENA_HEIGHT + 1, "Controls: WASD move | SPACE shoot | Q quit");

        screen.refresh();
    }

    private static boolean inBounds(int x, int y) {
        return x >= 0 && x < Config.ARENA_WIDTH && y >= 0 && y < Config.ARENA_HEIGHT;
    }

    private static String renderHealth(int health) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Config.MAX_HEALTH; i++) sb.append(i < health ? "♥" : "♡");
        return sb.toString();
    }
}
