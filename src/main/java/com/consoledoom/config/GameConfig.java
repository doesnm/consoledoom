package com.consoledoom.config;

import java.io.InputStream;
import java.util.Properties;

public enum GameConfig {
    INSTANCE;

    private final Properties properties = new Properties();

    GameConfig() {
        loadProperties();
    }

    private void loadProperties() {
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (is == null) {
                throw new RuntimeException("application.properties not found");
            }
            properties.load(is);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    public String getDbUrl() {
        return properties.getProperty("db.url");
    }

    public String getDbUser() {
        return properties.getProperty("db.user");
    }

    public String getDbPassword() {
        return properties.getProperty("db.password");
    }

    public int getArenaWidth() {
        return getInt("game.arena.width", 60);
    }

    public int getArenaHeight() {
        return getInt("game.arena.height", 15);
    }

    public int getTargetFps() {
        return getInt("game.target.fps", 10);
    }

    public int getMaxHealth() {
        return getInt("game.max.health", 5);
    }

    public char getEmptySymbol() {
        String val = properties.getProperty("game.empty.symbol", " ");
        return val.isEmpty() ? ' ' : val.charAt(0);
    }

    public int getMonsterMoveDelay() {
        return getInt("game.monster.move.delay", 3);
    }

    public int getWeaponCooldown() {
        return getInt("game.weapon.cooldown", 2);
    }

    public int getMinSpawnDistance() {
        return getInt("game.min.spawn.distance", 7);
    }

    public int getPasswordMinLength() {
        return getInt("security.password.min.length", 4);
    }

    public int getPasswordMaxLength() {
        return getInt("security.password.max.length", 32);
    }

    public int getNicknameMinLength() {
        return getInt("security.nickname.min.length", 3);
    }

    public int getNicknameMaxLength() {
        return getInt("security.nickname.max.length", 16);
    }

    public String getNicknamePattern() {
        return properties.getProperty("security.nickname.pattern", "^[a-zA-Z0-9_-]+$");
    }

    public String getDefaultAdminUsername() {
        return properties.getProperty("admin.default.username", "admin");
    }

    public String getDefaultAdminPassword() {
        return properties.getProperty("admin.default.password", "admin123");
    }

    private int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
