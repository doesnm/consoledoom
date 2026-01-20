package com.consoledoom.db;

import java.io.InputStream;
import java.util.Properties;

public final class DbConfig {
    private static final Properties props = new Properties();

    static {
        try (InputStream is = DbConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (is == null) throw new RuntimeException("application.properties not found in src/main/resources");
            props.load(is);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load DB config", e);
        }
    }

    public static String url() { return props.getProperty("db.url"); }
    public static String user() { return props.getProperty("db.user"); }
    public static String pass() { return props.getProperty("db.password"); }

    private DbConfig() {}
}
