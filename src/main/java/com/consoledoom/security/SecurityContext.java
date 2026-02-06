package com.consoledoom.security;

import com.consoledoom.models.User;
import java.util.Optional;

public class SecurityContext {
    private static SecurityContext instance;
    private User currentUser;

    private SecurityContext() {
    }

    public static synchronized SecurityContext getInstance() {
        if (instance == null) {
            instance = new SecurityContext();
        }
        return instance;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    public void logout() {
        this.currentUser = null;
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }

    public boolean hasPermission(Permission permission) {
        return currentUser != null && currentUser.getRole().hasPermission(permission);
    }

    public boolean hasRole(Role role) {
        return currentUser != null && currentUser.getRole().getLevel() >= role.getLevel();
    }

    public void checkPermission(Permission permission) {
        if (!hasPermission(permission)) {
            throw new SecurityException("Access denied: missing permission " + permission);
        }
    }

    public void checkRole(Role role) {
        if (!hasRole(role)) {
            throw new SecurityException("Access denied: requires role " + role + " or higher");
        }
    }
}
