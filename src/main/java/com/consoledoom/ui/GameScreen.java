package com.consoledoom.ui;

import com.consoledoom.arena.Arena;
import com.consoledoom.core.Config;
import com.consoledoom.entities.Bullet;
import com.consoledoom.entities.Player;
import com.consoledoom.entities.monsters.Monster;
import com.consoledoom.render.Renderer;
import com.consoledoom.systems.*;
import com.consoledoom.utils.Vec2;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.consoledoom.systems.MonsterAISystem;
import com.consoledoom.systems.AiLevel;
import java.util.List;
import java.util.Random;

public class GameScreen {
    private final Arena arena;
    private final Player player;
    private final List<Bullet> bullets;
    private final List<Monster> monsters;
    private final WeaponSystem weaponSystem = new WeaponSystem(2);
    private final Random rng = new Random();

    private int wave = 1;
    private int timeSeconds = 0;
    private int monsterMoveCooldown = 0;
    private static final int MONSTER_MOVE_DELAY = 3;

    public GameScreen() {
        this.arena = new Arena();
        this.player = new Player(arena.getPlayerSpawn());
        this.bullets = new java.util.ArrayList<>();
        this.monsters = new java.util.ArrayList<>();
        spawnWave();
    }

    public void update() {
        weaponSystem.tick();
        MovementSystem.updateBullets(bullets, arena);
        CombatSystem.processBulletCollisions(bullets, monsters, player);
        CombatSystem.applyMonsterDamage(player, monsters);

        if (monsterMoveCooldown > 0) {
            monsterMoveCooldown--;
        } else {
            AiLevel level =
                    wave < 1 ? AiLevel.DUMB :
                            wave < 2 ? AiLevel.NORMAL :
                                    wave < 3 ? AiLevel.SMART :
                                            AiLevel.GODLIKE;

            MonsterAISystem.moveMonstersSmart(monsters, player.getPosition(), arena, bullets, level);
            monsterMoveCooldown = MONSTER_MOVE_DELAY;
        }

        if (monsters.isEmpty()) {
            wave++;
            arena.nextMap();
            bullets.clear();
            player.setPosition(arena.getPlayerSpawn());
            spawnWave();
        }
    }

    public void handleInput(KeyStroke key) {
        var input = InputHandler.handleKey(key, player);
        switch (input.action) {
            case MOVE -> MovementSystem.movePlayer(player, new Vec2(input.dx, input.dy), arena);
            case SHOOT -> weaponSystem.shoot(player, arena, bullets);
        }
    }

    public void render(Screen screen) throws Exception {
        Renderer.render(screen, player, arena.getWalls(), bullets, monsters, wave, timeSeconds);
    }

    public boolean isGameOver() {
        return player.getHealth() <= 0;
    }

    public int getScore() {
        return player.getScore();
    }

    public int getKills() {
        return player.getKills();
    }

    public int getWave() {
        return wave;
    }

    public int getTimeSeconds() {
        return timeSeconds;
    }

    public void incrementTime() {
        timeSeconds++;
    }

    private void spawnWave() {
        monsters.clear();
        int totalMonsters = 3 + wave * 2;
        for (int i = 0; i < totalMonsters; i++) {
            Vec2 pos = findSpawnPosition();
            if (pos == null)
                break;
            Monster monster = createMonster(pos);
            monsters.add(monster);
        }
    }

    private Monster createMonster(Vec2 pos) {
        if (wave >= 5 && rng.nextDouble() < 0.2) {
            return new com.consoledoom.entities.monsters.TankMonster(pos);
        } else if (wave >= 3 && rng.nextDouble() < 0.4) {
            return new com.consoledoom.entities.monsters.FastMonster(pos);
        } else {
            return new com.consoledoom.entities.monsters.BasicMonster(pos);
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
            boolean occupied = monsters.stream().anyMatch(m -> m.getPosition().equals(pos));
            if (!occupied)
                return pos;
            tries++;
        }
        return null;
    }
}
