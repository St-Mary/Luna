package me.aikoo.stmary.core.bases;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CharacterBase {

  private final String id;
  private final HashMap<String, String> name = new HashMap<>();
  private final HashMap<String, String> description = new HashMap<>();

  private final HashMap<String, Dialog> dialogs = new HashMap<>();

  public CharacterBase(String id) {
    this.id = id;
  }

  public void addNames(HashMap<String, String> names) {
    this.name.putAll(names);
  }

  public void addDescriptions(HashMap<String, String> descriptions) {
    this.description.putAll(descriptions);
  }

  public void addDialog(String id, Dialog dialog) {
    this.dialogs.put(id, dialog);
  }

  public Dialog getDialog(String id) {
    return this.dialogs.get(id);
  }

  public static class Dialog {
    private final boolean haveChoices;
    private final HashMap<String, String> text;
    private final HashMap<String, String> question;
    private final ArrayList<Effect> effects = new ArrayList<>();
    private final ArrayList<Choice> choices = new ArrayList<>();

    public Dialog(boolean haveChoices, HashMap<String, String> text, HashMap<String, String> question) {
      this.haveChoices = haveChoices;
      this.text = text;
      this.question = question;
    }

    public String getText(String language) {
      return this.text.get(language);
    }

    public String getQuestion(String language) {
      return this.question.get(language);
    }

    public Choice getChoice(String id) {
      for (Choice choice : this.choices) {
        if (choice.id.equals(id)) {
          return choice;
        }
      }
      return null;
    }

    public void addChoices(List<Choice> choices) {
      this.choices.addAll(choices);
    }
  }

  public static class Choice {
    private final HashMap<String, String> texts = new HashMap<>();
    private final CharacterBase character;
    private final String buttonIcon;
    private final String buttonStyle;
    private final String id;
    private final String nextDialog;

    public Choice(CharacterBase character, String buttonIcon, String buttonStyle, String id, String nextDialog) {
      this.character = character;
      this.buttonIcon = buttonIcon;
      this.buttonStyle = buttonStyle;
      this.id = id;
      this.nextDialog = nextDialog;
    }

    public void addText(String lang, String text) {
      this.texts.put(lang, text);
    }

    public Dialog getNextDialog() {
      return character.getDialog(nextDialog);
    }

    public Button getButton(String language) {
      return Button.of(ButtonStyle.valueOf(this.buttonStyle.toUpperCase()), id, this.texts.get(language), Emoji.fromFormatted(this.buttonIcon));
    }
  }
}
