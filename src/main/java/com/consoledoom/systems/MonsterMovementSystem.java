package com.consoledoom.systems;

import com.consoledoom.arena.Arena;
import com.consoledoom.entities.monsters.Monster;
import com.consoledoom.utils.AStarPathfinder;
import com.consoledoom.utils.Vec2;

import java.util.List;

public class MonsterMovementSystem {

    public static void moveMonsters(List<Monster> monsters, Vec2 playerPos, Arena arena) {
        AStarPathfinder pathfinder = new AStarPathfinder(arena, monsters);

        for (Monster monster : monsters) {
            Vec2 current = monster.getPosition();
            if (current.equals(playerPos))
                continue;

            Vec2 nextStep = pathfinder.findNextStep(current, playerPos);
            if (nextStep != null) {
                monster.setPosition(nextStep);
            }
        }
    }
}
