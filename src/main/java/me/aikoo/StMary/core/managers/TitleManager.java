package me.aikoo.StMary.core.managers;

import com.google.gson.JsonObject;
import lombok.Getter;
import me.aikoo.StMary.core.JSONFileReader;
import me.aikoo.StMary.system.Title;

import java.util.ArrayList;
import java.util.HashMap;

public class TitleManager {

    @Getter
    public HashMap<String, Title> titles = new HashMap<>();

    public TitleManager() {
        this.load();
    }

    public void load() {
        ArrayList<JsonObject> jsonObjects = JSONFileReader.readAllFilesFrom("titles");

        for (JsonObject jsonObject : jsonObjects) {
            System.out.println(jsonObject);
            String name = jsonObject.get("name").getAsString();
            String description = jsonObject.get("description").getAsString();
            String icon = jsonObject.get("icon").getAsString();

            this.titles.put(name, new Title(name, description, icon));
        }
    }

    public Title getTitle(String name) {
        return this.titles.get(name);
    }
}
