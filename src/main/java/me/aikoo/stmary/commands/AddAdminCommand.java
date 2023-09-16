package me.aikoo.stmary.commands;

import java.util.Objects;
import me.aikoo.stmary.core.abstracts.CommandAbstract;
import me.aikoo.stmary.core.bot.StMaryClient;
import me.aikoo.stmary.core.constants.BotConfigConstant;
import me.aikoo.stmary.core.database.AdministratorEntity;
import me.aikoo.stmary.core.managers.DatabaseManager;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/** AddAdminCommand is the command used to add an administrator. */
public class AddAdminCommand extends CommandAbstract {

  /**
   * Constructor for the command.
   *
   * @param stMaryClient The StMaryClient instance.
   */
  public AddAdminCommand(StMaryClient stMaryClient) {
    super(stMaryClient);

    this.name = "addadmin";
    this.description = "Add an admin";
    this.cooldown = 10000L;
    this.setAdminCommand(true);

    this.options.add(
        new OptionData(OptionType.USER, "user", "The new administrator").setRequired(true));
  }

  /**
   * Execute method for the command.
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

    AdministratorEntity administrators =
        DatabaseManager.getAdministrator(
            Objects.requireNonNull(event.getOption("user")).getAsUser().getIdLong());
    if (administrators != null) {
      String errMsg = "Cet utilisateur est déjà administrateur !";
      event.getHook().sendMessage(errMsg).queue();
      return;
    }

    AdministratorEntity newAdmin = new AdministratorEntity();
    newAdmin.setDiscordId(Objects.requireNonNull(event.getOption("user")).getAsUser().getIdLong());

    DatabaseManager.save(newAdmin);

    event.getHook().sendMessage("L'utilisateur a été ajouté en tant qu'administrateur !").queue();
  }

  /**
   * Autocomplete method for the command.
   *
   * @param event The CommandAutoCompleteInteractionEvent triggered when the button is clicked.
   * @param language The language of the player
   */
  @Override
  public void autoComplete(CommandAutoCompleteInteractionEvent event, String language) {
    // Unused method for this command
  }
}
