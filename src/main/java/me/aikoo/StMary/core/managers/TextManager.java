package me.aikoo.StMary.core.managers;

import com.google.gson.JsonObject;
import lombok.Getter;
import me.aikoo.StMary.core.bot.StMaryClient;
import me.aikoo.StMary.core.utils.JSONFileReaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Text manager for formatting and generating scenes and errors.
 */
public class TextManager {

    private final Logger LOGGER = LoggerFactory.getLogger(TextManager.class);
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
     * Get the text associated with a given id and language.
     *
     * @param id       The id of the text.
     * @param language The language of the text.
     * @return The text associated with the name, or null if it doesn't exist.
     */
    public String getText(String id, String language) {
        return (this.texts.get(id) != null) ? this.texts.get(id).get(language).getAsJsonObject().get("text").getAsString() : null;
    }

    /**
     * Get the title associated with a given id and language.
     *
     * @param id       The id of the title.
     * @param language The language of the title.
     * @return The title associated with the name, or null if it doesn't exist.
     */
    public String getTitle(String id, String language) {
        return (this.texts.get(id) != null) ? this.texts.get(id).get(language).getAsJsonObject().get("title").getAsString() : null;
    }

    /**
     * Create a new Text object with given id and language.
     *
     * @param id       The id of the text.
     * @param language The language of the text.
     * @return The created Text object.
     */
    public Text createText(String id, String language) {
        return new Text(id, language, this.getText(id, language), this.getTitle(id, language));
    }

    /**
     * Load texts from JSON files.
     */
    private void load() {
        ArrayList<JsonObject> files = JSONFileReaderUtils.readAllFilesFrom("text");

        for (JsonObject file : files) {
            for (String key : file.keySet()) {
                if (file.get(key).getAsJsonObject().get("en").getAsJsonObject().get("text") == null || file.get(key).getAsJsonObject().get("fr").getAsJsonObject().get("text") == null) {
                    LOGGER.error("JSON Object {} is invalid. Please check the syntax.", key);
                    System.exit(1);
                }
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
        private final String language;
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
        public Text(String id, String language, String text, String title) {
            this.id = id;
            this.language = language;
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
                    tmpText = tmpText.replaceFirst(regex, stMaryClient.getLocationManager().formatLocation(name, this.language));
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
            formattedText.append("╭───────────┈ ➤ ✎ ❌ **").append(title).append("**\n- ");
            this.formatLocations();
            formattedText.append(tmpText).append("\n╰─────────── ·\uFEFF \uFEFF \uFEFF· \uFEFF ·\uFEFF \uFEFF \uFEFF· \uFEFF✦");
            return formattedText.toString();
        }
    }
}
