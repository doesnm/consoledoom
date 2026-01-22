package com.consoledoom.systems;

import com.consoledoom.arena.Arena;
import com.consoledoom.entities.Bullet;
import com.consoledoom.entities.monsters.Monster;
import com.consoledoom.utils.AStarPathfinder;
import com.consoledoom.utils.Vec2;

import java.util.List;
import java.util.Random;

public class MonsterAISystem {

    private static final int[][] DIRS5 = { {0,0}, {0,-1}, {0,1}, {-1,0}, {1,0} };
    private static final Random rng = new Random();

    public static void moveMonstersSmart(
            List<Monster> monsters,
            Vec2 playerPos,
            Arena arena,
            List<Bullet> bullets,
            AiLevel level
    ) {
        AStarPathfinder pathfinder = new AStarPathfinder(arena, monsters);

        for (Monster monster : monsters) {
            Vec2 cur = monster.getPosition();
            if (cur.equals(playerPos)) continue;

            Vec2 chaseStep = pathfinder.findNextStep(cur, playerPos);

            // If no dodging, keep original behavior
            if (level.horizonTicks <= 0 || level.dangerWeight <= 0) {
                if (chaseStep != null) monster.setPosition(chaseStep);
                continue;
            }

            Vec2 best = cur;
            int bestScore = Integer.MIN_VALUE;

            for (int[] d : DIRS5) {
                Vec2 cand = new Vec2(cur.x + d[0], cur.y + d[1]);
                if (!isWalkableForMonster(cand, monster, arena, monsters)) continue;

                int dist = manhattan(cand, playerPos);
                int danger = dangerAt(cand, bullets, arena, level.horizonTicks);

                int score = -dist * 10 - danger * level.dangerWeight;

                // Small bonus to keep chase “purposeful”
                if (chaseStep != null && cand.equals(chaseStep)) score += 8;

                // Noise so they aren’t perfect robots
                if (level.randomnessPercent > 0) score += rng.nextInt(level.randomnessPercent + 1);

                if (score > bestScore) {
                    bestScore = score;
                    best = cand;
                }
            }

            monster.setPosition(best);
        }
    }

    private static int manhattan(Vec2 a, Vec2 b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    private static boolean isWalkableForMonster(Vec2 pos, Monster self, Arena arena, List<Monster> monsters) {
        if (!arena.isInside(pos)) return false;
        if (arena.isWall(pos)) return false;

        for (Monster m : monsters) {
            if (m == self) continue;
            if (m.getPosition().equals(pos)) return false;
        }
        return true;
    }

    // Predict bullet positions up to 'horizon' ticks and score danger
    private static int dangerAt(Vec2 cell, List<Bullet> bullets, Arena arena, int horizon) {
        int danger = 0;

        for (Bullet b : bullets) {
            Vec2 p = b.getPosition();
            Vec2 dir = b.getDir();

            int x = p.x;
            int y = p.y;

            for (int t = 1; t <= horizon; t++) {
                x += dir.x;
                y += dir.y;

                Vec2 next = new Vec2(x, y);

                // bullet disappears at wall/outside
                if (!arena.isInside(next) || arena.isWall(next)) break;

                if (next.x == cell.x && next.y == cell.y) {
                    // sooner hit => bigger danger
                    danger += (horizon - t + 1);
                    break;
                }
            }
        }
        return danger;
    }
}
