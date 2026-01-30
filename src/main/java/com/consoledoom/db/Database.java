package com.consoledoom.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String URL = DbConfig.url();
    private static final String USER = DbConfig.user();
    private static final String PASS = DbConfig.pass();

    public static Connection getConnection() throws SQLException {
        Connection c = DriverManager.getConnection(URL, USER, PASS);

        try (var st = c.createStatement();
                var rs = st.executeQuery("select current_database(), inet_server_addr(), inet_server_port()")) {
            if (rs.next()) {
                System.out.println("DB OK: " + rs.getString(1) + " " + rs.getString(2) + ":" + rs.getInt(3));
            }
        } catch (Exception ignored) {
        }

        return c;
    }
}
