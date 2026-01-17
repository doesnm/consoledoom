package com.consoledoom.systems;

import com.consoledoom.entities.Bullet;
import com.consoledoom.entities.Player;
import com.consoledoom.entities.monsters.Monster;

import java.util.Iterator;
import java.util.List;

public class CombatSystem {

    public static void processBulletCollisions(List<Bullet> bullets, List<Monster> monsters, Player player) {
        Iterator<Bullet> bulletIt = bullets.iterator();
        while (bulletIt.hasNext()) {
            Bullet b = bulletIt.next();
            Monster hit = CollisionSystem.bulletHitMonster(b, monsters);
            if (hit != null) {
                hit.takeDamage(1);
                bulletIt.remove();
                if (hit.isDead()) {
                    monsters.remove(hit);
                    player.addKill();
                }
            }
        }
    }

    public static void applyMonsterDamage(Player player, List<Monster> monsters) {
        if (CollisionSystem.playerTouchingMonster(player.getPosition(), monsters)) {
            player.setHealth(player.getHealth() - 1);
        }
    }
}
