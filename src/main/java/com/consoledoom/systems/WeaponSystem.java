package com.consoledoom.systems;

import com.consoledoom.arena.Arena;
import com.consoledoom.entities.Bullet;
import com.consoledoom.entities.Player;
import com.consoledoom.utils.Vec2;

import java.util.List;

public class WeaponSystem {

    private int shootCooldownTicks = 0;
    private final int maxCooldown;

    public WeaponSystem(int maxCooldown) {
        this.maxCooldown = maxCooldown;
    }

    public boolean canShoot() {
        return shootCooldownTicks <= 0;
    }

    public void shoot(Player player, Arena arena, List<Bullet> bullets) {
        if (!canShoot())
            return;

        Vec2 dir = player.getFacing();
        if (dir == null || (dir.x == 0 && dir.y == 0)) {
            dir = new Vec2(1, 0); // default right
        }

        Vec2 spawn = new Vec2(
                player.getPosition().x + dir.x,
                player.getPosition().y + dir.y);

        if (arena.isInside(spawn) && !arena.isWall(spawn)) {
            bullets.add(new Bullet(spawn, dir));
            shootCooldownTicks = maxCooldown;
        }
    }

    public void tick() {
        if (shootCooldownTicks > 0) {
            shootCooldownTicks--;
        }
    }
}
