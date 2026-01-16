package com.consoledoom.entities;

import com.consoledoom.core.Config;
import com.consoledoom.utils.Vec2;

public class Player extends Entity {
    private int health;
    private int kills;
    private int score;

    public Player(Vec2 position) {
        super(position, Config.PLAYER_SYMBOL);
        this.health = Config.MAX_HEALTH;
        this.kills = 0;
        this.score = 0;
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
}
