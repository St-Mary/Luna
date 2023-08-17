package me.aikoo.StMary.core;

import me.aikoo.StMary.BotConfig;
import me.aikoo.StMary.commands.AbstractCommand;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventsListener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventsListener.class);
    private final StMaryClient stMaryClient;

    public EventsListener(StMaryClient client) {
        this.stMaryClient = client;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        LOGGER.info("StMary is logged as {}", event.getJDA().getSelfUser().getName());

        if (BotConfig.getMode().equals("dev")) {
            this.stMaryClient.getJda().getGuildById(BotConfig.getDevGuildId()).updateCommands().addCommands(this.stMaryClient.getCommandManager().getCommands().values().stream().map(AbstractCommand::buildCommandData).toArray(CommandData[]::new)).queue(cmds -> {
                LOGGER.info("Registered {} development commands !", cmds.size());
            });
        } else {
            this.stMaryClient.getJda().updateCommands().addCommands(this.stMaryClient.getCommandManager().getCommands().values().stream().map(AbstractCommand::buildCommandData).toArray(CommandData[]::new)).queue(cmds -> {
                LOGGER.info("Registered {} commands !", cmds.size());
            });
        }

    }

    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        AbstractCommand command = this.stMaryClient.getCommandManager().getCommand(event.getName());
        if (command == null) return;
        command.run(stMaryClient, event);
    }

    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        AbstractCommand command = this.stMaryClient.getCommandManager().getCommand(event.getInteraction().getName());
        if (command == null) return;
        command.autoComplete(stMaryClient, event);
    }
}
