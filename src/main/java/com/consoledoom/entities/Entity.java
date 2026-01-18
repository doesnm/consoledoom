package com.consoledoom.entities;

import com.consoledoom.utils.Vec2;
import com.googlecode.lanterna.TextColor;

public abstract class Entity {
    protected Vec2 position;
    protected char symbol;
    protected TextColor color;

    public Entity(Vec2 position, char symbol, TextColor color) {
        this.position = position;
        this.symbol = symbol;
        this.color = color;
    }

    public void setPosition(Vec2 pos) {
        this.position = pos;
    }

    public Vec2 getPosition() {
        return position;
    }

    public char getSymbol() {
        return symbol;
    }

    public TextColor getColor() {
        return color;
    }
}
