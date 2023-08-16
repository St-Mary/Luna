package me.aikoo.StMary.core;

import lombok.Getter;
import me.aikoo.StMary.BotConfig;
import me.aikoo.StMary.core.managers.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StMaryClient {
    private final Logger LOGGER = LoggerFactory.getLogger(StMaryClient.class);

    @Getter
    private final CooldownManager cooldownManager = new CooldownManager();

    @Getter
    private JDA jda;

    @Getter
    private final CommandManager commandManager;

    @Getter
    private final LocationManager locationManager = new LocationManager();

    @Getter
    private final TextManager textManager = new TextManager(this);

    @Getter
    private final DatabaseManager databaseManager = new DatabaseManager();

    @Getter
    private final TitleManager titleManager = new TitleManager();

    @Getter
    private final ButtonManager buttonManager;

    public StMaryClient() {
        this.commandManager = new CommandManager();
        this.commandManager.loadCommands(this);

        this.buttonManager = new ButtonManager(this);

        this.databaseManager.getSessionFactory();
        startStMaryClient();
    }

    private void startStMaryClient() {
        LOGGER.info("Starting StMaryClient...");
        String token = (BotConfig.getMode().equals("dev")) ? BotConfig.getDevToken() : BotConfig.getToken();

        jda = JDABuilder.createDefault(token)
                .addEventListeners(new EventsListener(this))
                .addEventListeners(buttonManager)
                .build();
    }
}
