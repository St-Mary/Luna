package me.aikoo.StMary.core.managers;

import com.google.gson.JsonObject;
import lombok.Getter;
import me.aikoo.StMary.core.bases.TitleBase;
import me.aikoo.StMary.core.utils.JSONFileReaderUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Title manager for players.
 */
public class TitleManager {

    @Getter
    public HashMap<String, TitleBase> titles = new HashMap<>();

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
        ArrayList<JsonObject> jsonObjects = JSONFileReaderUtils.readAllFilesFrom("titles");

        for (JsonObject jsonObject : jsonObjects) {
            String id = jsonObject.get("id").getAsString();
            String icon = jsonObject.get("icon").getAsString();

            TitleBase title = new TitleBase(id, icon);
            title.setName("en", jsonObject.get("name").getAsJsonObject().get("en").getAsString());
            title.setDescription("en", jsonObject.get("description").getAsJsonObject().get("en").getAsString());
            title.setName("fr", jsonObject.get("name").getAsJsonObject().get("fr").getAsString());
            title.setDescription("fr", jsonObject.get("description").getAsJsonObject().get("fr").getAsString());

            this.titles.put(id, title);
        }
    }

    /**
     * Get a title by its id.
     *
     * @param id The id of the title to retrieve.
     * @return The title corresponding to the id, or null if it doesn't exist.
     */
    public TitleBase getTitle(String id) {
        return this.titles.get(id);
    }

    /**
     * Get a title by its name.
     *
     * @param name The name of the title to retrieve.
     * @param language The language to get the title in.
     * @return The title corresponding to the name, or null if it doesn't exist.
     */
    public TitleBase getTitleByName(String name, String language) {
        for (TitleBase title : this.titles.values()) {
            if (title.getName(language).equalsIgnoreCase(name)) {
                return title;
            }
        }

        return null;
    }
}
