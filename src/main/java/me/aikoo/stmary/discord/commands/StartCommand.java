package me.aikoo.stmary.discord.commands;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import me.aikoo.stmary.discord.core.abstracts.ButtonListener;
import me.aikoo.stmary.discord.core.abstracts.CommandAbstract;
import me.aikoo.stmary.discord.core.bases.CharacterBase;
import me.aikoo.stmary.discord.core.bot.StMaryClient;
import me.aikoo.stmary.discord.core.constants.PlayerConstant;
import me.aikoo.stmary.discord.core.database.PlayerEntity;
import me.aikoo.stmary.discord.core.managers.CharacterManager;
import me.aikoo.stmary.discord.core.managers.DatabaseManager;
import me.aikoo.stmary.discord.core.managers.TextManager;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

/** The Start command allows users to begin their adventure. */
public class StartCommand extends CommandAbstract {

  /**
   * Creates a new Start command.
   *
   * @param stMaryClient The StMaryClient instance.
   */
  public StartCommand(StMaryClient stMaryClient) {
    super(stMaryClient);

    this.name = "start";
    this.description = "\uD83D\uDE80 Démarrer l'aventure !";
    this.cooldown = 10000L;
    this.setMustBeRegistered(false);

    this.options.add(
        new OptionData(OptionType.STRING, "language", "The language to use")
            .addChoice("\uD83C\uDDEB\uD83C\uDDF7 Français", "fr")
            .addChoice("\uD83C\uDDEC\uD83C\uDDE7 English", "en")
            .setRequired(true));
  }

  /**
   * Executes the Start command.
   *
   * @param event The SlashCommandInteractionEvent.
   */
  @Override
  public void execute(SlashCommandInteractionEvent event, String language) {
    CharacterBase.Dialog dialog = CharacterManager.getCharacter("1").getDialog("1.1");
    language = event.getOption("language").getAsString();

    if (!isEligibleToStartAdventure(event, language)) {
      return;
    }

    ButtonListener buttonListener = generateButtonListener(event, language, dialog);
    stMaryClient.getJda().addEventListener(buttonListener);
    stMaryClient.getCache().put("actionWaiter_" + event.getUser().getId(), "start");

    String introduction = TextManager.createText("start_adventure_introduction", language).build();
    String text = introduction + "\n\n" + dialog.printDialog(language);

    buttonListener.sendButtonMenu(event, text);
  }

  /**
   * Generate the ButtonListener for the Start command.
   *
   * @param event The SlashCommandInteractionEvent.
   * @param language The language of the player.
   * @return The ButtonListener.
   */
  public ButtonListener generateButtonListener(SlashCommandInteractionEvent event, String language, CharacterBase.Dialog dialog) {
    CharacterBase character = dialog.getCharacter();
    ArrayList<Button> buttons = new ArrayList<>();

    for (CharacterBase.Choice choice : dialog.getChoices()) {
      buttons.add(choice.getButton(language));
    }

    return new ButtonListener(stMaryClient, event.getUser().getId(), language, buttons, 120000L, false) {
              @Override
              public void buttonClick(ButtonInteractionEvent event) {
                if (event.getComponentId().equals("yes_1.1")) {
                  onClickYesBtn(event, language);
                } else {
                  onClickNoBtn(event, language);
                  this.timer.cancel();
                }
                this.timer.cancel();
              }

              @Override
              protected void closeBtnMenu(ButtonInteractionEvent event, String text) {
                CharacterBase.Dialog dialog = character.getDialog("1.1.2");
                timer.cancel();
                stMaryClient.getJda().removeEventListener(this);
                stMaryClient.getCache().delete("actionWaiter_" + authorId);

                if (event == null) {
                  message.editMessage(dialog.printDialog(language)).setComponents().queue();
                } else {
                  event.editMessage(dialog.printDialog(language)).setComponents().queue();
                }
              }
            };
  }

  /**
   * Check if player is eligible to start the adventure.
   *
   * @param event The SlashCommandInteractionEvent.
   * @param language The language of the player.
   */
  private boolean isEligibleToStartAdventure(SlashCommandInteractionEvent event, String language) {
    LocalDate creationDate = event.getUser().getTimeCreated().toLocalDateTime().toLocalDate();
    PlayerEntity player = DatabaseManager.getPlayer(event.getUser().getIdLong());

    // Check if the Discord account was created more than a week ago
    if (creationDate.isAfter(LocalDate.now().minusWeeks(PlayerConstant.CREATION_TIME_WEEK_LIMIT))) {
      event
              .reply(
                      TextManager.createText("start_adventure_error_creation_date", language).buildError())
              .queue();
      return false;
    }

    if (player != null) {
      // Player already exists, send an error message
      String error =
              TextManager.createText("start_adventure_error_already_started", language).buildError();
      event.reply(error).setEphemeral(true).queue();
      return false;
    }

    return true;
  }

  /**
   * Close the button menu when the user clicks on the "No" button.
   *
   * @param event The ButtonInteractionEvent triggered when the button is clicked.
   * @param language The language of the player.
   */
  public void onClickNoBtn(ButtonInteractionEvent event, String language) {
    CharacterBase character = CharacterManager.getCharacter("1");
    CharacterBase.Dialog dialog = character.getDialog("1.1.2");

    // Remove the cache and buttons
    stMaryClient.getCache().delete("actionWaiter_" + event.getUser().getId());
    event.editMessage(dialog.printDialog(language)).setComponents().queue();
  }

  /**
   * Handle the click on the "Yes" button.
   *
   * @param event The ButtonInteractionEvent triggered when the button is clicked.
   * @param language The language of the player
   */
  public void onClickYesBtn(ButtonInteractionEvent event, String language) {
    CharacterBase character = CharacterManager.getCharacter("1");
    String dialog = character.getDialog("1.1.1").printDialog(language);

    // Create a new player entity
    PlayerEntity player = new PlayerEntity();
    player.setDiscordId(event.getUser().getIdLong());
    player.setLevel(PlayerConstant.LEVEL);
    player.setExperience(PlayerConstant.EXPERIENCE);
    player.setMoney(PlayerConstant.MONEY);
    player.setTitles(PlayerConstant.TITLES, stMaryClient);
    player.setCurrentLocationRegion(PlayerConstant.CURRENT_LOCATION_REGION);
    player.setCurrentLocationTown(PlayerConstant.CURRENT_LOCATION_TOWN);
    player.setCurrentLocationPlace(PlayerConstant.CURRENT_LOCATION_PLACE);
    player.setCurrentTitle(PlayerConstant.CURRENT_TITLE);
    player.setMagicalBook(PlayerConstant.MAGICAL_BOOK);
    player.setLanguage(language);
    player.setCreationTimestamp(new Date());

    // Save the new player entity to the database
    DatabaseManager.save(player);

    event.editMessage(dialog).setComponents().queue();
    stMaryClient.getCache().delete("actionWaiter_" + event.getUser().getId());
  }

  /**
   * Auto-complete method for the Start command.
   *
   * @param event The CommandAutoCompleteInteractionEvent.
   */
  @Override
  public void autoComplete(CommandAutoCompleteInteractionEvent event, String language) {
    // Unused method for this command
  }
}
