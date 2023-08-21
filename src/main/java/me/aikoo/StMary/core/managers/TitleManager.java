package me.aikoo.StMary.core.managers;

import com.google.gson.JsonObject;
import lombok.Getter;
import me.aikoo.StMary.core.JSONFileReader;
import me.aikoo.StMary.core.system.Title;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Title manager for players.
 */
public class TitleManager {

    @Getter
    public HashMap<String, Title> titles = new HashMap<>();

    /**
     * Constructor for the TitleManager class.
     * Loads titles from JSON files.
     */
    public TitleManager() {
        this.load();
    }

    /**
     * Load titles from JSON files and add them to the list of titles.
     */
    public void load() {
        ArrayList<JsonObject> jsonObjects = JSONFileReader.readAllFilesFrom("titles");

        for (JsonObject jsonObject : jsonObjects) {
            String name = jsonObject.get("name").getAsString();
            String description = jsonObject.get("description").getAsString();
            String icon = jsonObject.get("icon").getAsString();

            this.titles.put(name, new Title(name, description, icon));
        }
    }

    /**
     * Get a title by its name.
     *
     * @param name The name of the title to retrieve.
     * @return The title corresponding to the name, or null if it doesn't exist.
     */
    public Title getTitle(String name) {
        return this.titles.get(name);
    }
}
