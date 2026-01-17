package com.consoledoom.systems;

import com.consoledoom.arena.Arena;
import com.consoledoom.entities.Bullet;
import com.consoledoom.entities.Player;
import com.consoledoom.utils.Vec2;

import java.util.Iterator;
import java.util.List;

public class MovementSystem {

    public static void movePlayer(Player player, Vec2 delta, Arena arena) {
        Vec2 newPos = new Vec2(player.getPosition().x + delta.x, player.getPosition().y + delta.y);
        if (arena.isWalkable(newPos)) {
            player.setPosition(newPos);
        }
    }

    public static void updateBullets(List<Bullet> bullets, Arena arena) {
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet b = it.next();
            Vec2 next = new Vec2(b.getPosition().x + b.getDir().x, b.getPosition().y + b.getDir().y);
            if (!arena.isInside(next) || arena.isWall(next)) {
                it.remove();
            } else {
                b.setPosition(next);
            }
        }
    }
}
