package me.aikoo.StMary.core.managers;

import com.google.gson.JsonObject;
import me.aikoo.StMary.core.bases.CharacterBase;
import me.aikoo.StMary.core.utils.JSONFileReaderUtils;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.util.ArrayList;
import java.util.HashMap;

public class CharacterManager {

    private final HashMap<String, CharacterBase> characters = new HashMap<>();

    public CharacterManager() {
        this.loadCharacters();
    }

    public CharacterBase getCharacter(String characterId) {
        return this.characters.get(characterId);
    }

    public CharacterBase getCharacterByName(String characterName, String language) {
        return this.characters.values().stream().filter(character -> character.getCharacterInformation(language).getName().equalsIgnoreCase(characterName)).findFirst().orElse(null);
    }

    public String formatCharacterDialog(CharacterBase.Information characterInformation, CharacterBase.Dialog dialog) {
        String text = "\uD83D\uDDE3\uFE0F **" + characterInformation.getName() + "** - " + dialog.getText();

        if (dialog.isQuestion()) {
            text += "\n\n➡\uFE0F **" + dialog.getQuestion() + "**\n";
        }

        return text;
    }

    private void loadCharacters() {
        ArrayList<JsonObject> charactersObject = JSONFileReaderUtils.readAllFilesFrom("characters");

        charactersObject.forEach(this::loadCharacter);
    }

    private void loadCharacter(JsonObject characterObject) {
        HashMap<String, CharacterBase.Information> informations = new HashMap<>();
        String id = characterObject.get("id").getAsString();
        informations.put("en", this.loadCharacterByLanguage(id, characterObject, "en"));
        informations.put("fr", this.loadCharacterByLanguage(id, characterObject, "fr"));

        this.characters.put(id, new CharacterBase(informations));
    }

    private CharacterBase.Information loadCharacterByLanguage(String id, JsonObject characterObject, String language) {
        String characterName = characterObject.get("name").getAsJsonObject().get(language).getAsString();
        String characterDescription = characterObject.get("description").getAsJsonObject().get(language).getAsString();

        HashMap<String, CharacterBase.Dialog> dialogs = this.loadDialogs(characterObject, language);

        return new CharacterBase.Information(id, characterName, characterDescription, dialogs);
    }

    private HashMap<String, CharacterBase.Dialog> loadDialogs(JsonObject characterObject, String language) {
        HashMap<String, CharacterBase.Dialog> dialogs = new HashMap<>();

        characterObject.get("dialogs").getAsJsonObject().keySet().forEach(key -> {
            CharacterBase.Dialog dialog = this.loadDialog(key, characterObject.get("dialogs").getAsJsonObject().get(key).getAsJsonObject(), language);
            dialogs.put(dialog.getId(), dialog);
        });

        return dialogs;
    }

    private CharacterBase.Dialog loadDialog(String id, JsonObject dialogObject, String language) {
        String dialog = dialogObject.get(language).getAsString();
        boolean isQuestion = dialogObject.get("choice").getAsJsonObject() != null;

        if (isQuestion) {
            JsonObject choice = dialogObject.get("choice").getAsJsonObject();
            String question = choice.get("question").getAsJsonObject().get(language).getAsString();
            HashMap<String, CharacterBase.Option> options = new HashMap<>();

            choice.get("choices").getAsJsonObject().keySet().forEach(key ->
                    options.put(key, this.loadOption(id, key, choice.get("choices").getAsJsonObject().get(key).getAsJsonObject(), language))
            );

            return new CharacterBase.Dialog(id, dialog, true, question, options);
        } else {
            return new CharacterBase.Dialog(id, dialog, false, null, null);
        }
    }

    private CharacterBase.Option loadOption(String dialogId, String optionId, JsonObject option, String language) {
        String name = option.get("name").getAsJsonObject().get(language).getAsString();
        String answer = option.get(language).getAsString();
        String icon = option.get("name").getAsJsonObject().get("icon").getAsString();
        ButtonStyle style = ButtonStyle.valueOf(option.get("name").getAsJsonObject().get("style").getAsString().toUpperCase());

        return new CharacterBase.Option(optionId, name, icon, style, new CharacterBase.Dialog(dialogId, answer, false, null,null));
    }
}
