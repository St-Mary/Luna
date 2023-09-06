package me.aikoo.stmary.core.constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * The BotConfigConstant class contains all the constants of the bot.
 */
public class BotConfigConstant {

    private static final Properties prop = new Properties();
    private static final Logger LOGGER = LoggerFactory.getLogger(BotConfigConstant.class);

    /**
     * Load the properties file.
     */
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

    /**
     * Get the token of the bot.
     * @return The token of the bot.
     */
    public static String getToken() {
        return prop.getProperty("token");
    }

    /**
     * Get the dev token of the bot.
     * @return The dev token of the bot.
     */
    public static String getDevToken() {
        return prop.getProperty("devToken");
    }

    /**
     * Get the dev guild id of the bot.
     * @return The dev guild id of the bot.
     */
    public static String getDevGuildId() {
        return prop.getProperty("devGuildId");
    }

    /**
     * Get the owner id of the bot.
     * @return The owner id of the bot.
     */
    public static String getOwnerId() {
        return prop.getProperty("ownerId");
    }

    /**
     * Get the mode of the bot.
     * @return The mode of the bot.
     */
    public static String getMode() {
        if (!List.of("dev", "prod").contains(prop.getProperty("mode"))) {
            LOGGER.error("Invalid mode specified in properties file. Valid modes are 'dev' and 'prod'.");
            System.exit(1);
        }
        return prop.getProperty("mode");
    }

    /**
     * Get the database host of the bot.
     * @return The database host of the bot.
     */
    public static String getDatabaseHost() {
        return prop.getProperty("databaseHost");
    }

    /**
     * Get the database port of the bot.
     * @return The database port of the bot.
     */
    public static String getDatabasePort() {
        return prop.getProperty("databasePort");
    }

    /**
     * Get the database username of the bot.
     * @return The database username of the bot.
     */
    public static String getDatabaseUsername() {
        return prop.getProperty("databaseUsername");
    }

    /**
     * Get the database password of the bot.
     * @return The database password of the bot.
     */
    public static String getDatabasePassword() {
        return prop.getProperty("databasePassword");
    }

    /**
     * Get the database name of the bot.
     * @return The database name of the bot.
     */
    public static String getDatabaseName() {
        return (getMode().equals("dev")) ? prop.getProperty("databaseDevDB") : prop.getProperty("databaseProdDB");
    }

    /**
     * Get the emote of the specified emote name.
     * @param emoteName The name of the emote.
     * @return The emote of the specified emote name.
     */
    public static String getEmote(String emoteName) {
        emoteName = emoteName.substring(0, 1).toUpperCase() + emoteName.substring(1);
        return prop.getProperty("emote" + emoteName);
    }
}
