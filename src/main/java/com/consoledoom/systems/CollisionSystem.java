package com.consoledoom.systems;

import com.consoledoom.entities.Bullet;
import com.consoledoom.entities.monsters.Monster;
import com.consoledoom.utils.Vec2;

import java.util.List;

public class CollisionSystem {

    public static boolean playerTouchingMonster(Vec2 playerPos, List<Monster> monsters) {
        for (Monster m : monsters) {
            if (m.getPosition().equals(playerPos)) {
                return true;
            }
        }
        return false;
    }

    public static Monster bulletHitMonster(Bullet bullet, List<Monster> monsters) {
        Vec2 pos = bullet.getPosition();
        for (Monster m : monsters) {
            if (m.getPosition().equals(pos)) {
                return m;
            }
        }
        return null;
    }
}
