package com.consoledoom.entities;

import com.consoledoom.core.Config;
import com.consoledoom.utils.Vec2;
import com.googlecode.lanterna.TextColor;

public class Player extends Entity {
    private int health;
    private int kills;
    private int score;

    private Vec2 facing = new Vec2(1, 0); // default: right

    public Player(Vec2 position) {
        super(position, '@', TextColor.ANSI.WHITE_BRIGHT);
        this.health = Config.MAX_HEALTH;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = Math.max(health, 0);
    }

    public int getKills() {
        return kills;
    }

    public void addKill() {
        kills++;
        score += 100;
    }

    public int getScore() {
        return score;
    }

    public Vec2 getFacing() {
        return facing;
    }

    public void setFacing(Vec2 facing) {
        this.facing = facing;
    }
}
