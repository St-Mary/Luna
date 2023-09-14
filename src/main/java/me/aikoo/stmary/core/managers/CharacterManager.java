package me.aikoo.stmary.core.managers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import me.aikoo.stmary.core.bases.CharacterBase;
import me.aikoo.stmary.core.bases.Effect;
import me.aikoo.stmary.core.utils.JSONFileReaderUtils;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

/** Manages characters in the bot. */
public class CharacterManager {
  private static final HashMap<String, CharacterBase> characters = new HashMap<>();

  static {
    load();
  }

  public static CharacterBase getCharacter(String id) {
    return characters.get(id);
  }

  private static void load() {
    List<JsonObject> charactersObjects = JSONFileReaderUtils.readAllFilesFrom("characters");

    for (JsonObject obj : charactersObjects) {
      loadCharacter(obj);
    }
  }

  private static void loadCharacter(JsonObject obj) {
    String id  = obj.get("id").getAsString();
    HashMap<String, String> names = getCharacterNamesOrDescription(obj.get("name").getAsJsonObject());
    HashMap<String, String> descriptions = getCharacterNamesOrDescription(obj.get("description").getAsJsonObject());

    CharacterBase character = new CharacterBase(id);
    character.addNames(names);
    character.addDescriptions(descriptions);

    for (String dialogId : obj.get("dialogs").getAsJsonObject().keySet()) {
      JsonObject dialogObj = obj.get("dialogs").getAsJsonObject().get(dialogId).getAsJsonObject();
      CharacterBase.Dialog dialog = loadDialog(dialogObj, character);
      character.addDialog(dialogId, dialog);
    }

    characters.put(id, character);
  }

  private static CharacterBase.Dialog loadDialog(JsonObject obj, CharacterBase character) {
    boolean haveChoices = obj.get("haveChoices").getAsBoolean();
    HashMap<String, String> texts = getCharacterNamesOrDescription(obj.get("text").getAsJsonObject());
    HashMap<String, String> questions = getCharacterNamesOrDescription(obj.get("choicesQuestion").getAsJsonObject());
    ArrayList<Effect> effects = new ArrayList<>();
    ArrayList<CharacterBase.Choice> choices = new ArrayList<>();

    CharacterBase.Dialog dialog = new CharacterBase.Dialog(haveChoices, texts, questions);

    if (haveChoices) {
      for (JsonElement choiceElement : obj.get("choices").getAsJsonArray()) {
        JsonObject choiceObj = choiceElement.getAsJsonObject();
        String buttonIcon = choiceObj.get("buttonIcon").getAsString();
        String buttonStyle = choiceObj.get("buttonStyle").getAsString();
        String id = choiceObj.get("id").getAsString();
        String nextDialog = choiceObj.get("next").getAsString();

        CharacterBase.Choice choice = new CharacterBase.Choice(character, buttonIcon, buttonStyle, id, nextDialog);

        for (String lang : choiceObj.get("text").getAsJsonObject().keySet()) {
          choice.addText(lang, choiceObj.get("text").getAsJsonObject().get(lang).getAsString());
        }

        choices.add(choice);
      }
    }

    // For each effect, get key and value
    for (String effect : obj.get("effects").getAsJsonObject().keySet()) {
      String value = obj.get("effects").getAsJsonObject().get(effect).getAsString();
      effects.add(new Effect(effect, value));
    }

    dialog.addChoices(choices);
    return dialog;
  }

  private static HashMap<String, String> getCharacterNamesOrDescription(JsonObject nameObj) {
    HashMap<String, String> names = new HashMap<>();
    for (String lang : nameObj.keySet()) {
      names.put(lang, nameObj.get(lang).getAsString());
    }
    return names;
  }
}
