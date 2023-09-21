package me.aikoo.stmary.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import me.aikoo.stmary.core.abstracts.ButtonListener;
import me.aikoo.stmary.core.abstracts.CommandAbstract;
import me.aikoo.stmary.core.bases.ObjectBase;
import me.aikoo.stmary.core.bases.PlaceBase;
import me.aikoo.stmary.core.bases.TitleBase;
import me.aikoo.stmary.core.bot.StMaryClient;
import me.aikoo.stmary.core.constants.BotConfigConstant;
import me.aikoo.stmary.core.database.MoveEntity;
import me.aikoo.stmary.core.database.PlayerEntity;
import me.aikoo.stmary.core.managers.DatabaseManager;
import me.aikoo.stmary.core.managers.LocationManager;
import me.aikoo.stmary.core.managers.TextManager;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

/** A command to display a user's menu, including their profile, inventory, and titles. */
public class MenuCommand extends CommandAbstract {

  /**
   * Constructs a Menu command.
   *
   * @param stMaryClient The StMaryClient instance.
   */
  public MenuCommand(StMaryClient stMaryClient) {
    super(stMaryClient);

    this.name = "menu";
    this.description = "\uD83C\uDF32 Principal menu";
    this.cooldown = 10000L;

    // Define the command options
    this.options.add(new OptionData(OptionType.USER, "user", "The user to display the menu from."));
  }

  /**
   * Executes the Menu command.
   *
   * @param event The SlashCommandInteractionEvent triggered by the command.
   */
  @Override
  public void execute(SlashCommandInteractionEvent event, String language) {
    // Determine the user to display the menu for
    User user =
        event.getOption("user") == null
            ? event.getUser()
            : Objects.requireNonNull(event.getOption("user")).getAsUser();
    PlayerEntity player = DatabaseManager.getPlayer(user.getIdLong());

    // Check if the user has an adventure account
    if (player == null) {
      event
          .reply(TextManager.createText("menu_no_account", language).buildError())
          .setEphemeral(true)
          .queue();
      return;
    }

    ButtonListener buttonListener =
        getButtonListener(
            stMaryClient, event.getUser().getId(), language, player, getButtons(language));
    buttonListener.sendButtonMenu(
        event, profilEmbed(event.getUser().getGlobalName(), player, language));
    stMaryClient.getJda().addEventListener(buttonListener);
  }

  /**
   * Get the button list
   *
   * @param language The language of the Player
   * @return The Button List
   */
  public List<Button> getButtons(String language) {
    Button profilButton =
        Button.of(
            ButtonStyle.PRIMARY,
            "profil_btn",
            TextManager.getText("menu_btn_profil", language),
            Emoji.fromUnicode("\uD83D\uDCDD"));
    Button inventoryButton =
        Button.of(
            ButtonStyle.PRIMARY,
            "inventory_btn",
            TextManager.getText("menu_btn_backpack", language),
            Emoji.fromUnicode("\uD83C\uDF92"));
    Button titlesButton =
        Button.of(
            ButtonStyle.PRIMARY,
            "titles_btn",
            TextManager.getText("menu_btn_titles", language),
            Emoji.fromUnicode("\uD83C\uDFC5"));
    Button closeButton =
        Button.of(
            ButtonStyle.DANGER,
            "close_btn",
            TextManager.getText("menu_btn_close", language),
            Emoji.fromFormatted(BotConfigConstant.getEmote("no")));

    return new ArrayList<>(List.of(profilButton, inventoryButton, titlesButton, closeButton));
  }

  /**
   * Generate the Button Listener
   *
   * @param stMaryClient The StMary Client
   * @param userId The UserId
   * @param language The Player language
   * @return The Button Listener
   */
  private ButtonListener getButtonListener(
      StMaryClient stMaryClient,
      String userId,
      String language,
      PlayerEntity player,
      List<Button> buttons) {
    return new ButtonListener(stMaryClient, userId, language, buttons, 60000L, true, false) {
      @Override
      public void buttonClick(ButtonInteractionEvent event) {
        switch (event.getComponentId()) {
          case "profil_btn" -> profilBtn(event, language, authorId, player);
          case "inventory_btn" -> inventoryBtn(event, language, authorId, player);
          case "titles_btn" -> titlesBtn(event, language, authorId, player);
          case "close_btn" -> closeBtnMenu(event, event.getMessage().getContentRaw());
          default -> {
            event.reply("Something went wrong").queue();
            System.out.println("Switch error on menu command: " + event.getComponentId());
          }
        }
      }
    };
  }

  /**
   * Generates a formatted profile embed for a user.
   *
   * @param name The user's name.
   * @param player The PlayerEntity associated with the user.
   * @return The formatted profile as a string.
   */
  private String profilEmbed(String name, PlayerEntity player, String language) {
    TitleBase title = player.getCurrentTitle(stMaryClient);
    String icon = title.getIcon();
    String location = generateLocationText(player, language);

    // Return the text to send
    return TextManager.getText("menu_player", language)
        .replace("{{icon}}", icon)
        .replace("{{name}}", name)
        .replace("{{level}}", player.getLevel().toString())
        .replace("{{exp}}", player.getExperience().toString())
        .replace("{{location}}", location)
        .replace(
            "{{region}}",
            LocationManager.getRegionById(player.getCurrentLocationRegion())
                .getName(player.getLanguage()));
  }

