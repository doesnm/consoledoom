package com.consoledoom.arena;

import com.consoledoom.core.Config;
import com.consoledoom.entities.Wall;
import com.consoledoom.utils.Vec2;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Arena {
    private final List<Wall> walls = new ArrayList<>();
    private Vec2 playerSpawn = new Vec2(5, 8);

    private final String[] maps = {"/maps/map1.txt", "/maps/map2.txt"};
    private int mapIndex = 0;

    public Arena() {
        loadMap(maps[mapIndex]);
    }

    public void nextMap() {
        mapIndex = (mapIndex + 1) % maps.length;
        loadMap(maps[mapIndex]);
    }

    public Vec2 getPlayerSpawn() {
        return playerSpawn.copy();
    }

    private void loadMap(String resourcePath) {
        walls.clear();
        playerSpawn = new Vec2(5, 8);

        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) throw new RuntimeException("Map not found: " + resourcePath);

            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String line;
            int y = 0;

            while ((line = br.readLine()) != null) {
                for (int x = 0; x < line.length(); x++) {
                    char c = line.charAt(x);
                    Vec2 pos = new Vec2(x, y);

                    if (c == '#') walls.add(new Wall(pos));
                    else if (c == '@') playerSpawn = pos;
                }
                y++;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load map: " + resourcePath, e);
        }
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public boolean isInside(Vec2 p) {
        return p.x >= 0 && p.x < Config.ARENA_WIDTH
                && p.y >= 0 && p.y < Config.ARENA_HEIGHT;
    }

    public boolean isWall(Vec2 p) {
        for (Wall w : walls) {
            if (w.getPosition().equals(p)) return true;
        }
        return false;
    }

    public boolean isWalkable(Vec2 p) {
        return isInside(p) && !isWall(p);
    }
}
