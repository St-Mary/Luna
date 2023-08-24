package me.aikoo.StMary.core.managers;

import com.google.gson.JsonObject;
import lombok.Getter;
import me.aikoo.StMary.core.utils.JSONFileReaderUtils;
import me.aikoo.StMary.core.bot.StMaryClient;

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
    @Deprecated
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
    @Deprecated
    public String generateError(String title, String text) {
        return generateScene("❌ Erreur : " + title, text);
    }

    /**
     * Get the text associated with a given id.
     *
     * @param id The id of the text.
     * @return The text associated with the name, or null if it doesn't exist.
     */
    public String getText(String id) {
        return (this.texts.get(id) != null) ? this.texts.get(id).get("text").getAsString() : null;
    }

    /**
     * Get the title associated with a given id.
     *
     * @param id The id of the title.
     * @return The title associated with the name, or null if it doesn't exist.
     */
    @Deprecated
    public String getTitle(String id) {
        return (this.texts.get(id) != null) ? this.texts.get(id).get("title").getAsString() : null;
    }

    /**
     * Create a new Text object with given id.
     *
     * @param id The id of the text.
     * @return The created Text object.
     */
    public Text createText(String id) {
        return new Text(id, this.getText(id), this.getTitle(id));
    }

    /**
     * Load texts from JSON files.
     */
    private void load() {
        ArrayList<JsonObject> files = JSONFileReaderUtils.readAllFilesFrom("text");

        for (JsonObject file : files) {
            for (String key : file.keySet()) {
                this.texts.put(key, file.get(key).getAsJsonObject());
            }
        }
    }

    /**
     * Text class for formatting and generating texts.
     */
    public class Text {

        @Getter
        private final String id;
        @Getter
        private final String text;
        @Getter
        private final String title;

        private final StringBuilder formattedText;
        private String tmpText;

        /**
         * Constructor for the Text class.
         *
         * @param id    The id of the text.
         * @param text  The text.
         * @param title The title.
         */
        public Text(String id, String text, String title) {
            this.id = id;
            this.text = text;
            this.title = title;

            this.tmpText = text;

            this.formattedText = new StringBuilder();
        }

        /**
         * Format locations in the text.
         */
        public void formatLocations() {
            String regex = "\\{location:([^{}]+)\\}";
            tmpText = tmpText.replaceAll("\n\n", "\n- ");

            Pattern pattern = Pattern.compile(regex);
            if ((pattern.matcher(tmpText).find())) {
                while (pattern.matcher(tmpText).find()) {
                    String name = stMaryClient.getLocationManager().extractLocationName(tmpText);
                    tmpText = tmpText.replaceFirst(regex, stMaryClient.getLocationManager().formatLocation(name));
                }
            }
        }

        /**
         * Replace a placeholder with a given replacement.
         *
         * @param name        The name of the placeholder.
         * @param replacement The replacement.
         * @return The Text object.
         */
        public Text replace(String name, String replacement) {
            Pattern pattern = Pattern.compile("\\{\\{" + name + "\\}\\}", Pattern.CASE_INSENSITIVE);
            tmpText = pattern.matcher(tmpText).replaceAll(replacement);
            return this;
        }

        /**
         * Build the text.
         *
         * @return The built text.
         */
        public String build() {
            formattedText.append("╭───────────┈ ➤ ✎ **").append(title).append("**\n- ");
            this.formatLocations();
            formattedText.append(tmpText).append("\n╰─────────── ·\uFEFF \uFEFF \uFEFF· \uFEFF ·\uFEFF \uFEFF \uFEFF· \uFEFF✦");
            return formattedText.toString();
        }

        /**
         * Build the error.
         *
         * @return The built error.
         */
        public String buildError() {
            formattedText.append("╭───────────┈ ➤ ✎ ❌ **Erreur : ").append(title).append("**\n- ");
            this.formatLocations();
            formattedText.append(tmpText).append("\n╰─────────── ·\uFEFF \uFEFF \uFEFF· \uFEFF ·\uFEFF \uFEFF \uFEFF· \uFEFF✦");
            return formattedText.toString();
        }
    }
}
