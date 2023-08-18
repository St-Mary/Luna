package me.aikoo.StMary.core.managers;

import com.google.gson.JsonObject;
import me.aikoo.StMary.core.JSONFileReader;
import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.system.Location;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextManager {

    private final HashMap<String, JsonObject> texts = new HashMap<>();
    private final StMaryClient stMaryClient;

    public TextManager(StMaryClient stMaryClient) {
        this.stMaryClient = stMaryClient;
        this.load();
    }

    public String generateScene(String title, String text) {
        String regex = "\\{location:([^{}]+)\\}";
        String formattedText = text
                .replaceAll("\n\n", "\n- ");

        Pattern pattern = Pattern.compile(regex);
        if ((pattern.matcher(formattedText).find())) {
            while (pattern.matcher(formattedText).find()) {
                String name = this.extractLocationName(formattedText);
                formattedText = formattedText.replaceFirst(regex, this.formatLocation(name));
            }
        }

        return "╭───────────┈ ➤ ✎ **" + title + "**\n- " + formattedText + "\n╰─────────── ·\uFEFF \uFEFF \uFEFF· \uFEFF ·\uFEFF \uFEFF \uFEFF· \uFEFF✦";
    }

    public EmbedBuilder generateErrorEmbed(String title, String text) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("❌ | Une erreur est survenue: " + title);
        embedBuilder.setDescription(text);
        embedBuilder.setColor(0xff0000);
        return embedBuilder;
    }

    public String formatLocation(String text) {
        Location location = this.stMaryClient.getLocationManager().getLocation(text);
        return (location != null) ? location.getIcon() + location.getName() : "Unknown location";
    }

    public String extractLocationName(String input) {
        String regex = "\\{location:([^{}]+)\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    public String getText(String name) {
        return (this.texts.get(name) != null) ? this.texts.get(name).get("text").getAsString() : null;
    }

    public String getTitle(String name) {
        return (this.texts.get(name) != null) ? this.texts.get(name).get("title").getAsString() : null;
    }

    private void load() {
        ArrayList<JsonObject> files = JSONFileReader.readAllFilesFrom("text");

        for (JsonObject file : files) {
            String name = file.get("name").getAsString();
            this.texts.put(name, file);
        }
    }
}
