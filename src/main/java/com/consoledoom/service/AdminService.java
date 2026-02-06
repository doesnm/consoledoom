package com.consoledoom.service;

import com.consoledoom.db.GameSessionDAO;
import com.consoledoom.db.UserDAO;
import com.consoledoom.models.LeaderboardEntry;
import com.consoledoom.models.User;
import com.consoledoom.security.Permission;
import com.consoledoom.security.Role;
import com.consoledoom.security.SecurityContext;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class AdminService {
    private final UserDAO userDAO;
    private final GameSessionDAO sessionDAO;
    private final SecurityContext securityContext;

    public AdminService() {
        this.userDAO = new UserDAO();
        this.sessionDAO = new GameSessionDAO();
        this.securityContext = SecurityContext.getInstance();
    }

    public List<User> getAllUsers() {
        return executeSecured(
                Permission.VIEW_ALL_USERS,
                () -> {
                    try {
                        return userDAO.getAllUsers();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return Collections.emptyList();
                    }
                });
    }

    public OperationResult deleteUser(int userId) {
        if (!securityContext.hasPermission(Permission.DELETE_USERS)) {
            return OperationResult.failure("Access denied: Admin permission required");
        }

        if (securityContext.getCurrentUser()
                .map(u -> u.getId() == userId)
                .orElse(false)) {
            return OperationResult.failure("Cannot delete your own account");
        }

        try {
            boolean deleted = userDAO.deleteUser(userId);
            return deleted
                    ? OperationResult.success("User deleted successfully")
                    : OperationResult.failure("User not found or is admin");
        } catch (SQLException e) {
            return OperationResult.failure("Delete failed: " + e.getMessage());
        }
    }

    public OperationResult deleteGameRecord(int sessionId) {
        if (!securityContext.hasPermission(Permission.DELETE_GAME_RECORDS)) {
            return OperationResult.failure("Access denied: Manager permission required");
        }

        try {
            boolean deleted = sessionDAO.deleteGameRecord(sessionId);
            return deleted
                    ? OperationResult.success("Game record deleted successfully")
                    : OperationResult.failure("Record not found");
        } catch (SQLException e) {
            return OperationResult.failure("Delete failed: " + e.getMessage());
        }
    }

    public OperationResult updateUserRole(int userId, Role newRole) {
        if (!securityContext.hasPermission(Permission.MANAGE_ROLES)) {
            return OperationResult.failure("Access denied: Admin permission required");
        }

        try {
            boolean updated = userDAO.updateUserRole(userId, newRole);
            return updated
                    ? OperationResult.success("Role updated successfully")
                    : OperationResult.failure("User not found");
        } catch (SQLException e) {
            return OperationResult.failure("Update failed: " + e.getMessage());
        }
    }

    public List<LeaderboardEntry> getAllGameRecords() {
        return executeSecured(
                Permission.DELETE_GAME_RECORDS,
                () -> {
                    try {
                        return sessionDAO.getLeaderboard(1000);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return Collections.emptyList();
                    }
                });
    }

    private <T> T executeSecured(Permission permission, Supplier<T> operation) {
        if (!securityContext.hasPermission(permission)) {
            throw new SecurityException("Access denied: requires " + permission);
        }
        return operation.get();
    }

    public boolean canAccessAdminPanel() {
        return securityContext.hasPermission(Permission.VIEW_ADMIN_PANEL);
    }

    public static class OperationResult {
        private final boolean success;
        private final String message;

        private OperationResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public static OperationResult success(String message) {
            return new OperationResult(true, message);
        }

        public static OperationResult failure(String message) {
            return new OperationResult(false, message);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }
}
