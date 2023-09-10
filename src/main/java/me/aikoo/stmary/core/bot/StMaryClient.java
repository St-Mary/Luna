package me.aikoo.stmary.core.bot;

import lombok.Getter;
import me.aikoo.stmary.core.cache.Cache;
import me.aikoo.stmary.core.constants.BotConfigConstant;
import me.aikoo.stmary.core.managers.ButtonManager;
import me.aikoo.stmary.core.managers.CommandManager;
import me.aikoo.stmary.core.managers.DatabaseManager;
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
