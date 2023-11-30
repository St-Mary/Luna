package com.stmarygate.discord.core.bot;

import com.stmarygate.discord.core.cache.Cache;
import com.stmarygate.discord.core.constants.BotConfigConstant;
import com.stmarygate.discord.core.managers.*;
import com.stmarygate.discord.core.managers.ButtonManager;
import com.stmarygate.discord.core.managers.DatabaseManager;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** StMaryClient is the main class of the bot. It contains all the managers and the JDA instance. */
public class StMaryClient {

  private final Logger LOGGER = LoggerFactory.getLogger(StMaryClient.class);

  /** Get the cache of the bot. */
  @Getter private final Cache<String, String> cache = new Cache<>(10000);

  /** Get the JDA instance of the bot. */
  @Getter private JDA jda;

  /** StMaryClient constructor. It initialize all managers and start the bot. */
  public StMaryClient() {
    CharacterManager.load();
    LocationManager.load();
    ObjectManager.load();
    TitleManager.load();
    TextManager.load();

    CommandManager.loadCommands(this);

    // Initialize the database manager
    DatabaseManager.getSessionFactory();

    // Start the bot
    startStMaryClient();
  }

  /** Start StMaryClient by creating a JDA instance. */
  private void startStMaryClient() {
    LOGGER.info("Starting StMaryClient...");
    String token =
        (BotConfigConstant.getMode().equals("dev"))
            ? BotConfigConstant.getDevToken()
            : BotConfigConstant.getToken();

    jda =
        JDABuilder.createDefault(token)
            .addEventListeners(new EventsListener(this))
            .addEventListeners(new ButtonManager())
            .build();
  }
}
