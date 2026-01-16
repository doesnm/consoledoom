package com.consoledoom.arena;

import com.consoledoom.entities.Wall;
import com.consoledoom.utils.Vec2;

import java.util.ArrayList;
import java.util.List;

public class Arena {
    private List<Wall> walls = new ArrayList<>();

    public Arena() {
        for (int x = 10; x <= 15; x++) {
            walls.add(new Wall(new Vec2(x, 5)));
        }
        for (int y = 5; y <= 10; y++) {
            walls.add(new Wall(new Vec2(15, y)));
        }
        walls.add(new Wall(new Vec2(20, 8)));
        walls.add(new Wall(new Vec2(21, 8)));
        walls.add(new Wall(new Vec2(22, 8)));
    }

    public List<Wall> getWalls() {
        return walls;
    }
}
