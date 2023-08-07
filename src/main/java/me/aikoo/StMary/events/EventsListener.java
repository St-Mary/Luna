package me.aikoo.StMary.events;

import me.aikoo.StMary.BotConfig;
import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.command.AbstractCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class EventsListener extends ListenerAdapter {

    private final StMaryClient stMaryClient;
    private static final Logger LOGGER = LoggerFactory.getLogger(EventsListener.class);

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
        if(command == null) return;
        command.run(event);
    }
}
