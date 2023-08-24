package me.aikoo.StMary.core.abstracts;

import lombok.Getter;
import me.aikoo.StMary.core.bases.PlaceBase;
import me.aikoo.StMary.core.bases.RegionBase;
import me.aikoo.StMary.core.bases.TownBase;

/**
 * Represents a location in the game world.
 */
public abstract class LocationAbstract {

    @Getter
    private final String id;

    /**
     * Creates a new Location instance with the specified name, description, and icon.
     *
     * @param id The id of the location.
     */
    public LocationAbstract(String id) {
        this.id = id;
    }

    /**
     * Get the name of the location.
     *
     * @return The name of the location.
     */
    public abstract String getName();

    /**
     * Get the description of the location.
     *
     * @return The description of the location.
     */
    public abstract String getDescription();

    /**
     * Get the icon representing the location.
     *
     * @return The icon representing the location.
     */
    public abstract String getIcon();

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
