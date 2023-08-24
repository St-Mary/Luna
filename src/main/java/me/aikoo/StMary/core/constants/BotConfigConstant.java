package me.aikoo.StMary.core.constants;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BotConfigConstant {
    private static final Dotenv dotenv = Dotenv.load();
    private static final Logger LOGGER = LoggerFactory.getLogger(BotConfigConstant.class);

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

    public static String getDatabaseHost() {
        return dotenv.get("DATABASE_HOST");
    }

    public static String getDatabasePort() {
        return dotenv.get("DATABASE_PORT");
    }

    public static String getDatabaseUsername() {
        return dotenv.get("DATABASE_USERNAME");
    }

    public static String getDatabasePassword() {
        return dotenv.get("DATABASE_PASSWORD");
    }

    public static String getDatabaseName() {
        return (getMode().equals("dev")) ? dotenv.get("DATABASE_DEV_DB") : dotenv.get("DATABASE_PROD_DB");
    }
}
