package com.consoledoom.entities.monsters;

import com.consoledoom.utils.Vec2;
import com.googlecode.lanterna.TextColor;

public class FastMonster extends Monster {
    public FastMonster(Vec2 position) {
        super(position, 'F', TextColor.ANSI.MAGENTA, 1, 1, 1); // очень быстрый
    }
}
