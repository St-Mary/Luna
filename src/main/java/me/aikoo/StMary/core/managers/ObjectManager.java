package me.aikoo.StMary.core.managers;

import com.google.gson.JsonObject;
import me.aikoo.StMary.core.bases.ObjectBase;
import me.aikoo.StMary.core.enums.ObjectType;
import me.aikoo.StMary.core.utils.JSONFileReaderUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Manages the creation and retrieval of game objects.
 */
public class ObjectManager {
    // Stores objects using their ID as the key
    private final HashMap<String, ObjectBase> objects = new HashMap<>();

    /**
     * Constructor for the ObjectManager class. Loads objects from JSON files at startup.
     */
    public ObjectManager() {
        load();
    }

    /**
     * Retrieves an object using its ID.
     *
     * @param id The ID of the object to retrieve.
     * @return The object corresponding to the ID or null if not found.
     */
    public ObjectBase getObject(String id) {
        return objects.get(id);
    }

    /**
     * Retrieves an object using its name (case-insensitive).
     *
     * @param name The name of the object to retrieve.
     * @return The object corresponding to the name or null if not found.
     */
    public ObjectBase getObjectByName(String name, String language) {
        name = name.toLowerCase();
        for (ObjectBase object : objects.values()) {
            if (object.getName(language).toLowerCase().equalsIgnoreCase(name)) {
                return object;
            }
        }
        return null;
    }

    /**
     * Loads object data from JSON files.
     */
    private void load() {
        // Load objects from JSON files
        ArrayList<JsonObject> objects = JSONFileReaderUtils.readAllFilesFrom("items");

        // Iterate through each JSON object and add them to the manager
        for (JsonObject object : objects) {
            String id = object.get("id").getAsString();
            String icon = object.get("icon").getAsString();
            ObjectType type = ObjectType.valueOf(object.get("type").getAsString().toUpperCase());

            ObjectBase objectBase = new ObjectBase(id, icon, type);
            objectBase.setName("en", object.get("name").getAsJsonObject().get("en").getAsString());
            objectBase.setDescription("en", object.get("description").getAsJsonObject().get("en").getAsString());
            objectBase.setName("fr", object.get("name").getAsJsonObject().get("fr").getAsString());
            objectBase.setDescription("fr", object.get("description").getAsJsonObject().get("fr").getAsString());

            this.objects.put(id, objectBase);
        }
    }
}
