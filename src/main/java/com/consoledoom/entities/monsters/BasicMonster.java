package com.consoledoom.entities.monsters;

import com.consoledoom.utils.Vec2;
import com.googlecode.lanterna.TextColor;

public class BasicMonster extends Monster {
    public BasicMonster(Vec2 position) {
        super(position, 'M', TextColor.ANSI.RED, 1, 1, 3);
    }
}
