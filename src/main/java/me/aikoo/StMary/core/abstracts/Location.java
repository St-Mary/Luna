package me.aikoo.StMary.core.abstracts;

import me.aikoo.StMary.core.classes.Place;
import me.aikoo.StMary.core.classes.Region;
import me.aikoo.StMary.core.classes.Town;

/**
 * Represents a location in the game world.
 */
public abstract class Location {

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
        if (this instanceof Region) {
            type = "Région";
        } else if (this instanceof Town) {
            type = "Ville";
        } else if (this instanceof Place) {
            type = "Lieu";
        }

        return type;
    }
}
