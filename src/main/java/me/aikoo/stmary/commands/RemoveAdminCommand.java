package me.aikoo.stmary.commands;

import me.aikoo.stmary.core.abstracts.CommandAbstract;
import me.aikoo.stmary.core.bot.StMaryClient;
import me.aikoo.stmary.core.constants.BotConfigConstant;
import me.aikoo.stmary.core.database.AdministratorEntity;
import me.aikoo.stmary.core.managers.DatabaseManager;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/** RemoveAdminCommand is the command used to remove an administrator. */
public class RemoveAdminCommand extends CommandAbstract {

  /**
   * Constructor of the removeadmin command
   *
   * @param stMaryClient The StMaryClient instance
   */
  public RemoveAdminCommand(StMaryClient stMaryClient) {
    super(stMaryClient);

    this.name = "removeadmin";
    this.description = "Remove an admin";
    this.cooldown = 10000L;
    this.setAdminCommand(true);

    this.options.add(
        new OptionData(OptionType.STRING, "userid", "The old administrator").setRequired(true));
  }

  /**
   * Remove an administrator.
   *
   * @param event The SlashCommandInteractionEvent triggered when the button is clicked.
   * @param language The language of the player
   */
  @Override
  public void execute(SlashCommandInteractionEvent event, String language) {
    if (!event.getUser().getId().equals(BotConfigConstant.getOwnerId())) {
      String errMsg = "Seul le propriétaire du bot peut exécuter cette commande !";
      event.getHook().sendMessage(errMsg).queue();
    }

    String userId = event.getOption("userid").getAsString();

    if (!userId.matches("[0-9]+")) {
      event.getHook().sendMessage("This user doesn't exist").queue();
      return;
    }

    AdministratorEntity administrator = DatabaseManager.getAdministrator(Long.parseLong(userId));
    if (administrator == null) {
      String errMsg = "Cet utilisateur n'est pas administrateur !";
      event.getHook().sendMessage(errMsg).queue();
      return;
    }

    DatabaseManager.delete(administrator);

    event
        .getHook()
        .sendMessage("L'utilisateur a été déstitué de son rôle d'administrateur !")
        .queue();
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
