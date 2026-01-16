package com.consoledoom.entities;

import com.consoledoom.core.Config;
import com.consoledoom.utils.Vec2;

public class Wall extends Entity {
    public Wall(Vec2 position) {
        super(position, Config.WALL_SYMBOL);
    }
}
