package com.consoledoom.core;

import com.consoledoom.arena.Arena;
import com.consoledoom.entities.Bullet;
import com.consoledoom.entities.Player;
import com.consoledoom.entities.monsters.*;
import com.consoledoom.render.Renderer;
import com.consoledoom.systems.*;
import com.consoledoom.utils.Vec2;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {

    private GameState state = GameState.PLAYING;

    private final Arena arena;
    private final Player player;

    private final List<Bullet> bullets = new ArrayList<>();
    private final List<Monster> monsters = new ArrayList<>();
    private int monsterMoveCooldown = 0;
    private static final int MONSTER_MOVE_DELAY = 3; // lower = faster

    private final WeaponSystem weaponSystem = new WeaponSystem(2);
    private final Random rng = new Random();

    private int wave = 1;
    private int timeSeconds = 0;

    private static final long FRAME_MS = 1000L / Config.TARGET_FPS;

    public Game(String playerName) {
        this.arena = new Arena();
        this.player = new Player(arena.getPlayerSpawn());
    }

    public void start() throws Exception {
        Screen screen = new DefaultTerminalFactory().createScreen();
        screen.startScreen();
        screen.setCursorPosition(null);

        long lastSecondMark = System.currentTimeMillis();

        // ✅ spawn the first wave ONCE before the loop
        spawnWave();

        try {
            while (state == GameState.PLAYING) {
                long frameStart = System.currentTimeMillis();

                // time counter
                long now = System.currentTimeMillis();
                if (now - lastSecondMark >= 1000) {
                    timeSeconds++;
                    lastSecondMark += 1000;
                }

                // input
                KeyStroke key;
                while ((key = screen.pollInput()) != null) {
                    var input = InputHandler.handleKey(key, player);
                    switch (input.action) {
                        case MOVE -> MovementSystem.movePlayer(player, new Vec2(input.dx, input.dy), arena);
                        case SHOOT -> weaponSystem.shoot(player, arena, bullets);
                        case QUIT -> state = GameState.GAME_OVER;
                        case NONE -> {
                        }
                    }
                }

                // update
                tick();

                // render
                Renderer.render(screen, player, arena.getWalls(), bullets, monsters, wave, timeSeconds);

                // fps cap
                long elapsed = System.currentTimeMillis() - frameStart;
                if (elapsed < FRAME_MS)
                    Thread.sleep(FRAME_MS - elapsed);
            }
        } finally {
            screen.stopScreen();
        }

        System.out.println("Game over! Final score: " + player.getScore());
    }

    private void tick() {
        weaponSystem.tick();
        MovementSystem.updateBullets(bullets, arena);

        // bullets ↔ monsters
        CombatSystem.processBulletCollisions(bullets, monsters, player);

        // monsters → player damage
        CombatSystem.applyMonsterDamage(player, monsters);

        // ===== MONSTER MOVEMENT (WITH COOLDOWN) =====
        if (monsterMoveCooldown > 0) {
            monsterMoveCooldown--;
        } else {
            MonsterMovementSystem.moveMonsters(
                    monsters,
                    player.getPosition(),
                    arena);
            monsterMoveCooldown = MONSTER_MOVE_DELAY;
        }

        // player death check
        if (player.getHealth() <= 0) {
            state = GameState.GAME_OVER;
            return;
        }

        // ===== WAVE SYSTEM =====
        if (monsters.isEmpty()) {
            wave++;

            arena.nextMap(); // map changes PER WAVE
            bullets.clear();
            player.setPosition(arena.getPlayerSpawn());

            spawnWave(); // spawn next wave
        }
    }

    private void spawnWave() {
        monsters.clear();

        int totalMonsters = 3 + wave * 2;

        for (int i = 0; i < totalMonsters; i++) {
            Monster monster;
            Vec2 pos = findSpawnPosition();

            if (pos == null)
                break;

            if (wave >= 5 && rng.nextDouble() < 0.2) {
                monster = new TankMonster(pos);
            } else if (wave >= 3 && rng.nextDouble() < 0.4) {
                monster = new FastMonster(pos);
            } else {
                monster = new BasicMonster(pos);
            }

            monsters.add(monster);
        }
    }

    private Vec2 findSpawnPosition() {
        int tries = 0;
        final int MIN_SPAWN_DISTANCE = 7;

        while (tries < 1000) {
            int x = rng.nextInt(Config.ARENA_WIDTH);
            int y = rng.nextInt(Config.ARENA_HEIGHT);
            Vec2 pos = new Vec2(x, y);

            if (!arena.isWalkable(pos))
                continue;
            if (pos.equals(player.getPosition()))
                continue;

            int dist = Math.abs(pos.x - player.getPosition().x) + Math.abs(pos.y - player.getPosition().y);

            if (dist < MIN_SPAWN_DISTANCE)
                continue;

            boolean occupied = false;
            for (Monster m : monsters) {
                if (m.getPosition().equals(pos)) {
                    occupied = true;
                    break;
                }
            }
            if (!occupied)
                return pos;

            tries++;
        }
        return null;
    }
}
