package com.consoledoom.utils;

import com.consoledoom.arena.Arena;
import com.consoledoom.entities.monsters.Monster;

import java.util.*;

public class AStarPathfinder {

    private final Arena arena;
    private final List<Monster> monsters;
    private static final int[][] DIRS = { { 0, -1 }, { 0, 1 }, { -1, 0 }, { 1, 0 } };

    public AStarPathfinder(Arena arena, List<Monster> monsters) {
        this.arena = arena;
        this.monsters = monsters;
    }

    public Vec2 findNextStep(Vec2 start, Vec2 goal) {
        if (start.equals(goal))
            return null;

        for (int[] dir : DIRS) {
            Vec2 neighbor = new Vec2(start.x + dir[0], start.y + dir[1]);
            if (neighbor.equals(goal) && isWalkable(neighbor, start)) {
                return neighbor;
            }
        }

        Node result = aStar(start, goal);
        if (result == null)
            return null;

        List<Vec2> path = reconstructPath(result);
        if (path.size() < 2)
            return null;
        return path.get(1); // [0] = start, [1] = next step
    }

    private Node aStar(Vec2 start, Vec2 goal) {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fScore));
        Map<Vec2, Node> allNodes = new HashMap<>();

        Node startNode = new Node(start, null, 0, heuristic(start, goal));
        openSet.add(startNode);
        allNodes.put(start, startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.position.equals(goal)) {
                return current;
            }

            for (int[] dir : DIRS) {
                Vec2 neighborPos = new Vec2(current.position.x + dir[0], current.position.y + dir[1]);

                if (!isWalkable(neighborPos, current.position))
                    continue;

                double tentativeGScore = current.gScore + 1;

                Node neighbor = allNodes.get(neighborPos);
                if (neighbor == null) {
                    neighbor = new Node(
                            neighborPos,
                            current,
                            tentativeGScore,
                            tentativeGScore + heuristic(neighborPos, goal));
                    allNodes.put(neighborPos, neighbor);
                    openSet.add(neighbor);
                } else if (tentativeGScore < neighbor.gScore) {
                    openSet.remove(neighbor);
                    neighbor.cameFrom = current;
                    neighbor.gScore = tentativeGScore;
                    neighbor.fScore = tentativeGScore + heuristic(neighborPos, goal);
                    openSet.add(neighbor);
                }
            }
        }

        return null;
    }

    private double heuristic(Vec2 a, Vec2 b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    private boolean isWalkable(Vec2 pos, Vec2 from) {
        if (!arena.isInside(pos))
            return false;
        if (arena.isWall(pos))
            return false;

        for (Monster m : monsters) {
            if (m.getPosition().equals(pos) && !pos.equals(from)) {
                return false;
            }
        }
        return true;
    }

    private List<Vec2> reconstructPath(Node node) {
        List<Vec2> path = new ArrayList<>();
        while (node != null) {
            path.add(node.position);
            node = node.cameFrom;
        }
        Collections.reverse(path);
        return path;
    }

    private static class Node {
        final Vec2 position;
        Node cameFrom;
        double gScore;
        double fScore;

        Node(Vec2 position, Node cameFrom, double gScore, double fScore) {
            this.position = position;
            this.cameFrom = cameFrom;
            this.gScore = gScore;
            this.fScore = fScore;
        }
    }
}
