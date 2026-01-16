package com.consoledoom.render;

import com.consoledoom.core.Config;
import com.consoledoom.entities.*;
import com.consoledoom.utils.Vec2;

import java.util.List;

public class Renderer {
    public static void render(Player player, List<Wall> walls, int wave, int timeSeconds) {
        clearScreen();

        String hud = String.format(
                "╔══════════════════════════════════════════════════════════════╗\n" +
                        "║ SCORE: %-5d %s WAVE: %-2d KILLS: %-3d TIME: %02d:%02d           ║\n" +
                        "╠══════════════════════════════════════════════════════════════╣",
                player.getScore(),
                renderHealth(player.getHealth()),
                wave,
                player.getKills(),
                timeSeconds / 60,
                timeSeconds % 60);
        System.out.println(hud);

        char[][] screen = new char[Config.ARENA_HEIGHT][Config.ARENA_WIDTH];
        for (int y = 0; y < Config.ARENA_HEIGHT; y++) {
            for (int x = 0; x < Config.ARENA_WIDTH; x++) {
                screen[y][x] = Config.EMPTY_SYMBOL;
            }
        }

        for (int x = 0; x < Config.ARENA_WIDTH; x++) {
            screen[0][x] = Config.WALL_SYMBOL;
            screen[Config.ARENA_HEIGHT - 1][x] = Config.WALL_SYMBOL;
        }
        for (int y = 0; y < Config.ARENA_HEIGHT; y++) {
            screen[y][0] = Config.WALL_SYMBOL;
            screen[y][Config.ARENA_WIDTH - 1] = Config.WALL_SYMBOL;
        }

        for (Wall wall : walls) {
            int wx = wall.getPosition().x;
            int wy = wall.getPosition().y;
            if (wx >= 0 && wx < Config.ARENA_WIDTH && wy >= 0 && wy < Config.ARENA_HEIGHT) {
                screen[wy][wx] = wall.getSymbol();
            }
        }

        Vec2 pos = player.getPosition();
        if (pos.x >= 0 && pos.x < Config.ARENA_WIDTH && pos.y >= 0 && pos.y < Config.ARENA_HEIGHT) {
            screen[pos.y][pos.x] = player.getSymbol();
        }

        for (int y = 0; y < Config.ARENA_HEIGHT; y++) {
            System.out.print("║ ");
            for (int x = 0; x < Config.ARENA_WIDTH; x++) {
                System.out.print(screen[y][x]);
            }
            System.out.println(" ║");
        }

        System.out.println("╚══════════════════════════════════════════════════════════════╝");
    }

    private static String renderHealth(int health) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Config.MAX_HEALTH; i++) {
            if (i < health)
                sb.append("♥");
            else
                sb.append("♡");
        }
        return sb.toString();
    }

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
