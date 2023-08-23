package me.aikoo.StMary.core;

import me.aikoo.StMary.BotConfig;
import me.aikoo.StMary.core.abstracts.AbstractCommand;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * This class handles various Discord events and interactions.
 */
public class EventsListener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventsListener.class);
    private final StMaryClient stMaryClient;

    /**
     * Constructor to initialize the EventsListener with the StMaryClient.
     *
     * @param client The StMaryClient instance.
     */
    public EventsListener(StMaryClient client) {
        this.stMaryClient = client;
    }

    /**
     * Called when the bot is ready.
     * @param event The ReadyEvent.
     */
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        LOGGER.info("StMary is logged as {}", event.getJDA().getSelfUser().getName());

        if (BotConfig.getMode().equals("dev")) {
            // Merge commands and admin commands
            ArrayList<AbstractCommand> commands = new ArrayList<>(this.stMaryClient.getCommandManager().getCommands().values());
            commands.addAll(this.stMaryClient.getCommandManager().getAdminCommands().values());

            this.stMaryClient.getJda().getGuildById(BotConfig.getDevGuildId()).updateCommands()
                    .addCommands(commands.stream().map(AbstractCommand::buildCommandData).toArray(CommandData[]::new))
                    .queue(cmds -> {
                        LOGGER.info("Registered {} development commands !", cmds.size());
                    });
        } else {
            this.stMaryClient.getJda().updateCommands()
                    .addCommands(this.stMaryClient.getCommandManager().getCommands().values().stream().map(AbstractCommand::buildCommandData).toArray(CommandData[]::new))
                    .queue(cmds -> {
                        LOGGER.info("Registered {} commands !", cmds.size());
                    });

            this.stMaryClient.getJda().getGuildById(BotConfig.getDevGuildId()).updateCommands()
                    .addCommands(this.stMaryClient.getCommandManager().getAdminCommands().values().stream().map(AbstractCommand::buildCommandData).toArray(CommandData[]::new))
                    .queue(cmds -> {
                        LOGGER.info("Registered {} admin commands !", cmds.size());
                    });
        }
    }

    /**
     * Called when a slash command is executed.
     * @param event The SlashCommandInteractionEvent.
     */
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        AbstractCommand command = this.stMaryClient.getCommandManager().getCommand(event.getName());
        command = (command == null) ? this.stMaryClient.getCommandManager().getAdminCommand(event.getName()) : command;
        if (command == null) return;
        command.run(event);
    }

    /**
     * Called when a slash command auto-complete request is received.
     * @param event The CommandAutoCompleteInteractionEvent.
     */
    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        AbstractCommand command = this.stMaryClient.getCommandManager().getCommand(event.getInteraction().getName());
        if (command == null) return;
        command.autoComplete(event);
    }
}
