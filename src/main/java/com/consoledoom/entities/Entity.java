package com.consoledoom.entities;

import com.consoledoom.utils.Vec2;

public abstract class Entity {
    protected Vec2 position;
    protected char symbol;

    public Entity(Vec2 position, char symbol) {
        this.position = position;
        this.symbol = symbol;
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
}
