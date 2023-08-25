package me.aikoo.StMary.core.bases;

import lombok.Getter;

import java.util.HashMap;

/**
 * Represents a title that can be assigned to a player.
 */
public class TitleBase {

    @Getter
    private final String id;

    private final HashMap<String, String> names = new HashMap<>();
    private final HashMap<String, String> descriptions = new HashMap<>();

    @Getter
    private final String icon;

    /**
     * Creates a new Title instance with the specified name, description, and icon.
     *
     * @param icon        An icon associated with the title.
     */
    public TitleBase(String id, String icon) {
        this.id = id;
        this.icon = icon;
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
     * @param name     The name of the location in the specified language.
     */
    public void setName(String language, String name) {
        names.put(language, name);
    }

    /**
     * Set the description of the location in the specified language.
     *
     * @param language    The language to set the description in.
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

    /**
     * Formats the title as a string, including its icon and name.
     *
     * @return A formatted string representation of the title.
     */
    public String format(String language) {
        return this.icon + " " + this.getName(language);
    }
}
