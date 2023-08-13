package me.aikoo.StMary.core.managers;

import com.google.gson.JsonObject;
import me.aikoo.StMary.core.JSONFileReader;
import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.system.places.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextManager {

    private HashMap<String, JsonObject> texts = new HashMap<>();
    private final StMaryClient stMaryClient;

    public TextManager(StMaryClient stMaryClient) {
        this.stMaryClient = stMaryClient;
        this.load();
    }

    public String generateScene(String title, String text) {
        String regex = "\\{location:([^{}]+)\\}";
        String formattedText = text
                .replaceAll("\n\n", "\n- ");
                // .replaceAll(regex, this.formatLocation(text));

        Pattern pattern = Pattern.compile(regex);
        if ((pattern.matcher(formattedText).find())) {
            while (pattern.matcher(formattedText).find()) {
                formattedText = formattedText.replaceFirst(regex, this.formatLocation(formattedText));
            }
        }

        return "╭───────────┈ ➤ ✎ **" + title + "**\n- " + formattedText + "\n╰─────────── ·\uFEFF \uFEFF \uFEFF· \uFEFF ·\uFEFF \uFEFF \uFEFF· \uFEFF✦";
    }

    private String formatLocation(String text) {
        String name = this.extractLocationName(text);
        Location location = this.stMaryClient.getLocationManager().getLocation(name);
        return (location != null) ? location.getIcon() + "**" + location.getName() + "**" : "Unknown location";
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
        return (this.texts.get(name) != null) ?  this.texts.get(name).get("text").getAsString() : null;
    }

    public String getTitle(String name) {
        return (this.texts.get(name) != null) ?  this.texts.get(name).get("title").getAsString() : null;
    }

    private void load() {
        ArrayList<JsonObject> files = JSONFileReader.readAllFilesFrom("text");

        for (JsonObject file : files) {
            String name = file.get("name").getAsString();
            this.texts.put(name, file);
        }
    }
}
