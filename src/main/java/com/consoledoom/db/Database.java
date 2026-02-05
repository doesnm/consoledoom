// src/main/java/com/consoledoom/db/Database.java
package com.consoledoom.db;

import com.consoledoom.config.GameConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection factory using Singleton config.
 */
public class Database {
    private static final GameConfig config = GameConfig.INSTANCE;

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                config.getDbUrl(),
                config.getDbUser(),
                config.getDbPassword());
    }
}
