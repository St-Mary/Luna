package me.aikoo.stmary.core.managers;

import com.google.gson.JsonObject;
import me.aikoo.stmary.core.bases.CharacterBase;
import me.aikoo.stmary.core.utils.JSONFileReaderUtils;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.util.HashMap;
import java.util.List;

/**
 * Manages characters in the bot.
 */
public class CharacterManager {

    private static final HashMap<String, CharacterBase> characters = new HashMap<>();


    static {
        loadCharacters();
    }

    /**
     * Get all the characters.
     * @param characterId The ID of the character.
     * @return The character.
     */
    public static CharacterBase getCharacter(String characterId) {
        return characters.get(characterId);
    }

    /**
     * Get all the characters.
     * @param characterName The name of the character.
     * @param language The language to get the character in.
     * @return The character.
     */
    public static CharacterBase getCharacterByName(String characterName, String language) {
        return characters.values().stream().filter(character -> character.getCharacterInformation(language).getName().equalsIgnoreCase(characterName)).findFirst().orElse(null);
    }

    /**
     * Format a character dialog.
     * @param characterInformation The information of the character.
     * @param dialog The dialog of the character.
     * @return The formatted character dialog.
     */
    public static String formatCharacterDialog(CharacterBase.Information characterInformation, CharacterBase.Dialog dialog) {
        String text = "\uD83D\uDDE3\uFE0F  **" + characterInformation.getName() + "** - " + dialog.getText();

        if (dialog.isQuestion()) {
            text += "\n\n➡\uFE0F **" + dialog.getQuestion() + "**\n";
        }

        return text;
    }

    /**
     * Format a character option.
     */
    private static void loadCharacters() {
        List<JsonObject> charactersObject = JSONFileReaderUtils.readAllFilesFrom("characters");

        charactersObject.forEach(CharacterManager::loadCharacter);
    }

    /**
     * Load a character.
     * @param characterObject The character object.
     */
    private static void loadCharacter(JsonObject characterObject) {
        HashMap<String, CharacterBase.Information> informations = new HashMap<>();
        String id = characterObject.get("id").getAsString();
        informations.put("en", loadCharacterByLanguage(id, characterObject, "en"));
        informations.put("fr", loadCharacterByLanguage(id, characterObject, "fr"));

        characters.put(id, new CharacterBase(informations));
    }

    /**
     * Load a character by language.
     * @param id The ID of the character.
     * @param characterObject The character object.
     * @param language The language to load the character in.
     * @return The character information.
     */
    private static CharacterBase.Information loadCharacterByLanguage(String id, JsonObject characterObject, String language) {
        String characterName = characterObject.get("name").getAsJsonObject().get(language).getAsString();
        String characterDescription = characterObject.get("description").getAsJsonObject().get(language).getAsString();

        HashMap<String, CharacterBase.Dialog> dialogs = loadDialogs(characterObject, language);

        return new CharacterBase.Information(id, characterName, characterDescription, dialogs);
    }

    /**
     * Load the dialogs of a character.
     * @param characterObject The character object.
     * @param language The language to load the dialogs in.
     * @return The dialogs of the character.
     */
    private static HashMap<String, CharacterBase.Dialog> loadDialogs(JsonObject characterObject, String language) {
        HashMap<String, CharacterBase.Dialog> dialogs = new HashMap<>();

        characterObject.get("dialogs").getAsJsonObject().keySet().forEach(key -> {
            CharacterBase.Dialog dialog = loadDialog(key, characterObject.get("dialogs").getAsJsonObject().get(key).getAsJsonObject(), language);
            dialogs.put(dialog.getId(), dialog);
        });

        return dialogs;
    }

    /**
     * Load a dialog.
     * @param id The ID of the dialog.
     * @param dialogObject The dialog object.
     * @param language The language to load the dialog in.
     * @return The dialog.
     */
    private static CharacterBase.Dialog loadDialog(String id, JsonObject dialogObject, String language) {
        String dialog = dialogObject.get(language).getAsString();
        boolean isQuestion = dialogObject.get("choice").getAsJsonObject() != null;

        if (isQuestion) {
            JsonObject choice = dialogObject.get("choice").getAsJsonObject();
            String question = choice.get("question").getAsJsonObject().get(language).getAsString();
            HashMap<String, CharacterBase.Option> options = new HashMap<>();

            choice.get("choices").getAsJsonObject().keySet().forEach(key ->
                    options.put(key, loadOption(id, key, choice.get("choices").getAsJsonObject().get(key).getAsJsonObject(), language))
            );

            return new CharacterBase.Dialog(id, dialog, true, question, options);
        } else {
            return new CharacterBase.Dialog(id, dialog, false, null, null);
        }
    }

    /**
     * Load an option.
     * @param dialogId The ID of the dialog.
     * @param optionId The ID of the option.
     * @param option The option object.
     * @param language The language to load the option in.
     * @return The option.
     */
    private static CharacterBase.Option loadOption(String dialogId, String optionId, JsonObject option, String language) {
        String name = option.get("name").getAsJsonObject().get(language).getAsString();
        String answer = option.get(language).getAsString();
        String icon = option.get("name").getAsJsonObject().get("icon").getAsString();
        ButtonStyle style = ButtonStyle.valueOf(option.get("name").getAsJsonObject().get("style").getAsString().toUpperCase());

        return new CharacterBase.Option(optionId, name, icon, style, new CharacterBase.Dialog(dialogId, answer, false, null, null));
    }
}
