package me.aikoo.StMary.core.bases;

import lombok.Getter;
import me.aikoo.StMary.core.abstracts.LocationAbstract;

import java.util.ArrayList;

/**
 * Represents a region in the game world.
 */
public class RegionBase extends LocationAbstract {

    @Getter
    private final String name;

    @Getter
    private final String description;

    @Getter
    private final String icon = "\uD83C\uDF0D "; // A region icon, you can customize this.

    @Getter
    private final ArrayList<TownBasee> towns = new ArrayList<>();

    @Getter
    private final ArrayList<PlaceBase> places = new ArrayList<>();

    /**
     * Creates a new Region instance with the specified name and description.
     *
     * @param name        The name of the region.
     * @param description A brief description of the region.
     */
    public RegionBase(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Adds a town to the list of towns in the region.
     *
     * @param town The town to add to the region.
     */
    public void addTown(TownBasee town) {
        this.towns.add(town);
    }

    /**
     * Adds a place to the list of places in the region.
     *
     * @param place The place to add to the region.
     */
    public void addPlace(PlaceBase place) {
        this.places.add(place);
    }
}
