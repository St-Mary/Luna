package me.aikoo;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class BotConfig {
    private static final Dotenv dotenv = Dotenv.load();
    private static final Logger LOGGER = LoggerFactory.getLogger(BotConfig.class);

    public static String getToken() {
        return dotenv.get("TOKEN");
    }

    public static String getDevToken() {
        return dotenv.get("DEV_TOKEN");
    }

    public static String getDevGuildId() {
        return dotenv.get("DEV_GUILD_ID");
    }

    public static String getOwnerId() {
        return dotenv.get("OWNER_ID");
    }

    public static String getMode() {
        if (!List.of(new String[]{"dev", "prod"}).contains(dotenv.get("MODE"))) {
            LOGGER.error("Invalid mode specified in .env file. Valid modes are 'dev' and 'prod'.");
            System.exit(1);
        }
        return dotenv.get("MODE");
    }
}
