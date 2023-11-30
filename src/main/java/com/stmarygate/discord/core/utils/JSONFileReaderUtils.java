package com.stmarygate.discord.core.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
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
    String path = "json" + File.separator + dir + File.separator + subDir;
    return readFiles(path);
  }

  /**
   * Reads all JSON files from the specified directory.
   *
   * @param dir The directory containing JSON files.
   * @return A list of JSON objects read from the files.
   */
  public static List<JsonObject> readAllFilesFrom(String dir) {
    String path = "json" + File.separator + dir;
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
    Map<String, String> jsonFiles = getResourceFolderFiles(path);

    for (Map.Entry<String, String> entry : jsonFiles.entrySet()) {
      JsonObject object = gson.fromJson(entry.getValue(), JsonObject.class);
      objects.add(object);
    }

    return objects;
  }

  private static Map<String, String> getResourceFolderFiles(String folder) {
    Map<String, String> result = new HashMap<>();
    JarInputStream jarInputStream = null;

    if (checkIfItsJar()) {
      try {
        jarInputStream =
            new JarInputStream(
                new FileInputStream(
                    new File(
                            JSONFileReaderUtils.class
                                .getProtectionDomain()
                                .getCodeSource()
                                .getLocation()
                                .toURI())
                        .getPath()));
        // Read content
        while (true) {
          JarEntry jarEntry = jarInputStream.getNextJarEntry();
          if (jarEntry == null) break;
          String fileName = jarEntry.getName();
          if (fileName.startsWith(folder + "/")) {
            if (!jarEntry.isDirectory()) {
              String fileText = new String(jarInputStream.readAllBytes(), StandardCharsets.UTF_8);
              result.put(fileName, fileText);
            }
          }
        }
      } catch (IOException ignore) {
        // Ignore this exception and just return false
      } catch (URISyntaxException e) {
        LOGGER.error("Error while reading files from resource folder", e);
      } finally {
        try {
          if (jarInputStream != null) jarInputStream.close();
        } catch (IOException ignored) {
          // Ignore this exception and just return result
        }
      }
    } else {
      try {
        // Chargement des ressources depuis l'environnement de développement
        URL resource = JSONFileReaderUtils.class.getClassLoader().getResource(folder);
        if (resource != null) {
          Path path = Paths.get(resource.toURI());
          Files.walk(path)
              .filter(Files::isRegularFile)
              .forEach(
                  filePath -> {
                    try {
                      String fileName = filePath.toString().replace(path + File.separator, "");
                      InputStream fileContent = Files.newInputStream(filePath);
                      String fileText =
                          new String(fileContent.readAllBytes(), StandardCharsets.UTF_8);
                      result.put(fileName, fileText);
                    } catch (IOException e) {
                      LOGGER.error("Error while reading files from resource folder", e);
                    }
                  });
        } else {
          throw new FileNotFoundException("Resource folder not found");
        }
      } catch (IOException | URISyntaxException e) {
        LOGGER.error("Error while reading files from resource folder", e);
      }
    }

    return result;
  }

  private static boolean checkIfItsJar() {
    return JSONFileReaderUtils.class
        .getResource("JSONFileReaderUtils.class")
        .toString()
        .startsWith("jar");
  }
}
