// src/main/java/com/consoledoom/security/Permission.java
package com.consoledoom.security;

/**
 * Fine-grained permissions for role-based access control.
 */
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
