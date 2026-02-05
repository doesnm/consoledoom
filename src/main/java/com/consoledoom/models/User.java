// src/main/java/com/consoledoom/models/User.java
package com.consoledoom.models;

import com.consoledoom.security.Role;
import java.sql.Timestamp;

/**
 * User entity with authentication and authorization data.
 */
public class User {
    private final int id;
    private final String nickname;
    private final String passwordHash;
    private final Role role;
    private final Timestamp createdAt;
    private final Timestamp lastLogin;
    private final boolean active;

    public User(int id, String nickname, String passwordHash, Role role,
            Timestamp createdAt, Timestamp lastLogin, boolean active) {
        this.id = id;
        this.nickname = nickname;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
        this.active = active;
    }

    // Builder pattern for User
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int id;
        private String nickname;
        private String passwordHash;
        private Role role = Role.PLAYER;
        private Timestamp createdAt;
        private Timestamp lastLogin;
        private boolean active = true;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public Builder passwordHash(String hash) {
            this.passwordHash = hash;
            return this;
        }

        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public Builder createdAt(Timestamp ts) {
            this.createdAt = ts;
            return this;
        }

        public Builder lastLogin(Timestamp ts) {
            this.lastLogin = ts;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public User build() {
            return new User(id, nickname, passwordHash, role, createdAt, lastLogin, active);
        }
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public boolean isActive() {
        return active;
    }
}
