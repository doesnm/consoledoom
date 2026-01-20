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
import com.consoledoom.db.Database;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {
    private final StringBuilder nameBuffer = new StringBuilder();
    private boolean savedThisRun = false;
    private List<Database.LeaderboardRow> cachedTop = new ArrayList<>();

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
            while (true) {
                long frameStart = System.currentTimeMillis();

                // time counter (only while playing)
                if (state == GameState.PLAYING) {
                    long now = System.currentTimeMillis();
                    if (now - lastSecondMark >= 1000) {
                        timeSeconds++;
                        lastSecondMark += 1000;
                    }
                }

                // input
                KeyStroke key;
                while ((key = screen.pollInput()) != null) {

                    if (state == GameState.PLAYING) {
                        var input = InputHandler.handleKey(key, player);
                        switch (input.action) {
                            case MOVE -> MovementSystem.movePlayer(player, new Vec2(input.dx, input.dy), arena);
                            case SHOOT -> weaponSystem.shoot(player, arena, bullets);
                            case QUIT -> {
                                state = GameState.GAME_OVER_NAME;
                                nameBuffer.setLength(0);
                                savedThisRun = false;
                            }
                            case NONE -> {}
                        }
                    }
                    else if (state == GameState.GAME_OVER_NAME) {
                        handleNameInput(key);
                    }
                    else if (state == GameState.LEADERBOARD) {
                        // Q or ESC to exit
                        if (key.getKeyType() == com.googlecode.lanterna.input.KeyType.Escape ||
                                (key.getKeyType() == com.googlecode.lanterna.input.KeyType.Character
                                        && (key.getCharacter() == 'q' || key.getCharacter() == 'Q'))) {
                            return;
                        }
                    }
                }


                // update
                if (state == GameState.PLAYING) {
                    tick();
                }

                if (state == GameState.PLAYING) {
                    Renderer.render(screen, player, arena.getWalls(), bullets, monsters, wave, timeSeconds);
                } else if (state == GameState.GAME_OVER_NAME) {
                    renderGameOverName(screen);
                } else if (state == GameState.LEADERBOARD) {
                    renderLeaderboard(screen);
                }


                // fps cap
                long elapsed = System.currentTimeMillis() - frameStart;
                if (elapsed < FRAME_MS)
                    Thread.sleep(FRAME_MS - elapsed);
            }
        } finally {
            screen.stopScreen();
        }
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
            state = GameState.GAME_OVER_NAME;
            nameBuffer.setLength(0);
            savedThisRun = false;
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
    private void handleNameInput(KeyStroke key) {
        var type = key.getKeyType();

        if (type == com.googlecode.lanterna.input.KeyType.Character) {
            char ch = key.getCharacter();
            if (Character.isLetterOrDigit(ch) || ch == '_' || ch == '-') {
                if (nameBuffer.length() < 16) nameBuffer.append(ch);
            }
        } else if (type == com.googlecode.lanterna.input.KeyType.Backspace) {
            if (nameBuffer.length() > 0) nameBuffer.deleteCharAt(nameBuffer.length() - 1);
        } else if (type == com.googlecode.lanterna.input.KeyType.Enter) {
            String nickname = nameBuffer.toString().trim();
            if (nickname.isEmpty()) nickname = "Player";

            if (!savedThisRun) {
                int score = player.getScore();
                int kills = player.getKills();
                int deaths = 1; // one run = one death
                int finalWave = wave;
                int timeSurvivedSec = timeSeconds;

                Database.saveGameSession(nickname, score, kills, deaths, finalWave, timeSurvivedSec);
                cachedTop = Database.topSessions(10);
                savedThisRun = true;
            }

            state = GameState.LEADERBOARD;
        } else if (type == com.googlecode.lanterna.input.KeyType.Escape) {
            cachedTop = Database.topSessions(10);
            state = GameState.LEADERBOARD;
        }
    }

    private void renderGameOverName(Screen screen) throws Exception {
        screen.clear();
        var g = screen.newTextGraphics();

        g.putString(2, 2, "=== GAME OVER ===");
        g.putString(2, 4, "Final score: " + player.getScore());
        g.putString(2, 5, "Kills: " + player.getKills());
        g.putString(2, 6, "Wave: " + wave);
        g.putString(2, 7, String.format("Time: %02d:%02d", timeSeconds / 60, timeSeconds % 60));

        g.putString(2, 9, "Enter nickname (ENTER to save):");
        g.putString(2, 10, "> " + nameBuffer);

        g.putString(2, 12, "Backspace=delete | ESC=skip save");
        screen.refresh();
    }

    private void renderLeaderboard(Screen screen) throws Exception {
        screen.clear();
        var g = screen.newTextGraphics();

        g.putString(2, 2, "=== LEADERBOARD TOP 10 ===");
        g.putString(2, 4, String.format("%-16s %6s %5s %5s %6s %5s %6s",
                "NICK", "SCORE", "K", "D", "KD", "W", "TIME"));

        int y = 6;
        int rank = 1;
        for (var row : cachedTop) {
            String time = String.format("%02d:%02d", row.timeSurvivedSec / 60, row.timeSurvivedSec % 60);
            g.putString(2, y++, String.format(
                    "%2d) %-16s %6d %5d %5d %6s %5d %6s",
                    rank++,
                    row.nickname,
                    row.score,
                    row.kills,
                    row.deaths,
                    row.kd.toPlainString(),
                    row.wave,
                    time
            ));
            if (y > 20) break;
        }

        g.putString(2, y + 2, "Press Q or ESC to quit");
        screen.refresh();
    }

}
