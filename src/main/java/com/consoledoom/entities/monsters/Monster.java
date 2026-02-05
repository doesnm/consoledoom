package com.consoledoom.entities.monsters;

import com.consoledoom.entities.Entity;
import com.consoledoom.utils.Vec2;
import com.googlecode.lanterna.TextColor;

public abstract class Monster extends Entity {
    protected int health;
    protected int damage;
    protected int moveDelay;

    public Monster(Vec2 position, char symbol, TextColor color, int health, int damage, int moveDelay) {
        super(position, symbol, color);
        this.health = health;
        this.damage = damage;
        this.moveDelay = moveDelay;
    }

    public void takeDamage(int amount) {
        health -= amount;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public int getHealth() {
        return health;
    }

    public int getMoveDelay() {
        return moveDelay;
    }
}

