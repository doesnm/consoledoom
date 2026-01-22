package com.consoledoom.render;

import com.consoledoom.core.Config;
import com.consoledoom.entities.Bullet;
import com.consoledoom.entities.Player;
import com.consoledoom.entities.Wall;
import com.consoledoom.entities.monsters.Monster;
import com.consoledoom.utils.Vec2;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.TextColor;

import java.io.IOException;
import java.util.List;

public class Renderer {

    private static final TextColor BG_COLOR = TextColor.ANSI.BLACK;

    public static void render(Screen screen, Player player, List<Wall> walls, List<Bullet> bullets,
            List<Monster> monsters, int wave, int timeSeconds) throws IOException {
        screen.doResizeIfNecessary();
        screen.clear();

        TextGraphics g = screen.newTextGraphics();
        g.setBackgroundColor(BG_COLOR);

        int offsetY = 2;

        // empty space
        g.setForegroundColor(TextColor.ANSI.DEFAULT);
        for (int y = 0; y < Config.ARENA_HEIGHT; y++) {
            for (int x = 0; x < Config.ARENA_WIDTH; x++) {
                g.setCharacter(x, offsetY + y, TextCharacter.fromCharacter(
                        Config.EMPTY_SYMBOL,
                        TextColor.ANSI.DEFAULT,
                        BG_COLOR)[0]);
            }
        }

        // map's borders
        for (Wall w : walls) {
            Vec2 p = w.getPosition();
            if (inBounds(p.x, p.y)) {
                putChar(g, p.x, offsetY + p.y, w.getSymbol(), w.getColor());
            }
        }

        // bullets
        for (Bullet b : bullets) {
            Vec2 p = b.getPosition();
            if (inBounds(p.x, p.y)) {
                putChar(g, p.x, offsetY + p.y, b.getSymbol(), b.getColor());
            }
        }

        // monsters
        for (Monster m : monsters) {
            Vec2 p = m.getPosition();
            if (inBounds(p.x, p.y)) {
                putChar(g, p.x, offsetY + p.y, m.getSymbol(), m.getColor());
            }
        }

        // player
        Vec2 pp = player.getPosition();
        if (inBounds(pp.x, pp.y)) {
            putChar(g, pp.x, offsetY + pp.y, player.getSymbol(), player.getColor());
        }

        // HUD
        String hearts = renderHealth(player.getHealth());
        String hud = String.format(
                "SCORE: %d   %s   WAVE: %d   KILLS: %d   MONSTERS: %d   TIME: %02d:%02d",
                player.getScore(), hearts, wave, player.getKills(), monsters.size(),
                timeSeconds / 60, timeSeconds % 60);

        g.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        g.setBackgroundColor(BG_COLOR);
        g.putString(0, 0, hud);

        g.setForegroundColor(TextColor.ANSI.CYAN);
        g.putString(0, offsetY + Config.ARENA_HEIGHT + 1,
                "Controls: WASD move | Arrow keys shoot | Q quit");

        screen.refresh();
    }

    // ← Вспомогательный метод для установки символа с цветом
    private static void putChar(TextGraphics g, int x, int y, char symbol, TextColor fg) {
        g.setForegroundColor(fg);
        g.setBackgroundColor(BG_COLOR);
        g.setCharacter(x, y, symbol);
    }

    private static boolean inBounds(int x, int y) {
        return x >= 0 && x < Config.ARENA_WIDTH && y >= 0 && y < Config.ARENA_HEIGHT;
    }

    private static String renderHealth(int health) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Config.MAX_HEALTH; i++)
            sb.append(i < health ? "♥" : "♡");
        return sb.toString();
    }
}
