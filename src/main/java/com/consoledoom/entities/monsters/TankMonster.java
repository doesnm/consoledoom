package com.consoledoom.entities.monsters;

import com.consoledoom.utils.Vec2;
import com.googlecode.lanterna.TextColor;

public class TankMonster extends Monster {
    public TankMonster(Vec2 position) {
        super(position, 'T', TextColor.ANSI.YELLOW_BRIGHT, 3, 2, 5); // медленный, но крепкий
    }
}
