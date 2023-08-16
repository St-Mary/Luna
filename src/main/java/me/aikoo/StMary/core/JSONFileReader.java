package me.aikoo.StMary.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JSONFileReader {
    public static ArrayList<JsonObject> readAllFilesFrom(String dir, String subDir) {
        String path = "src/main/resources/json/" + dir + "/" + subDir;
        return readFiles(path);
    }

    public static ArrayList<JsonObject> readAllFilesFrom(String dir) {
        String path = "src/main/resources/json/" + dir;
        return readFiles(path);
    }

    private static ArrayList<JsonObject> readFiles(String path) {
        Gson gson = new Gson();
        ArrayList<JsonObject> objects = new ArrayList<>();
        try {
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
