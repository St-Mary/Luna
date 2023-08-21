package me.aikoo.StMary.core.system;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * Represents a place within a region or town in the game world.
 */
public class Place extends Location {

    @Getter
    private final String name;

    @Getter
    private final String description;

    @Getter
    private final String icon = "\uD83D\uDCCD"; // You can customize this icon.

    @Getter
    private final Region region;

    @Getter
    @Setter
    private Town town = null;

    @Getter
    private final ArrayList<Journey> availableMoves = new ArrayList<>();

    /**
     * Creates a new Place instance with the specified name, description, and region.
     *
     * @param name        The name of the place.
     * @param description A brief description of the place.
     * @param region      The region to which the place belongs.
     */
    public Place(String name, String description, Region region) {
        this.name = name;
        this.description = description;
        this.region = region;
    }

    /**
     * Adds a move to the list of available moves from this place.
     *
     * @param move The move to add.
     */
    public void addMove(Journey move) {
        this.availableMoves.add(move);
    }

    /**
     * Retrieves a move by its name from the list of available moves.
     *
     * @param name The name of the move to retrieve.
     * @return The Move instance with the specified name, or null if not found.
     */
    public Journey getMove(String name) {
        return this.availableMoves.stream().filter(move -> move.getTo().getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Check if it's a town place.
     * @return true if it's a town place, false otherwise.
     */
    public boolean isTownPlace() {
        return this.town != null;
    }
}
