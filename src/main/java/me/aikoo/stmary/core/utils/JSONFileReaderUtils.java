package me.aikoo.stmary.core.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This class provides utility methods for reading JSON files. */
public class JSONFileReaderUtils {

  static Logger LOGGER = LoggerFactory.getLogger(JSONFileReaderUtils.class);

  /**
   * Reads all JSON files from the specified directory and subdirectory.
   *
   * @param dir The main directory containing JSON files.
   * @param subDir The subdirectory within the main directory.
   * @return A list of JSON objects read from the files.
   */
  public static List<JsonObject> readAllFilesFrom(String dir, String subDir) {
    String path = "/json/" + dir + "/" + subDir;
    return readFiles(path);
  }

  /**
   * Reads all JSON files from the specified directory.
   *
   * @param dir The directory containing JSON files.
   * @return A list of JSON objects read from the files.
   */
  public static List<JsonObject> readAllFilesFrom(String dir) {
    String path = "/json/" + dir;
    return readFiles(path);
  }

  /**
   * Reads JSON files from the specified path.
   *
   * @param path The path to the directory containing JSON files.
   * @return A list of JSON objects read from the files.
   */
  private static List<JsonObject> readFiles(String path) {
    Gson gson = new Gson();
    List<JsonObject> objects = new ArrayList<>();
    List<String> jsonFiles =
            Stream.of(getResourceFolderFiles(path)).filter(f -> Files.isRegularFile(f.toPath())).map(f -> path + "/" + f.getName()).toList();

    for (String file : jsonFiles) {
      try (Reader reader =
          new BufferedReader(
              new InputStreamReader(
                      JSONFileReaderUtils.class.getResource(file).openStream(), StandardCharsets.UTF_8))) {
        JsonObject object = gson.fromJson(reader, JsonObject.class);
        objects.add(object);
      } catch (IOException e) {
        LOGGER.error("Failed to read JSON file: " + file + " (" + e.getMessage() + ")");
      }
    }

    return objects;
  }

  private static File[] getResourceFolderFiles(String folder) {
    URL url = JSONFileReaderUtils.class.getResource(folder);
    return new File(url.getPath()).listFiles();
  }
}
