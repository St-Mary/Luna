package me.aikoo.StMary.core.classes;

import lombok.Getter;
import me.aikoo.StMary.core.abstracts.Location;

import java.util.ArrayList;

/**
 * Represents a town in the game world.
 */
public class Town extends Location {

    @Getter
    private final String name;

    @Getter
    private final String description;

    @Getter
    private final String icon = "\uD83C\uDFD8\uFE0F "; // A town icon, you can customize this.

    @Getter
    private final Region region;

    @Getter
    private final ArrayList<Place> places = new ArrayList<>();

    @Getter
    private final Place entryPoint;

    /**
     * Creates a new Town instance with the specified name, description, region, and entry point.
     *
     * @param name        The name of the town.
     * @param description A brief description of the town.
     * @param region      The region to which the town belongs.
     * @param entryPoint  The entry point of the town.
     */
    public Town(String name, String description, Region region, Place entryPoint) {
        this.name = name;
        this.description = description;
        this.region = region;
        this.entryPoint = entryPoint;
    }

    /**
     * Adds a place to the list of places in the town.
     *
     * @param place The place to add to the town.
     */
    public void addPlace(Place place) {
        this.places.add(place);
    }

    /**
     * Retrieves a place in the town by its name.
     *
     * @param name The name of the place to retrieve.
     * @return The Place object with the specified name, or null if not found.
     */
    public Place getPlace(String name) {
        return this.places.stream().filter(place -> place.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
