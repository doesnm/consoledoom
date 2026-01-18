package com.consoledoom.entities;

import com.consoledoom.utils.Vec2;
import com.googlecode.lanterna.TextColor;

public class Bullet extends Entity {
    private final Vec2 dir; // direction vector like (1,0)

    public Bullet(Vec2 position, Vec2 dir) {
        super(position, '*', TextColor.ANSI.YELLOW);
        this.dir = dir;
    }

    public Vec2 getDir() {
        return dir;
    }
}
