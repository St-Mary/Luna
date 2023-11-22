package me.aikoo.stmary.discord.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import me.aikoo.stmary.discord.core.abstracts.CommandAbstract;
import me.aikoo.stmary.discord.core.bases.TitleBase;
import me.aikoo.stmary.discord.core.bot.StMaryClient;
import me.aikoo.stmary.discord.core.database.PlayerEntity;
import me.aikoo.stmary.discord.core.managers.DatabaseManager;
import me.aikoo.stmary.discord.core.managers.TextManager;
import me.aikoo.stmary.discord.core.managers.TitleManager;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/** A command to select and display a title for the user. */
public class SelectTitleCommand extends CommandAbstract {

  /**
   * Constructs a SelectTitle command.
   *
   * @param stMaryClient The StMaryClient instance.
   */
  public SelectTitleCommand(StMaryClient stMaryClient) {
    super(stMaryClient);

    this.name = "selecttitle";
    this.description = "\uD83C\uDF96\uFE0F Select your current title to show";
    this.cooldown = 15000L;

    // Define the command options
    this.options.add(
        new OptionData(OptionType.STRING, "title", "The title to show")
            .setAutoComplete(true)
            .setRequired(true));
  }

  /**
   * Executes the SelectTitle command.
   *
   * @param event The SlashCommandInteractionEvent triggered by the command.
   */
  @Override
  public void execute(SlashCommandInteractionEvent event, String language) {
    String titleId = Objects.requireNonNull(event.getOption("title")).getAsString();

    // Check if the selected title exists
    if (TitleManager.getTitle(titleId) == null) {
      String errorText =
          TextManager.createText("select_title_error_title_not_exist", language).buildError();
      event.reply(errorText).setEphemeral(true).queue();
      return;
    }

    PlayerEntity player = DatabaseManager.getPlayer(event.getUser().getIdLong());

    TextManager.Text text = TextManager.createText("select_title_success", language);
    text.replace("title", TitleManager.getTitle(titleId).format(language));

    // Perform verifications before update the current title
    if (!verifications(player, titleId, event, language)) {
      return;
    }

    player.setCurrentTitle(titleId);
    DatabaseManager.update(player);
    event.reply(text.build()).queue();
  }

  /**
   * Perform verifications before updating the current title.
   *
   * @param player The player entity.
   * @param titleId The name of the selected title.
   * @param event The SlashCommandInteractionEvent.
   * @return True if verifications pass, false otherwise.
   */
  public boolean verifications(
      PlayerEntity player, String titleId, SlashCommandInteractionEvent event, String language) {
    if (player == null) {
      return false;
    }
    Map<String, TitleBase> titles = player.getTitles(stMaryClient);

    // Check if the player owns the selected title
    if (!titles.containsKey(titleId)) {
      String errorText =
          TextManager.createText("select_title_error_title_not_posseded", language).buildError();
      event.reply(errorText).setEphemeral(true).queue();
      return false;
    }

    // Check if the selected title is already the current title
    if (player.getCurrentTitle(stMaryClient).getId().equals(titleId)) {
      String errorText =
          TextManager.createText("select_title_error_title_already_active", language).buildError();
      event.reply(errorText).setEphemeral(true).queue();
      return false;
    }

    return true;
  }

  /**
   * Provides auto-complete choices for the title selection.
   *
   * @param event The CommandAutoCompleteInteractionEvent triggered by the auto-complete request.
   */
  @Override
  public void autoComplete(CommandAutoCompleteInteractionEvent event, String language) {
    PlayerEntity player = DatabaseManager.getPlayer(event.getUser().getIdLong());

    if (player != null) {
      Collection<TitleBase> titles = player.getTitles(stMaryClient).values();
      ArrayList<Command.Choice> choices = new ArrayList<>();

      // Add title choices
      for (TitleBase title : titles) {
        choices.add(new Command.Choice(title.format(language), title.getId()));

        if (choices.size() == 24) {
          choices.add(new Command.Choice("Autre", "Autre"));
          break;
        }
      }

      event.replyChoices(choices).queue();
    }
  }
}
