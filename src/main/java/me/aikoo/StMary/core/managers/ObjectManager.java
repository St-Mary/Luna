package me.aikoo.StMary.core.managers;

import com.google.gson.JsonObject;
import me.aikoo.StMary.core.JSONFileReader;
import me.aikoo.StMary.core.system.Object;
import me.aikoo.StMary.core.system.ObjectType;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Manages the creation and retrieval of game objects.
 */
public class ObjectManager {
    // Stores objects using their ID as the key
    private final HashMap<String, Object> objects = new HashMap<>();

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
    public Object getObject(String id) {
        return objects.get(id);
    }

    /**
     * Retrieves an object using its name (case-insensitive).
     *
     * @param name The name of the object to retrieve.
     * @return The object corresponding to the name or null if not found.
     */
    public Object getObjectByName(String name) {
        name = name.toLowerCase();
        for (Object object : objects.values()) {
            if (object.getName().toLowerCase().equalsIgnoreCase(name)) {
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
        ArrayList<JsonObject> objects = JSONFileReader.readAllFilesFrom("objects");

        // Iterate through each JSON object and add them to the manager
        for (JsonObject object : objects) {
            String id = object.get("id").getAsString();
            String name = object.get("name").getAsString();
            String icon = object.get("icon").getAsString();
            ObjectType type = ObjectType.valueOf(object.get("type").getAsString().toUpperCase());
            String description = object.get("description").getAsString();

            this.objects.put(id, new Object(id, name, icon, type, description));
        }
    }
}
