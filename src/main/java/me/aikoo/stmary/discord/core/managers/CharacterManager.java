package me.aikoo.stmary.discord.core.managers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.aikoo.stmary.discord.core.bases.CharacterBase;
import me.aikoo.stmary.discord.core.bases.Effect;
import me.aikoo.stmary.discord.core.utils.JSONFileReaderUtils;

/** Manages characters in the bot. */
public class CharacterManager {
  private static final HashMap<String, CharacterBase> characters = new HashMap<>();

  /**
   * Get a character by its id.
   *
   * @param id The id of the character.
   * @return The character.
   */
  public static CharacterBase getCharacter(String id) {
    return characters.get(id);
  }

  /**
   * Get a dialog by its id.
   *
   * @param id The id of the dialog.
   * @return The dialog.
   */
  public static CharacterBase.Dialog getDialog(String id) {
    for (CharacterBase character : characters.values()) {
      if (character.getDialogs().containsKey(id)) {
        return character.getDialog(id);
      }
    }

    return null;
  }

  /**
   * Get a choice by its id.
   *
   * @param id The id of the choice.
   * @return The choice.
   */
  public static CharacterBase.Choice getChoice(String id) {
    for (CharacterBase character : characters.values()) {
      for (CharacterBase.Dialog dialog : character.getDialogs().values()) {
        for (CharacterBase.Choice choice : dialog.getChoices()) {
          if (choice.getId().equals(id)) {
            return choice;
          }
        }
      }
    }

    return null;
  }

  /** Load all characters from the characters folder. */
  public static void load() {
    List<JsonObject> charactersObjects = JSONFileReaderUtils.readAllFilesFrom("characters");

    for (JsonObject obj : charactersObjects) {
      loadCharacter(obj);
    }
  }

  /**
   * Load a character from a JsonObject.
   *
   * @param obj The JsonObject containing the character.
   */
  private static void loadCharacter(JsonObject obj) {
    String id = obj.get("id").getAsString();
    HashMap<String, String> names =
        getCharacterNamesOrDescription(obj.get("name").getAsJsonObject());
    HashMap<String, String> descriptions =
        getCharacterNamesOrDescription(obj.get("description").getAsJsonObject());

    CharacterBase character = new CharacterBase(id);
    character.addNames(names);
    character.addDescriptions(descriptions);

    for (Map.Entry<String, JsonElement> dialogEntry :
        obj.get("dialogs").getAsJsonObject().entrySet()) {
      String dialogId = dialogEntry.getKey();
      JsonObject dialogObj = dialogEntry.getValue().getAsJsonObject();
      CharacterBase.Dialog dialog = loadDialog(dialogObj, character);
      character.addDialog(dialogId, dialog);
    }

    characters.put(id, character);
  }

  /**
   * Load a dialog from a JsonObject.
   *
   * @param obj The JsonObject containing the dialog.
   * @param character The character of the dialog.
   * @return The dialog.
   */
  private static CharacterBase.Dialog loadDialog(JsonObject obj, CharacterBase character) {
    boolean haveChoices = obj.get("haveChoices").getAsBoolean();
    HashMap<String, String> texts =
        getCharacterNamesOrDescription(obj.get("text").getAsJsonObject());
    HashMap<String, String> questions =
        getCharacterNamesOrDescription(obj.get("choicesQuestion").getAsJsonObject());
    ArrayList<Effect> effects = new ArrayList<>();
    ArrayList<CharacterBase.Choice> choices = new ArrayList<>();

    CharacterBase.Dialog dialog =
        new CharacterBase.Dialog(character, haveChoices, texts, questions);

    if (haveChoices) {
      for (JsonElement choiceElement : obj.get("choices").getAsJsonArray()) {
        JsonObject choiceObj = choiceElement.getAsJsonObject();
        String buttonIcon = choiceObj.get("buttonIcon").getAsString();
        String buttonStyle = choiceObj.get("buttonStyle").getAsString();
        String id = choiceObj.get("id").getAsString();
        String nextDialog = choiceObj.get("next").getAsString();

        CharacterBase.Choice choice =
            new CharacterBase.Choice(character, buttonIcon, buttonStyle, id, nextDialog);

        // For each text, get key and value
        for (Map.Entry<String, JsonElement> langEntry :
            choiceObj.get("text").getAsJsonObject().entrySet()) {
          choice.addText(langEntry.getKey(), langEntry.getValue().getAsString());
        }

        choices.add(choice);
      }
    }

    // For each effect, get key and value
    for (Map.Entry<String, JsonElement> effectEntry :
        obj.get("effects").getAsJsonObject().entrySet()) {
      effects.add(new Effect(effectEntry.getKey(), effectEntry.getValue().getAsString()));
    }

    dialog.addChoices(choices);
    return dialog;
  }

  /**
   * Get the names or descriptions of a character.
   *
   * @param nameObj The JsonObject containing the names or descriptions.
   * @return A HashMap containing the names or descriptions.
   */
  private static HashMap<String, String> getCharacterNamesOrDescription(JsonObject nameObj) {
    HashMap<String, String> names = new HashMap<>();

    for (Map.Entry<String, JsonElement> lang : nameObj.entrySet()) {
      names.put(lang.getKey(), lang.getValue().getAsString());
    }

    return names;
  }
}
