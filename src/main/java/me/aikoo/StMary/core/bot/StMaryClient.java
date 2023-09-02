package me.aikoo.StMary.core.bot;

import lombok.Getter;
import me.aikoo.StMary.core.constants.BotConfigConstant;
import me.aikoo.StMary.core.managers.*;
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

    // Principal managers of the bot
    @Getter
    private final CooldownManager cooldownManager = new CooldownManager();
    @Getter
    private final CommandManager commandManager;
    @Getter
    private final LocationManager locationManager = new LocationManager(this);
    @Getter
    private final TextManager textManager = new TextManager(this);
    @Getter
    private final DatabaseManager databaseManager = new DatabaseManager();
    @Getter
    private final TitleManager titleManager = new TitleManager();
    @Getter
    private final ObjectManager objectManager = new ObjectManager();
    @Getter
    private final CharacterManager characterManager = new CharacterManager();
    @Getter
    private final ButtonManager buttonManager;

    // JDA instance to interact with Discord
    @Getter
    private JDA jda;

    /**
     * StMaryClient constructor. It initialize all managers and start the bot.
     */
    public StMaryClient() {
        // Initialize the command manager
        this.commandManager = new CommandManager();
        this.commandManager.loadCommands(this);

        // Initialize the button manager
        this.buttonManager = new ButtonManager(this);

        // Initialize the database manager
        this.databaseManager.getSessionFactory();

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
                .addEventListeners(buttonManager)
                .build();
    }
}

