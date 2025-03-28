package com.ou;

import java.io.IOException;
import java.util.Properties;

public class Config {
    public static final Properties PROPERTIES = new Properties();
    public static final String SECRET_KEY;
    static {
        try {
            PROPERTIES.load(ClassLoader.getSystemResourceAsStream("config.properties"));
            SECRET_KEY = PROPERTIES.getProperty("SECURE_KEY", "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
