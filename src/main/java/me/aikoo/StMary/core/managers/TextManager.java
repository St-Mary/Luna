package me.aikoo.StMary.core.managers;

import com.google.gson.JsonObject;
import me.aikoo.StMary.core.JSONFileReader;
import me.aikoo.StMary.core.StMaryClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Text manager for formatting and generating scenes and errors.
 */
public class TextManager {

    private final HashMap<String, JsonObject> texts = new HashMap<>();
    private final StMaryClient stMaryClient;

    /**
     * Constructor for the TextManager class.
     *
     * @param stMaryClient The main StMary client.
     */
    public TextManager(StMaryClient stMaryClient) {
        this.stMaryClient = stMaryClient;
        this.load();
    }

    /**
     * Generate a scene with given title and text, replacing location placeholders with their corresponding format.
     *
     * @param title The scene's title.
     * @param text  The scene's text.
     * @return The generated scene.
     */
    public String generateScene(String title, String text) {
        String regex = "\\{location:([^{}]+)\\}";
        String formattedText = text.replaceAll("\n\n", "\n- ");

        Pattern pattern = Pattern.compile(regex);
        if ((pattern.matcher(formattedText).find())) {
            while (pattern.matcher(formattedText).find()) {
                String name = stMaryClient.getLocationManager().extractLocationName(formattedText);
                formattedText = formattedText.replaceFirst(regex, stMaryClient.getLocationManager().formatLocation(name));
            }
        }

        return "╭───────────┈ ➤ ✎ **" + title + "**\n- " + formattedText + "\n╰─────────── ·\uFEFF \uFEFF \uFEFF· \uFEFF ·\uFEFF \uFEFF \uFEFF· \uFEFF✦";
    }

    /**
     * Generate an error message with given title and text using the generateScene method.
     *
     * @param title The error's title.
     * @param text  The error's text.
     * @return The generated error message.
     */
    public String generateError(String title, String text) {
        return generateScene("❌ Erreur : " + title, text);
    }

    /**
     * Get the text associated with a given name.
     *
     * @param name The name of the text.
     * @return The text associated with the name, or null if it doesn't exist.
     */
    public String getText(String name) {
        return (this.texts.get(name) != null) ? this.texts.get(name).get("text").getAsString() : null;
    }

    /**
     * Get the title associated with a given name.
     *
     * @param name The name of the title.
     * @return The title associated with the name, or null if it doesn't exist.
     */
    public String getTitle(String name) {
        return (this.texts.get(name) != null) ? this.texts.get(name).get("title").getAsString() : null;
    }

    /**
     * Load texts from JSON files.
     */
    private void load() {
        ArrayList<JsonObject> files = JSONFileReader.readAllFilesFrom("text");

        for (JsonObject file : files) {
            String name = file.get("name").getAsString();
            this.texts.put(name, file);
        }
    }
}
