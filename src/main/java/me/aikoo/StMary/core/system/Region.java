package me.aikoo.StMary.core.system;

import lombok.Getter;

import java.util.ArrayList;

/**
 * Represents a region in the game world.
 */
public class Region extends Location {

    @Getter
    private final String name;

    @Getter
    private final String description;

    @Getter
    private final String icon = "\uD83C\uDF0D "; // A region icon, you can customize this.

    @Getter
    private final ArrayList<Town> towns = new ArrayList<>();

    @Getter
    private final ArrayList<Place> places = new ArrayList<>();

    /**
     * Creates a new Region instance with the specified name and description.
     *
     * @param name        The name of the region.
     * @param description A brief description of the region.
     */
    public Region(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Adds a town to the list of towns in the region.
     *
     * @param town The town to add to the region.
     */
    public void addTown(Town town) {
        this.towns.add(town);
    }

    /**
     * Adds a place to the list of places in the region.
     *
     * @param place The place to add to the region.
     */
    public void addPlace(Place place) {
        this.places.add(place);
    }
}
