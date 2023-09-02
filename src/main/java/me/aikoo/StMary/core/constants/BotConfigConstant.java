package me.aikoo.StMary.core.constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Properties;

public class BotConfigConstant {

    private static final Properties prop = new Properties();
    private static final Logger LOGGER = LoggerFactory.getLogger(BotConfigConstant.class);

    static {
        try (InputStream input = new FileInputStream("config/config.properties")) {
            prop.load(input);

            List<String> keys = List.of("token", "devToken", "devGuildId", "ownerId", "mode", "databaseHost", "databasePort", "databaseUsername", "databasePassword", "databaseDevDB", "databaseProdDB");

            for (String key : keys) {
                if (prop.getProperty(key) == null) {
                    LOGGER.error("Missing key in properties file: " + key);
                    System.exit(1);
                }
            }
        } catch (IOException io) {
            io.printStackTrace();
            System.exit(1);
        }
    }

    public static String getToken() {
        return prop.getProperty("token");
    }

    public static String getDevToken() {
        return prop.getProperty("devToken");
    }

    public static String getDevGuildId() {
        return prop.getProperty("devGuildId");
    }

    public static String getOwnerId() {
        return prop.getProperty("ownerId");
    }

    public static String getMode() {
        if (!List.of("dev", "prod").contains(prop.getProperty("mode"))) {
            LOGGER.error("Invalid mode specified in properties file. Valid modes are 'dev' and 'prod'.");
            System.exit(1);
        }
        return prop.getProperty("mode");
    }

    public static String getDatabaseHost() {
        return prop.getProperty("databaseHost");
    }

    public static String getDatabasePort() {
        return prop.getProperty("databasePort");
    }

    public static String getDatabaseUsername() {
        return prop.getProperty("databaseUsername");
    }

    public static String getDatabasePassword() {
        return prop.getProperty("databasePassword");
    }

    public static String getDatabaseName() {
        return (getMode().equals("dev")) ? prop.getProperty("databaseDevDB") : prop.getProperty("databaseProdDB");
    }
}
