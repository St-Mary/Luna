package me.aikoo.stmary.core.constants;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import me.aikoo.stmary.core.utils.JSONFileReaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The BotConfigConstant class contains all the constants of the bot. */
public class BotConfigConstant {

  private static final Properties prop = new Properties();
  private static final Logger LOGGER = LoggerFactory.getLogger(BotConfigConstant.class);

  /** Load the properties file. */
  static {
    try (InputStream input = getInputStream()) {
        prop.load(input);

      List<String> keys =
          List.of(
              "token",
              "devToken",
              "devGuildId",
              "ownerId",
              "mode",
              "databaseHost",
              "databasePort",
              "databaseUsername",
              "databasePassword",
              "databaseDevDB",
              "databaseProdDB");

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
   *
   * @return The token of the bot.
   */
  public static String getToken() {
    return prop.getProperty("token");
  }

  /**
   * Get the dev token of the bot.
   *
   * @return The dev token of the bot.
   */
  public static String getDevToken() {
    return prop.getProperty("devToken");
  }

  /**
   * Get the dev guild id of the bot.
   *
   * @return The dev guild id of the bot.
   */
  public static String getDevGuildId() {
    return prop.getProperty("devGuildId");
  }

  /**
   * Get the owner id of the bot.
   *
   * @return The owner id of the bot.
   */
  public static String getOwnerId() {
    return prop.getProperty("ownerId");
  }

  /**
   * Get the debug channel id of the bot.
   *
   * @return The debug channel id of the bot.
   */
  public static String getDebugChannelId() {
    return prop.getProperty("debugChannelId");
  }

  /**
   * Get the mode of the bot.
   *
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
   *
   * @return The database host of the bot.
   */
  public static String getDatabaseHost() {
    return prop.getProperty("databaseHost");
  }

  /**
   * Get the database port of the bot.
   *
   * @return The database port of the bot.
   */
  public static String getDatabasePort() {
    return prop.getProperty("databasePort");
  }

  /**
   * Get the database username of the bot.
   *
   * @return The database username of the bot.
   */
  public static String getDatabaseUsername() {
    return prop.getProperty("databaseUsername");
  }

  /**
   * Get the database password of the bot.
   *
   * @return The database password of the bot.
   */
  public static String getDatabasePassword() {
    return prop.getProperty("databasePassword");
  }

  /**
   * Get the database name of the bot.
   *
   * @return The database name of the bot.
   */
  public static String getDatabaseName() {
    return (getMode().equals("dev"))
        ? prop.getProperty("databaseDevDB")
        : prop.getProperty("databaseProdDB");
  }

  /**
   * Get the emote of the specified emote name.
   *
   * @param emoteName The name of the emote.
   * @return The emote of the specified emote name.
   */
  public static String getEmote(String emoteName) {
    emoteName = emoteName.substring(0, 1).toUpperCase() + emoteName.substring(1);
    return prop.getProperty("emote" + emoteName);
  }

  private static InputStream getInputStream() {
    JarInputStream jarInputStream = null;

    if (checkIfItsJar()) {
      try {
        jarInputStream =
                new JarInputStream(
                        new FileInputStream(
                                new File(
                                        JSONFileReaderUtils.class
                                                .getProtectionDomain()
                                                .getCodeSource()
                                                .getLocation()
                                                .toURI())
                                        .getPath()));
        // Read content
        while (true) {
          JarEntry jarEntry = jarInputStream.getNextJarEntry();
          if (jarEntry == null) break;
          String fileName = jarEntry.getName();
          if (fileName.endsWith("config/config.properties")) {
              String fileText = new String(jarInputStream.readAllBytes(), StandardCharsets.UTF_8);
              return new ByteArrayInputStream(fileText.getBytes());
          }
        }
      } catch (IOException ignore) {
        // Ignore this exception and just return false
      } catch (URISyntaxException e) {
        LOGGER.error("Error while reading config file", e);
      } finally {
        try {
          if (jarInputStream != null) jarInputStream.close();
        } catch (IOException ignored) {
          // Ignore this exception and just return result
        }
      }
    } else {
      return BotConfigConstant.class.getResourceAsStream("/config/config.properties");
    }

    return null;
  }

  private static boolean checkIfItsJar() {
    return BotConfigConstant.class
            .getResource("BotConfigConstant.class")
            .toString()
            .startsWith("jar");
  }
}
