package me.aikoo.stmary.core.bases;

import java.util.HashMap;
import lombok.Getter;
import me.aikoo.stmary.core.enums.ObjectType;

/** Represents an in-game object. */
public class ObjectBase {

  /** Get the id of the object. */
  @Getter private final String id;

  private final HashMap<String, String> names = new HashMap<>();
  private final HashMap<String, String> descriptions = new HashMap<>();

  /** Get the icon associated with the object. */
  @Getter private final String icon;

  /** Get the type of the object. */
  @Getter private final ObjectType type;

  /**
   * Creates a new Object with the specified attributes.
   *
   * @param id The unique identifier for the object.
   * @param icon The icon associated with the object.
   * @param type The type of the object.
   */
  public ObjectBase(String id, String icon, ObjectType type) {
    this.id = id;
    this.icon = icon;
    this.type = type;
  }

  /**
   * Get the name of the location in the specified language.
   *
   * @param language The language to get the name in.
   * @return The name of the location in the specified language.
   */
  public String getName(String language) {
    return names.getOrDefault(language, "No name available.");
  }

  /**
   * Set the name of the location in the specified language.
   *
   * @param language The language to set the name in.
   * @param name The name of the location in the specified language.
   */
  public void setName(String language, String name) {
    names.put(language, name);
  }

  /**
   * Set the description of the location in the specified language.
   *
   * @param language The language to set the description in.
   * @param description The description of the location in the specified language.
   */
  public void setDescription(String language, String description) {
    descriptions.put(language, description);
  }

  /**
   * Get the description of the location in the specified language.
   *
   * @param language The language to get the description in.
   * @return The description of the location in the specified language.
   */
  public String getDescription(String language) {
    return descriptions.getOrDefault(language, "No description available.");
  }
}