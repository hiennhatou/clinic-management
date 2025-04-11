package com.ou.pojos;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBUtils {
    private static final Properties dbProp = new Properties();
    
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            dbProp.load(ClassLoader.getSystemResourceAsStream("db.properties"));
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "Can't find classname for JDBC Driver", ex);
        } catch (IOException ex) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "Error read file", ex);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection((String) dbProp.getOrDefault("JDBC_URL", ""), (String) dbProp.getOrDefault("JDBC_USERNAME", ""), (String) dbProp.getOrDefault("JDBC_PASSWORD", ""));
    }
}
