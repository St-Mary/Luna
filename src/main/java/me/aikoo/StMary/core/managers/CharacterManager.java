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

            ArrayList<String> options = new ArrayList<>();

            dialog.getOptions().forEach((key, option) -> options.add(option.getName()));

            text += String.join(", ", options);
        }

        return text;
    }

    private void loadCharacters() {
        ArrayList<JsonObject> charactersObject = JSONFileReaderUtils.readAllFilesFrom("characters");

        charactersObject.forEach(this::loadCharacter);
    }

    private void loadCharacter(JsonObject characterObject) {
        HashMap<String, CharacterBase.Information> informations = new HashMap<>();
        informations.put("en", this.loadCharacterByLanguage(characterObject, "en"));
        informations.put("fr", this.loadCharacterByLanguage(characterObject, "fr"));

        this.characters.put(characterObject.get("id").getAsString(), new CharacterBase(informations));
    }

    private CharacterBase.Information loadCharacterByLanguage(JsonObject characterObject, String language) {
        String characterName = characterObject.get("name").getAsJsonObject().get(language).getAsString();
        String characterDescription = characterObject.get("description").getAsJsonObject().get(language).getAsString();

        HashMap<String, CharacterBase.Dialog> dialogs = this.loadDialogs(characterObject, language);

        return new CharacterBase.Information(characterName, characterDescription, dialogs);
    }

    private HashMap<String, CharacterBase.Dialog> loadDialogs(JsonObject characterObject, String language) {
        HashMap<String, CharacterBase.Dialog> dialogs = new HashMap<>();

        characterObject.get("dialogs").getAsJsonObject().keySet().forEach(key ->
                dialogs.put(key, this.loadDialog(characterObject.get("dialogs").getAsJsonObject().get(key).getAsJsonObject(), language))
        );

        return dialogs;
    }

    private CharacterBase.Dialog loadDialog(JsonObject dialogObject, String language) {
        String dialog = dialogObject.get(language).getAsString();
        boolean isQuestion = dialogObject.get("choice").getAsJsonObject() != null;

        if (isQuestion) {
            JsonObject choice = dialogObject.get("choice").getAsJsonObject();
            String question = choice.get("question").getAsJsonObject().get(language).getAsString();
            HashMap<String, CharacterBase.Option> options = new HashMap<>();

            choice.get("choices").getAsJsonObject().keySet().forEach(key ->
                    options.put(key, this.loadOption(choice.get("choices").getAsJsonObject().get(key).getAsJsonObject(), language))
            );

            return new CharacterBase.Dialog(dialog, true, question, options);
        } else {
            return new CharacterBase.Dialog(dialog, false, null, null);
        }
    }

    private CharacterBase.Option loadOption(JsonObject option, String language) {
        String name = option.get("name").getAsJsonObject().get(language).getAsString();
        String answer = option.get("answer").getAsJsonObject().get(language).getAsString();
        String icon = option.get("name").getAsJsonObject().get("icon").getAsString();
        ButtonStyle style = ButtonStyle.valueOf(option.get("name").getAsJsonObject().get("style").getAsString().toUpperCase());

        return new CharacterBase.Option(name, icon, style, new CharacterBase.Dialog(answer, false, null,null));
    }
}
