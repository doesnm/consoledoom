// src/main/java/com/consoledoom/db/Database.java
package com.consoledoom.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String URL = DbConfig.url();
    private static final String USER = DbConfig.user();
    private static final String PASS = DbConfig.pass();

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
