package me.aikoo.StMary.core.bases;

import lombok.Getter;
import me.aikoo.StMary.core.abstracts.LocationAbstract;

import java.util.ArrayList;

/**
 * Represents a town in the game world.
 */
public class TownBase extends LocationAbstract {

    @Getter
    private final String icon = "\uD83C\uDFD8\uFE0F "; // A town icon, you can customize this.

    @Getter
    private final RegionBase region;

    @Getter
    private final ArrayList<PlaceBase> places = new ArrayList<>();

    @Getter
    private final PlaceBase entryPoint;

    /**
     * Creates a new Town instance with the specified name, description, region, and entry point.
     *
     * @param id          The id of the town.
     * @param region      The region to which the town belongs.
     * @param entryPoint  The entry point of the town.
     */
    public TownBase(String id, RegionBase region, PlaceBase entryPoint) {
        super(id);
        this.region = region;
        this.entryPoint = entryPoint;
    }

    /**
     * Adds a place to the list of places in the town.
     *
     * @param place The place to add to the town.
     */
    public void addPlace(PlaceBase place) {
        this.places.add(place);
    }

    /**
     * Retrieves a place in the town by its name.
     *
     * @param name The name of the place to retrieve.
     * @return The Place object with the specified name, or null if not found.
     */
    public PlaceBase getPlace(String name, String language) {
        return this.places.stream().filter(place -> place.getName(language).equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
