package me.aikoo.stmary.commands;

import java.util.Objects;
import me.aikoo.stmary.core.abstracts.CommandAbstract;
import me.aikoo.stmary.core.bot.StMaryClient;
import me.aikoo.stmary.core.database.PlayerEntity;
import me.aikoo.stmary.core.managers.DatabaseManager;
import me.aikoo.stmary.core.managers.TitleManager;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/** AddTitleCommand is the command used to add a title to a user. */
public class AddTitleCommand extends CommandAbstract {

  /**
   * Constructor for the command.
   *
   * @param stMaryClient The StMaryClient instance.
   */
  public AddTitleCommand(StMaryClient stMaryClient) {
    super(stMaryClient);

    this.name = "addtitle";
    this.description = "Add a title to a user";
    this.cooldown = 10000L;
    this.setMustBeRegistered(false);
    this.setAdminCommand(true);

    this.options.add(new OptionData(OptionType.STRING, "title", "The title id").setRequired(true));
    this.options.add(
        new OptionData(OptionType.STRING, "userid", "The user to add title").setRequired(true));
  }

  /**
   * Execute method for the command.
   *
   * @param event The SlashCommandInteractionEvent triggered when the button is clicked.
   * @param language The language of the player
   */
  @Override
  public void execute(SlashCommandInteractionEvent event, String language) {
    if (!event.getUser().getId().equals("985986599995187270")) {
      return;
    }
    String titleId = Objects.requireNonNull(event.getOption("title")).getAsString();
    String userId = Objects.requireNonNull(event.getOption("userid")).getAsString();

    if (!userId.matches("[0-9]+")) {
      event.reply("This user doesn't exist").queue();
      return;
    }

    PlayerEntity player =
        DatabaseManager.getPlayer(Objects.requireNonNull(event.getOption("userid")).getAsLong());

    if (player == null) {
      event.reply("This user doesn't exist").queue();
      return;
    }

    if (TitleManager.getTitle(titleId) == null) {
      event.reply("This title doesn't exist").queue();
      return;
    }
    player.addTitle(titleId, stMaryClient);
    DatabaseManager.update(player);

    event.reply("The title has been added to the user").queue();
  }

  /**
   * Auto complete method for the command.
   *
   * @param event The CommandAutoCompleteInteractionEvent triggered when the button is clicked.
   * @param language The language of the player
   */
  @Override
  public void autoComplete(CommandAutoCompleteInteractionEvent event, String language) {
    // Unused method for this command
  }
}
