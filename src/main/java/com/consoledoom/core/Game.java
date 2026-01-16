package com.consoledoom.core;

import com.consoledoom.arena.Arena;
import com.consoledoom.entities.Player;
import com.consoledoom.render.Renderer;
import com.consoledoom.utils.Vec2;

import java.io.IOException;
import java.util.Scanner;

public class Game {
    private GameState state = GameState.PLAYING;
    private Player player;
    private Arena arena;
    private int wave = 1;
    private int timeSeconds = 0;
    private final Scanner scanner = new Scanner(System.in);

    public Game() {
        // Стартовая позиция игрока
        player = new Player(new Vec2(5, 8));
        arena = new Arena();
    }

    public void start() throws IOException {
        System.out.println("🎮 Game started! Press 'q' to quit.");
        long lastTime = System.currentTimeMillis();

        while (state == GameState.PLAYING) {
            long now = System.currentTimeMillis();
            if (now - lastTime >= 1000) { // каждую секунду
                timeSeconds++;
                lastTime = now;
            }

            // Пока без ввода — просто рендер
            Renderer.render(player, arena.getWalls(), wave, timeSeconds);

            // Имитация паузы между кадрами
            try {
                Thread.sleep(1000 / Config.TARGET_FPS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            // Простой выход
            if (System.in.available() > 0) {
                char c = (char) System.in.read();
                if (c == 'q' || c == 'Q') {
                    state = GameState.GAME_OVER;
                }
            }
        }

        System.out.println("👋 Game over. Final score: " + player.getScore());
    }
}
