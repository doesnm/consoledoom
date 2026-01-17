package com.consoledoom.entities.monsters;

import com.consoledoom.entities.Entity;
import com.consoledoom.utils.Vec2;

public abstract class Monster extends Entity {
    protected int health;
    protected int damage;

    public Monster(Vec2 position, char symbol, int health, int damage) {
        super(position, symbol);
        this.health = health;
        this.damage = damage;
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
}
