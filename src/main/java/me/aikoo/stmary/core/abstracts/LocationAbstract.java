package me.aikoo.stmary.core.abstracts;

import lombok.Getter;
import me.aikoo.stmary.core.bases.PlaceBase;
import me.aikoo.stmary.core.bases.RegionBase;
import me.aikoo.stmary.core.bases.TownBase;

import java.util.HashMap;

/**
 * Represents a location in the game world.
 */
public abstract class LocationAbstract {

    /**
     * Get the id of the location.
     */
    @Getter
    private final String id;

    private final HashMap<String, String> names = new HashMap<>();
    private final HashMap<String, String> descriptions = new HashMap<>();

    /**
     * Creates a new Location instance with the specified name, description, and icon.
     *
     * @param id The id of the location.
     */
    protected LocationAbstract(String id) {
        this.id = id;
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
     * Get the icon representing the location.
     *
     * @return The icon representing the location.
     */
    public abstract String getIcon();

    /**
     * Get the type of the location.
     * @return The type of the location.
     */
    public String getType() {
        String type = "";
        if (this instanceof RegionBase) {
            type = "Région";
        } else if (this instanceof TownBase) {
            type = "Ville";
        } else if (this instanceof PlaceBase) {
            type = "Lieu";
        }

        return type;
    }
}
