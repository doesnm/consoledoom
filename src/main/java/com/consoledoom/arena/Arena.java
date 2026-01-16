package com.consoledoom.arena;

import com.consoledoom.core.Config;
import com.consoledoom.entities.Wall;
import com.consoledoom.utils.Vec2;

import java.util.ArrayList;
import java.util.List;

public class Arena {
    private final List<Wall> walls = new ArrayList<>();

    public Arena() {
        for (int x = 10; x <= 15; x++) walls.add(new Wall(new Vec2(x, 5)));
        for (int y = 5; y <= 10; y++) walls.add(new Wall(new Vec2(15, y)));
        walls.add(new Wall(new Vec2(20, 8)));
        walls.add(new Wall(new Vec2(21, 8)));
        walls.add(new Wall(new Vec2(22, 8)));
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public boolean isInside(Vec2 p) {
        return p.x > 0 && p.x < Config.ARENA_WIDTH - 1
                && p.y > 0 && p.y < Config.ARENA_HEIGHT - 1;
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
