package com.consoledoom.systems;

import com.consoledoom.arena.Arena;
import com.consoledoom.entities.monsters.Monster;
import com.consoledoom.utils.Vec2;

import java.util.List;

public class MonsterMovementSystem {

    public static void moveMonsters(List<Monster> monsters, Vec2 playerPos, Arena arena) {

        for (Monster m : monsters) {
            Vec2 mp = m.getPosition();

            int dx = Integer.compare(playerPos.x, mp.x); // -1, 0, 1
            int dy = Integer.compare(playerPos.y, mp.y);

            Vec2 next = mp;

            // try horizontal move first
            if (dx != 0) {
                Vec2 tryX = new Vec2(mp.x + dx, mp.y);
                if (canMove(tryX, arena, monsters)) {
                    next = tryX;
                }
            }

            // if X blocked, try vertical
            if (next.equals(mp) && dy != 0) {
                Vec2 tryY = new Vec2(mp.x, mp.y + dy);
                if (canMove(tryY, arena, monsters)) {
                    next = tryY;
                }
            }

            m.setPosition(next);
        }
    }

    private static boolean canMove(Vec2 pos, Arena arena, List<Monster> monsters) {
        if (!arena.isWalkable(pos)) return false;

        for (Monster other : monsters) {
            if (other.getPosition().equals(pos)) return false;
        }

        return true;
    }
}
