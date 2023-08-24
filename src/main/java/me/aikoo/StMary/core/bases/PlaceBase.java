package me.aikoo.StMary.core.bases;

import lombok.Getter;
import lombok.Setter;
import me.aikoo.StMary.core.abstracts.LocationAbstract;

import java.util.ArrayList;

/**
 * Represents a place within a region or town in the game world.
 */
public class PlaceBase extends LocationAbstract {


    @Getter
    private final String icon = "\uD83D\uDCCD"; // You can customize this icon.

    @Getter
    private final RegionBase region;
    @Getter
    private final ArrayList<JourneyBase> availableMoves = new ArrayList<>();
    @Getter
    @Setter
    private TownBase town = null;

    /**
     * Creates a new Place instance with the specified name, description, and region.
     *
     * @param id     The id of the place.
     * @param region The region to which the place belongs.
     */
    public PlaceBase(String id, RegionBase region) {
        super(id);
        this.region = region;
    }

    /**
     * Adds a move to the list of available moves from this place.
     *
     * @param move The move to add.
     */
    public void addMove(JourneyBase move) {
        this.availableMoves.add(move);
    }

    /**
     * Retrieves a move by its name from the list of available moves.
     *
     * @param name The name of the move to retrieve.
     * @return The Move instance with the specified name, or null if not found.
     */
    public JourneyBase getMove(String name) {
        return this.availableMoves.stream().filter(move -> move.getTo().getId().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Check if it's a town place.
     *
     * @return true if it's a town place, false otherwise.
     */
    public boolean isTownPlace() {
        return this.town != null;
    }
}
