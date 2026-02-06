package com.consoledoom.security;

import java.util.Set;
import java.util.EnumSet;

public enum Role {
    PLAYER(1),
    EDITOR(2),
    MANAGER(3),
    ADMIN(4);

    private final int level;

    Role(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public boolean hasPermission(Permission permission) {
        return getPermissions().contains(permission);
    }

    public Set<Permission> getPermissions() {
        return switch (this) {
            case PLAYER -> EnumSet.of(
                    Permission.PLAY_GAME,
                    Permission.VIEW_LEADERBOARD,
                    Permission.VIEW_OWN_STATS);
            case EDITOR -> EnumSet.of(
                    Permission.PLAY_GAME,
                    Permission.VIEW_LEADERBOARD,
                    Permission.VIEW_OWN_STATS,
                    Permission.EDIT_OWN_RECORDS);
            case MANAGER -> EnumSet.of(
                    Permission.PLAY_GAME,
                    Permission.VIEW_LEADERBOARD,
                    Permission.VIEW_OWN_STATS,
                    Permission.EDIT_OWN_RECORDS,
                    Permission.VIEW_ALL_USERS,
                    Permission.DELETE_GAME_RECORDS);
            case ADMIN -> EnumSet.allOf(Permission.class);
        };
    }

    public boolean canManage(Role other) {
        return this.level > other.level;
    }
}
