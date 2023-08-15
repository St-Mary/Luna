package me.aikoo.StMary.core.managers;

import com.google.gson.JsonObject;
import me.aikoo.StMary.core.JSONFileReader;
import me.aikoo.StMary.system.Location;
import me.aikoo.StMary.system.Place;
import me.aikoo.StMary.system.Region;
import me.aikoo.StMary.system.Town;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class LocationManager {

    private HashMap<String, Region> regions = new HashMap<>();
    private final ArrayList<Town> towns = new ArrayList<>();
    private final ArrayList<Place> places = new ArrayList<>();

    private final ArrayList<JsonObject> placesObject = new ArrayList<>();
    private final ArrayList<JsonObject> townsObjects = new ArrayList<>();
    private final Logger LOGGER = LoggerFactory.getLogger(LocationManager.class);

    public LocationManager() {
        this.load();
    }

    public Location getLocation(String name) {
        Location location = this.getRegion(name);
        location = (location != null) ? location : this.getTown(name);
        location = (location != null) ? location : this.getPlace(name);

        return location;
    }

    public Town getTown(String name) {
        return this.towns.stream().filter(town -> town.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Region getRegion(String name) {
        return this.regions.get(name);
    }

    public Place getPlace(String name) {
        return this.places.stream().filter(place -> place.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    private HashMap<String, Region> loadRegions() {
        ArrayList<JsonObject> regions = JSONFileReader.readAllFilesFrom("places", "regions");
        HashMap<String, Region> regionHashMap = new HashMap<>();

        for (JsonObject regionObject : regions) {
            String name = regionObject.get("name").getAsString();
            String description = regionObject.get("description").getAsString();

            Region region = new Region(name, description);

            regionObject.get("towns").getAsJsonArray().forEach(town -> {
                String townName = town.getAsString();
                JsonObject townObject = this.townsObjects.stream().filter(t -> t.get("name").getAsString().equalsIgnoreCase(townName)).findFirst().orElse(null);

                if (townObject == null) {
                    LOGGER.error("Town " + townName + " not found!");
                    return;
                }

                if (!townObject.get("region").getAsString().equals(region.getName())) return;

                Town t = new Town(townObject.get("name").getAsString(), townObject.get("description").getAsString(), region);
                townObject.get("places").getAsJsonArray().forEach(place -> {
                    String placeName = place.getAsString();
                    JsonObject placeObject = this.placesObject.stream().filter(p -> p.get("name").getAsString().equalsIgnoreCase(placeName)).findFirst().orElse(null);

                    if (placeObject == null) {
                        LOGGER.error("Place " + placeName + " not found!");
                        return;
                    }

                    Place p = new Place(placeObject.get("name").getAsString(), placeObject.get("description").getAsString(), region);
                    p.setTown(t);
                    t.addPlace(p);
                    this.places.add(p);
                });
                this.towns.add(t);
            });

            regionObject.get("places").getAsJsonArray().forEach(place -> {
                String placeName = place.getAsString();
                JsonObject placeObject = this.placesObject.stream().filter(p -> p.get("name").getAsString().equalsIgnoreCase(placeName)).findFirst().orElse(null);

                if (placeObject == null) {
                    LOGGER.error("Place " + placeName + " not found!");
                    return;
                }

                if (!placeObject.get("region").getAsString().equals(region.getName())) return;

                Place p = new Place(placeObject.get("name").getAsString(), placeObject.get("description").getAsString(), region);
                region.addPlace(p);
                this.places.add(p);
            });
        }

        return regionHashMap;
    }

    private void load() {
        this.placesObject.addAll(JSONFileReader.readAllFilesFrom("places", "places"));
        this.townsObjects.addAll(JSONFileReader.readAllFilesFrom("places", "towns"));
        this.regions = this.loadRegions();
    }
}
