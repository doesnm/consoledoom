package com.consoledoom.entities;

import com.consoledoom.utils.Vec2;
import com.googlecode.lanterna.TextColor;

public class Wall extends Entity {
    public Wall(Vec2 position) {
        super(position, '#', TextColor.ANSI.BLACK_BRIGHT);
    }
}
