package me.aikoo.StMary.system;

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
}