  /**
   * Generates a formatted location text for a user.
   *
   * @param player The PlayerEntity instance
   * @param language The language of the player
   * @return The formatted location text
   */
  private String generateLocationText(PlayerEntity player, String language) {
    MoveEntity move = DatabaseManager.getMove(player.getId());

    if (move != null) {
      PlaceBase to = LocationManager.getPlaceById(move.getTo());
      PlaceBase from = LocationManager.getPlaceById(move.getFrom());
      String destinationName = generatePlaceName(to, player, language);
      String departureName = generatePlaceName(from, player, language);
      return TextManager.getText("menu_location_2", language)
          .replace("{{destination}}", destinationName)
          .replace("{{departure}}", departureName);
    } else {
      String place =
          LocationManager.getPlaceById(player.getCurrentLocationPlace())
              .getName(player.getLanguage());
      String town =
          (!player.getCurrentLocationTown().isEmpty())
              ? LocationManager.getTownById(player.getCurrentLocationTown())
                  .getName(player.getLanguage())
              : TextManager.getText("menu_no_town", language);
      return TextManager.getText("menu_location_1", language)
          .replace("{{town}}", town)
          .replace("{{place}}", place);
    }
  }

  /**
   * Generates a formatted place name.
   *
   * @param place The place to generate the name for
   * @param player The PlayerEntity instance
   * @param language The language of the player
   * @return The formatted place name
   */
  private String generatePlaceName(PlaceBase place, PlayerEntity player, String language) {
    if (place.isTownPlace()) {
      return place.getTown().getName(player.getLanguage())
          + " - "
          + place.getName(player.getLanguage());
    } else {
      return place.getName(player.getLanguage())
          + " - "
          + place.getRegion().getName(player.getLanguage());
    }
  }

  /**
   * Displays the user's profile.
   *
   * @param event The ButtonInteractionEvent triggered when the button is clicked.
   * @param language The language of the player
   * @param id The id of the player
   * @param player The PlayerEntity instance
   */
  public void profilBtn(
      ButtonInteractionEvent event, String language, String id, PlayerEntity player) {
    if (!event.getUser().getId().equals(id)) {
      event
          .reply(TextManager.createText("command_error_button_only_author", language).buildError())
          .setEphemeral(true)
          .queue();
      return;
    }

    event
        .getMessage()
        .editMessage(profilEmbed(event.getUser().getGlobalName(), player, language))
        .queue();
  }

  /**
   * Displays the user's inventory.
   *
   * @param event The ButtonInteractionEvent triggered when the button is clicked.
   * @param language The language of the player
   * @param id The id of the player
   * @param player The PlayerEntity instance
   */
  public void inventoryBtn(
      ButtonInteractionEvent event, String language, String id, PlayerEntity player) {
    if (!event.getUser().getId().equals(id)) {
      event
          .reply(TextManager.createText("command_error_button_only_author", language).buildError())
          .setEphemeral(true)
          .queue();
      return;
    }

    TitleBase title = player.getCurrentTitle(stMaryClient);
    String icon = title.getIcon();
    ObjectBase magicalBook = player.getMagicalBook(stMaryClient);
    String magicalBookName =
        (magicalBook != null)
            ? "%s `%s`".formatted(magicalBook.getIcon(), magicalBook.getName(language))
            : TextManager.getText("menu_no_magical_book", language);

    // Generate the inventory text
    String text =
        TextManager.getText("menu_backpack", language)
            .replace("{{icon}}", icon)
            .replace("{{name}}", event.getUser().getGlobalName())
            .replace("{{money}}", player.getMoney().toString())
            .replace("{{magical_book}}", magicalBookName);

    event.editMessage(text).queue();
  }

  /**
   * Displays the user's titles.
   *
   * @param event The ButtonInteractionEvent triggered when the button is clicked.
   * @param language The language of the player
   * @param id The id of the player
   * @param player The PlayerEntity instance
   */
  public void titlesBtn(
      ButtonInteractionEvent event, String language, String id, PlayerEntity player) {
    if (!event.getUser().getId().equals(id)) {
      event
          .reply(TextManager.createText("command_error_button_only_author", language).buildError())
          .setEphemeral(true)
          .queue();
      return;
    }

    TitleBase title = player.getCurrentTitle(stMaryClient);
    String icon = title.getIcon();

    Map<String, TitleBase> titles = player.getTitles(stMaryClient);

    StringBuilder stringBuilder = new StringBuilder();
    for (TitleBase t : titles.values()) {
      stringBuilder.append(t.getIcon()).append(" | `").append(t.getName(language)).append("`");
      if (t.getId().equals(title.getId())) {
        stringBuilder.append(TextManager.getText("menu_titles_actual", language));
      } else {
        stringBuilder.append("\n");
      }
    }

    // Generate the text to send
    String text =
        TextManager.getText("menu_titles", language)
            .replace("{{icon}}", icon)
            .replace("{{name}}", event.getUser().getGlobalName())
            .replace("{{titles}}", stringBuilder.toString())
            .replace("{{nb_titles}}", String.valueOf(titles.size()));

    event.editMessage(text).queue();
  }

  /**
   * Auto-complete handler for the Menu command.
   *
   * @param event The CommandAutoCompleteInteractionEvent triggered by auto-complete.
   */
  @Override
  public void autoComplete(CommandAutoCompleteInteractionEvent event, String language) {
    // This command doesn't support auto-complete, so this method is left empty.
  }
}
