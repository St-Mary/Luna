package me.aikoo.stmary.core.bases;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

/** The CharacterBase class represents a character. */
public class CharacterBase {

  @Getter private final String id;
  private final HashMap<String, String> name = new HashMap<>();
  private final HashMap<String, String> description = new HashMap<>();

  @Getter private final HashMap<String, Dialog> dialogs = new HashMap<>();

  /**
   * Creates a new CharacterBase.
   *
   * @param id The id of the character.
   */
  public CharacterBase(String id) {
    this.id = id;
  }

  /**
   * Add names to the character.
   *
   * @param names The names to add.
   */
  public void addNames(Map<String, String> names) {
    this.name.putAll(names);
  }

  /**
   * Add descriptions to the character.
   *
   * @param descriptions The descriptions to add.
   */
  public void addDescriptions(Map<String, String> descriptions) {
    this.description.putAll(descriptions);
  }

  /**
   * Get the name of the character.
   *
   * @param language The language of the player to get the name in.
   * @return The name of the character.
   */
  public String getName(String language) {
    return this.name.get(language);
  }

  /**
   * Get the description of the character.
   *
   * @param language The language of the player to get the description in.
   * @return The description of the character.
   */
  public String getDescription(String language) {
    return this.description.get(language);
  }

  /**
   * Add a dialog to the character.
   *
   * @param id The id of the dialog.
   * @param dialog The dialog to add.
   */
  public void addDialog(String id, Dialog dialog) {
    this.dialogs.put(id, dialog);
  }

  /**
   * Get a dialog by its id.
   *
   * @param id The id of the dialog.
   * @return The dialog.
   */
  public Dialog getDialog(String id) {
    return this.dialogs.get(id);
  }

  /** The Dialog class represents a dialog. */
  public static class Dialog {
    @Getter private final CharacterBase character;
    @Getter private final boolean haveChoices;
    private final HashMap<String, String> text;
    private final HashMap<String, String> question;
    @Getter private final ArrayList<Effect> effects = new ArrayList<>();
    @Getter private final ArrayList<Choice> choices = new ArrayList<>();

    /**
     * Creates a new Dialog.
     *
     * @param character The character of the dialog.
     * @param haveChoices If the dialog have choices.
     * @param text The text of the dialog.
     * @param question The question of the dialog.
     */
    public Dialog(
        CharacterBase character, boolean haveChoices, HashMap<String, String> text, HashMap<String, String> question) {
        this.character = character;
      this.haveChoices = haveChoices;
      this.text = text;
      this.question = question;
    }

    /**
     * Get the text of the dialog.
     *
     * @param language The language of the player to get the text in.
     * @return The text of the dialog.
     */
    public String getText(String language) {
      return this.text.get(language);
    }

    /**
     * Get the question of the dialog.
     *
     * @param language The language of the player to get the question in.
     * @return The question of the dialog.
     */
    public String getQuestion(String language) {
      return this.question.get(language);
    }

    /**
     * Print the dialog.
     *
     * @param language The language of the player to print the dialog in.`
     *                 @return The printed dialog.
     */
    public String printDialog(String language) {
      StringBuilder stringBuilder = new StringBuilder();

      stringBuilder.append("\uD83D\uDDE3️  **").append(this.character.getName(language)).append(" :** ").append(this.getText(language)).append("\n");

      if (this.isHaveChoices()) {
        stringBuilder.append("\n➡️ **").append(this.getQuestion(language)).append("**").append("\n");
      }

      return stringBuilder.toString();
    }

    /**
     * Get the choice of the dialog.
     *
     * @param id The id of the choice.
     * @return The choice.
     */
    public Choice getChoice(String id) {
      for (Choice choice : this.choices) {
        if (choice.id.equals(id)) {
          return choice;
        }
      }
      return null;
    }

    /**
     * Add choices to the dialog.
     *
     * @param choices The choices to add.
     */
    public void addChoices(List<Choice> choices) {
      this.choices.addAll(choices);
    }
  }

  /** The Choice class represents a choice. */
  public static class Choice {
    private final HashMap<String, String> texts = new HashMap<>();
    private final CharacterBase character;
    private final String buttonIcon;
    private final String buttonStyle;
    @Getter private final String id;
    private final String nextDialog;

    /**
     * Creates a new Choice.
     *
     * @param character The character of the choice.
     * @param buttonIcon The icon of the button.
     * @param buttonStyle The style of the button.
     * @param id The id of the button.
     * @param nextDialog The id of the next dialog.
     */
    public Choice(
        CharacterBase character,
        String buttonIcon,
        String buttonStyle,
        String id,
        String nextDialog) {
      this.character = character;
      this.buttonIcon = buttonIcon;
      this.buttonStyle = buttonStyle;
      this.id = id;
      this.nextDialog = nextDialog;
    }

    /**
     * Add a text to the choice.
     *
     * @param lang The language of the text.
     * @param text The text to add.
     */
    public void addText(String lang, String text) {
      this.texts.put(lang, text);
    }

    /**
     * Get the next dialog.
     *
     * @return The next dialog.
     */
    public Dialog getNextDialog() {
      return character.getDialog(nextDialog);
    }

    /**
     * Get the button.
     *
     * @param language The language of the player to get the button in.
     * @return The button.
     */
    public Button getButton(String language) {
      return Button.of(
          ButtonStyle.valueOf(this.buttonStyle.toUpperCase()),
          id,
          this.texts.get(language),
          Emoji.fromFormatted(this.buttonIcon));
    }
  }
}
