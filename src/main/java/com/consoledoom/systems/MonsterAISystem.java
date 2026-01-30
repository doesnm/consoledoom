package com.consoledoom.systems;

import com.consoledoom.arena.Arena;
import com.consoledoom.entities.Bullet;
import com.consoledoom.entities.monsters.Monster;
import com.consoledoom.utils.AStarPathfinder;
import com.consoledoom.utils.Vec2;

import java.util.List;
import java.util.Random;

public class MonsterAISystem {

    private static final int[][] DIRS5 = { { 0, 0 }, { 0, -1 }, { 0, 1 }, { -1, 0 }, { 1, 0 } };
    private static final Random rng = new Random();

    public static void moveMonstersSmart(
            List<Monster> monsters,
            Vec2 playerPos,
            Arena arena,
            List<Bullet> bullets,
            AiLevel level) {
        AStarPathfinder pathfinder = new AStarPathfinder(arena, monsters);

        for (Monster monster : monsters) {
            Vec2 cur = monster.getPosition();
            if (cur.equals(playerPos))
                continue;

            Vec2 chaseStep = pathfinder.findNextStep(cur, playerPos);

            if (level.horizonTicks <= 0 || level.dangerWeight <= 0) {
                if (chaseStep != null)
                    monster.setPosition(chaseStep);
                continue;
            }

            if (chaseStep == null) {
                continue;
            }

            int dangerOnChaseStep = dangerAt(chaseStep, bullets, arena, level.horizonTicks);

            if (dangerOnChaseStep == 0) {
                monster.setPosition(chaseStep);
                continue;
            }

            Vec2 best = cur;
            int bestScore = Integer.MIN_VALUE;

            for (int[] d : DIRS5) {
                Vec2 cand = new Vec2(cur.x + d[0], cur.y + d[1]);
                if (!isWalkableForMonster(cand, monster, arena, monsters))
                    continue;

                int danger = dangerAt(cand, bullets, arena, level.horizonTicks);

                int pathDist = pathfinder.findPathLength(cand, playerPos);
                if (pathDist < 0)
                    pathDist = 1000;

                int score = -pathDist * 10 - danger * level.dangerWeight;

                if (cand.equals(chaseStep))
                    score += 15;

                if (cand.equals(cur))
                    score -= 5;

                if (level.randomnessPercent > 0) {
                    score += rng.nextInt(level.randomnessPercent + 1);
                }

                if (score > bestScore) {
                    bestScore = score;
                    best = cand;
                }
            }

            monster.setPosition(best);
        }
    }

    private static boolean isWalkableForMonster(Vec2 pos, Monster self, Arena arena, List<Monster> monsters) {
        if (!arena.isInside(pos))
            return false;
        if (arena.isWall(pos))
            return false;

        for (Monster m : monsters) {
            if (m == self)
                continue;
            if (m.getPosition().equals(pos))
                return false;
        }
        return true;
    }

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

                if (!arena.isInside(next) || arena.isWall(next))
                    break;

                if (next.x == cell.x && next.y == cell.y) {
                    danger += (horizon - t + 1);
                    break;
                }
            }
        }
        return danger;
    }
}
