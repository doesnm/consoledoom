package com.consoledoom.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton Database
 */
public final class Database {

    private static Database instance;

    private final String url = DbConfig.url();
    private final String user = DbConfig.user();
    private final String pass = DbConfig.pass();

    private Database() {}

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }
}
