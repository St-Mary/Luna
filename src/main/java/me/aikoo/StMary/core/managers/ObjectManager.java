package me.aikoo.StMary.core.managers;

import com.google.gson.JsonObject;
import me.aikoo.StMary.core.JSONFileReader;
import me.aikoo.StMary.system.Object;
import me.aikoo.StMary.system.ObjectType;

import java.util.ArrayList;
import java.util.HashMap;

public class ObjectManager {
    private final HashMap<String, Object> objects = new HashMap<>();

    public ObjectManager() {
        load();
    }

    public Object getObject(String id) {
        return objects.get(id);
    }

    public Object getObjectByName(String name) {
        for (Object object : objects.values()) {
            if (object.getName().equalsIgnoreCase(name)) {
                return object;
            }
        }
        return null;
    }

    private void load() {
        ArrayList<JsonObject> objects = JSONFileReader.readAllFilesFrom("objects");

        for (JsonObject object : objects) {
            String id = object.get("id").getAsString();
            String name = object.get("name").getAsString();
            String icon = object.get("icon").getAsString();
            ObjectType type = ObjectType.valueOf(object.get("type").getAsString().toUpperCase());
            String description = object.get("description").getAsString();

            this.objects.put(id, new Object(id, name, icon, type, description));
            System.out.println("Loaded object: " + id + " (" + name + ")");
        }
    }
}
