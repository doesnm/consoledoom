package com.consoledoom.entities;

import com.consoledoom.core.Config;
import com.consoledoom.utils.Vec2;

public class Bullet extends Entity {
    private final Vec2 dir; // direction vector like (1,0)

    public Bullet(Vec2 position, Vec2 dir) {
        super(position, Config.BULLET_SYMBOL);
        this.dir = dir;
    }

    public Vec2 getDir() {
        return dir;
    }
}
