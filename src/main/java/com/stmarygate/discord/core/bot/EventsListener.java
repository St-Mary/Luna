package com.stmarygate.discord.core.bot;

import com.stmarygate.discord.core.abstracts.CommandAbstract;
import com.stmarygate.discord.core.constants.BotConfigConstant;
import com.stmarygate.discord.core.managers.CommandManager;
import java.util.ArrayList;
import java.util.Objects;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This class handles various Discord events and interactions. */
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
   *
   * @param event The ReadyEvent.
   */
  @Override
  public void onReady(@NotNull ReadyEvent event) {
    LOGGER.info("StMary is logged as {}", event.getJDA().getSelfUser().getName());

    if (BotConfigConstant.getMode().equals("dev")) {
      // Merge commands and admin commands
      ArrayList<CommandAbstract> commands = new ArrayList<>(CommandManager.getCommands().values());
      commands.addAll(CommandManager.getAdminCommands().values());

      Objects.requireNonNull(
              this.stMaryClient.getJda().getGuildById(BotConfigConstant.getDevGuildId()))
          .updateCommands()
          .addCommands(
              commands.stream().map(CommandAbstract::buildCommandData).toArray(CommandData[]::new))
          .queue(cmds -> LOGGER.info("Registered {} development commands !", cmds.size()));
    } else {
      this.stMaryClient
          .getJda()
          .updateCommands()
          .addCommands(
              CommandManager.getCommands().values().stream()
                  .map(CommandAbstract::buildCommandData)
                  .toArray(CommandData[]::new))
          .queue(
              cmds -> {
                LOGGER.info("Registered {} commands !", cmds.size());
              });

      Objects.requireNonNull(
              this.stMaryClient.getJda().getGuildById(BotConfigConstant.getDevGuildId()))
          .updateCommands()
          .addCommands(
              CommandManager.getAdminCommands().values().stream()
                  .map(CommandAbstract::buildCommandData)
                  .toArray(CommandData[]::new))
          .queue(cmds -> LOGGER.info("Registered {} admin commands !", cmds.size()));
    }
  }

  /**
   * Called when a slash command is executed.
   *
   * @param event The SlashCommandInteractionEvent.
   */
  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    CommandAbstract command = CommandManager.getCommand(event.getName());
    command = (command == null) ? CommandManager.getAdminCommand(event.getName()) : command;
    if (command == null) {
      return;
    }
    command.run(event);
  }

  /**
   * Called when a slash command auto-complete request is received.
   *
   * @param event The CommandAutoCompleteInteractionEvent.
   */
  @Override
  public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
    CommandAbstract command = CommandManager.getCommand(event.getInteraction().getName());
    if (command == null) {
      return;
    }
    command.runAutoComplete(event);
  }
}
