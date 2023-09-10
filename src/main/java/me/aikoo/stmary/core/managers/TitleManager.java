package me.aikoo.stmary.core.managers;

import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import me.aikoo.stmary.core.bases.TitleBase;
import me.aikoo.stmary.core.utils.JSONFileReaderUtils;

/** Title manager for players. */
public class TitleManager {

  @Getter public static Map<String, TitleBase> titles = new HashMap<>();

  static {
    load();
  }

  /** Load titles from JSON files and add them to the list of titles. */
  public static void load() {
    List<JsonObject> jsonObjects = JSONFileReaderUtils.readAllFilesFrom("titles");

    for (JsonObject jsonObject : jsonObjects) {
      String id = jsonObject.get("id").getAsString();
      String icon = jsonObject.get("icon").getAsString();

      TitleBase title = new TitleBase(id, icon);
      title.setName("en", jsonObject.get("name").getAsJsonObject().get("en").getAsString());
      title.setDescription(
          "en", jsonObject.get("description").getAsJsonObject().get("en").getAsString());
      title.setName("fr", jsonObject.get("name").getAsJsonObject().get("fr").getAsString());
      title.setDescription(
          "fr", jsonObject.get("description").getAsJsonObject().get("fr").getAsString());

      titles.put(id, title);
    }
  }

  /**
   * Get a title by its id.
   *
   * @param id The id of the title to retrieve.
   * @return The title corresponding to the id, or null if it doesn't exist.
   */
  public static TitleBase getTitle(String id) {
    return titles.get(id);
  }

  /**
   * Get a title by its name.
   *
   * @param name The name of the title to retrieve.
   * @param language The language to get the title in.
   * @return The title corresponding to the name, or null if it doesn't exist.
   */
  public static TitleBase getTitleByName(String name, String language) {
    for (TitleBase title : titles.values()) {
      if (title.getName(language).equalsIgnoreCase(name)) {
        return title;
      }
    }

    return null;
  }
}
