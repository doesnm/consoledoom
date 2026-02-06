package com.consoledoom.security;

public enum Permission {
    // Player permissions
    PLAY_GAME,
    VIEW_LEADERBOARD,
    VIEW_OWN_STATS,

    // Editor permissions
    EDIT_OWN_RECORDS,

    // Manager permissions
    VIEW_ALL_USERS,
    DELETE_GAME_RECORDS,

    // Admin permissions
    DELETE_USERS,
    MANAGE_ROLES,
    VIEW_ADMIN_PANEL
}
