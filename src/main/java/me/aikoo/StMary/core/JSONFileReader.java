package me.aikoo.StMary.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.reflections.Reflections;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class JSONFileReader {
    public static ArrayList<JsonObject> readAllFilesFrom(String dir, String subDir) {
        Gson gson = new Gson();
        ArrayList<JsonObject> objects = new ArrayList<>();

        try {
            String path = "src/main/resources/json/" + dir + "/" + subDir;
            List<String> jsonFiles = Files.list(Path.of(path))
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .toList();

            for (String file : jsonFiles) {
                Reader reader = Files.newBufferedReader(Path.of(file), StandardCharsets.UTF_8);
                JsonObject object = gson.fromJson(reader, JsonObject.class);
                objects.add(object);
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return objects;
    }
}
