package com.consoledoom.factory;

import com.consoledoom.entities.monsters.*;
import com.consoledoom.utils.Vec2;
import java.util.Random;
import java.util.function.Function;
import java.util.Map;
import java.util.HashMap;

public class MonsterFactory {
    private static final Random random = new Random();

    private static final Map<MonsterType, Function<Vec2, Monster>> creators = new HashMap<>();

    static {
        creators.put(MonsterType.BASIC, BasicMonster::new);
        creators.put(MonsterType.FAST, FastMonster::new);
        creators.put(MonsterType.TANK, TankMonster::new);
    }

    public enum MonsterType {
        BASIC, FAST, TANK
    }

    public static Monster createMonster(MonsterType type, Vec2 position) {
        Function<Vec2, Monster> creator = creators.get(type);
        if (creator == null) {
            throw new IllegalArgumentException("Unknown monster type: " + type);
        }
        return creator.apply(position);
    }

    public static Monster createRandomMonster(Vec2 position, int wave) {
        Function<Integer, MonsterType> waveStrategy = w -> {
            double roll = random.nextDouble();
            if (w >= 5 && roll < 0.2) {
                return MonsterType.TANK;
            } else if (w >= 3 && roll < 0.4) {
                return MonsterType.FAST;
            }
            return MonsterType.BASIC;
        };

        return createMonster(waveStrategy.apply(wave), position);
    }

    public static void registerCreator(MonsterType type, Function<Vec2, Monster> creator) {
        creators.put(type, creator);
    }
}
