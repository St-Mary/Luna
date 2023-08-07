package me.aikoo.StMary.core;

import me.aikoo.StMary.BotConfig;
import me.aikoo.StMary.command.CommandManager;
import me.aikoo.StMary.events.EventsListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class StMaryClient {
    private final Logger LOGGER = LoggerFactory.getLogger(StMaryClient.class);
    private final CooldownManager cooldownManager = new CooldownManager();
    private JDA jda;
    private final CommandManager commandManager;

    public StMaryClient() {
        this.commandManager = new CommandManager();
        this.commandManager.loadCommands(this);

        startStMaryClient();
    }

    private void startStMaryClient() {
        LOGGER.info("Starting StMaryClient...");
        String token = (BotConfig.getMode().equals("dev")) ? BotConfig.getDevToken() : BotConfig.getToken();

        jda = JDABuilder.createDefault(token)
                .addEventListeners(new EventsListener(this))
                .build();
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public JDA getJda() {
        return jda;
    }
}
