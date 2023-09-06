package me.aikoo.StMary.core.bot;

import lombok.Getter;
import me.aikoo.StMary.core.cache.Cache;
import me.aikoo.StMary.core.constants.BotConfigConstant;
import me.aikoo.StMary.core.managers.ButtonManager;
import me.aikoo.StMary.core.managers.CommandManager;
import me.aikoo.StMary.core.managers.DatabaseManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * StMaryClient is the main class of the bot. It contains all the managers and the JDA instance.
 */
public class StMaryClient {
    // Logger
    private final Logger LOGGER = LoggerFactory.getLogger(StMaryClient.class);
    @Getter
    private final Cache<String, String> cache = new Cache<>(20000);

    // JDA instance to interact with Discord
    @Getter
    private JDA jda;

    /**
     * StMaryClient constructor. It initialize all managers and start the bot.
     */
    public StMaryClient() {
        CommandManager.loadCommands(this);

        // Initialize the database manager
        DatabaseManager.getSessionFactory();

        // Start the bot
        startStMaryClient();
    }

    /**
     * Start StMaryClient by creating a JDA instance.
     */
    private void startStMaryClient() {
        LOGGER.info("Starting StMaryClient...");
        String token = (BotConfigConstant.getMode().equals("dev")) ? BotConfigConstant.getDevToken() : BotConfigConstant.getToken();

        jda = JDABuilder.createDefault(token)
                .addEventListeners(new EventsListener(this))
                .addEventListeners(new ButtonManager())
                .build();
    }
}

