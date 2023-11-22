package me.aikoo.stmary.discord.core.bases;

import java.util.ArrayList;
import lombok.Getter;
import me.aikoo.stmary.discord.core.abstracts.LocationAbstract;

/** Represents a region in the game world. */
public class RegionBase extends LocationAbstract {

  /** Get the icon associated with the region. */
  @Getter private final String icon = "\uD83C\uDF0D "; // A region icon, you can customize this.

  /** Get the list of towns in the region. */
  @Getter private final ArrayList<TownBase> towns = new ArrayList<>();

  /** Get the list of places in the region. */
  @Getter private final ArrayList<PlaceBase> places = new ArrayList<>();

  /**
   * Creates a new Region instance with the specified name and description.
   *
   * @param id The id of the region.
   */
  public RegionBase(String id) {
    super(id);
  }

  /**
   * Adds a town to the list of towns in the region.
   *
   * @param town The town to add to the region.
   */
  public void addTown(TownBase town) {
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
