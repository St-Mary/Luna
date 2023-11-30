package com.stmarygate.discord.core.constants;

import io.github.cdimascio.dotenv.Dotenv;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The BotConfigConstant class contains all the constants of the bot. */
public class BotConfigConstant {

  private static final Dotenv dotenv =
      Dotenv.configure().ignoreIfMalformed().ignoreIfMissing().load();
  private static final Logger LOGGER = LoggerFactory.getLogger(BotConfigConstant.class);

  /**
   * Get the current version of the bot.
   *
   * @return The current version of the bot.
   */
  public static String getVersion() {
    return "v0.0.1";
  }

  /**
   * Get the token of the bot.
   *
   * @return The token of the bot.
   */
  public static String getToken() {
    return dotenv.get("token");
  }

  /**
   * Get the dev token of the bot.
   *
   * @return The dev token of the bot.
   */
  public static String getDevToken() {
    return dotenv.get("devToken");
  }

  /**
   * Get the dev guild id of the bot.
   *
   * @return The dev guild id of the bot.
   */
  public static String getDevGuildId() {
    return dotenv.get("devGuildId");
  }

  /**
   * Get the owner id of the bot.
   *
   * @return The owner id of the bot.
   */
  public static String getOwnerId() {
    return dotenv.get("ownerId");
  }

  /**
   * Get the debug channel id of the bot.
   *
   * @return The debug channel id of the bot.
   */
  public static String getDebugChannelId() {
    return dotenv.get("debugChannelId");
  }

  /**
   * Get the mode of the bot.
   *
   * @return The mode of the bot.
   */
  public static String getMode() {
    if (!List.of("dev", "prod").contains(dotenv.get("mode"))) {
      LOGGER.error("Invalid mode specified in properties file. Valid modes are 'dev' and 'prod'.");
      System.exit(1);
    }
    return dotenv.get("mode");
  }

  /**
   * Get the database host of the bot.
   *
   * @return The database host of the bot.
   */
  public static String getDatabaseHost() {
    return dotenv.get("databaseHost");
  }

  /**
   * Get the database port of the bot.
   *
   * @return The database port of the bot.
   */
  public static String getDatabasePort() {
    return dotenv.get("databasePort");
  }

  /**
   * Get the database username of the bot.
   *
   * @return The database username of the bot.
   */
  public static String getDatabaseUsername() {
    return dotenv.get("databaseUsername");
  }

  /**
   * Get the database password of the bot.
   *
   * @return The database password of the bot.
   */
  public static String getDatabasePassword() {
    return dotenv.get("databasePassword");
  }

  /**
   * Get the database name of the bot.
   *
   * @return The database name of the bot.
   */
  public static String getDatabaseName() {
    return (getMode().equals("dev")) ? dotenv.get("databaseDevDB") : dotenv.get("databaseProdDB");
  }

  /**
   * Get the emote of the specified emote name.
   *
   * @param emoteName The name of the emote.
   * @return The emote of the specified emote name.
   */
  public static String getEmote(String emoteName) {
    emoteName = emoteName.substring(0, 1).toUpperCase() + emoteName.substring(1);
    return dotenv.get("emote" + emoteName);
  }
}
