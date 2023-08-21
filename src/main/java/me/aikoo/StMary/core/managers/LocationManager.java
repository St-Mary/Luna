package me.aikoo.StMary.core.managers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.aikoo.StMary.core.JSONFileReader;
import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.core.system.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manages locations, including regions, towns, and places, and their associated data.
 */
public class LocationManager {

    private final ArrayList<Town> towns = new ArrayList<>();
    private final StMaryClient stMaryClient;
    private final ArrayList<Place> places = new ArrayList<>();
    private final ArrayList<JsonObject> placesObject = new ArrayList<>();
    private final ArrayList<JsonObject> townsObjects = new ArrayList<>();
    private final Logger LOGGER = LoggerFactory.getLogger(LocationManager.class);
    private HashMap<String, Region> regions = new HashMap<>();

    /**
     * Constructor for the LocationManager class.
     *
     * @param stMaryClient The main StMary client.
     */
    public LocationManager(StMaryClient stMaryClient) {
        this.stMaryClient = stMaryClient;
        this.load();
    }

    /**
     * Gets a location by its name, which can be a region, town, or place.
     *
     * @param name The name of the location to retrieve.
     * @return The corresponding location or null if not found.
     */
    public Location getLocation(String name) {
        Location location = this.getRegion(name);
        location = (location != null) ? location : this.getTown(name);
        location = (location != null) ? location : this.getPlace(name);

        return location;
    }

    /**
     * Gets a town by its name.
     *
     * @param name The name of the town to retrieve.
     * @return The town object or null if not found.
     */
    public Town getTown(String name) {
        return this.towns.stream().filter(town -> town.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Gets a region by its name.
     *
     * @param name The name of the region to retrieve.
     * @return The region object or null if not found.
     */
    public Region getRegion(String name) {
        return this.regions.get(name);
    }

    /**
     * Gets a place by its name.
     *
     * @param name The name of the place to retrieve.
     * @return The place object or null if not found.
     */
    public Place getPlace(String name) {
        return this.places.stream().filter(place -> place.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Loads regions from JSON files and creates corresponding region objects.
     *
     * @return A map of regions with their names as keys and corresponding region objects as values.
     */
    private HashMap<String, Region> loadRegions() {
        ArrayList<JsonObject> regions = JSONFileReader.readAllFilesFrom("places", "regions");
        HashMap<String, Region> regionHashMap = new HashMap<>();

        for (JsonObject regionObject : regions) {
            String name = regionObject.get("name").getAsString();
            String description = regionObject.get("description").getAsString();

            // Create the region object
            Region region = new Region(name, description);

            // Load towns for the region
            loadTowns(region, regionObject);

            // Load places for the region
            loadPlaces(region, regionObject);

            // Add the region to the map of regions
            regionHashMap.put(name, region);
        }

        return regionHashMap;
    }

    /**
     * Loads towns for a region from the JSON object of the region.
     *
     * @param region       The region object to which to add towns.
     * @param regionObject The JSON object of the region containing town information.
     */
    private void loadTowns(Region region, JsonObject regionObject) {
        regionObject.get("towns").getAsJsonArray().forEach(townObj -> {
            String townName = townObj.getAsString();
            JsonObject townObject = findTownObject(townName, region.getName());

            if (townObject == null) {
                LOGGER.error("Town " + townName + " not found!");
                return;
            }

            JsonObject entryPointObject = findEntryPointObject(townObject);

            if (entryPointObject == null) {
                LOGGER.error("Entry point " + townObject.get("entryPoint").getAsString() + " not found!");
                return;
            }

            // Create the entry point object
            Place entryPoint = new Place(entryPointObject.get("name").getAsString(), entryPointObject.get("description").getAsString(), region);

            // Create the town object
            Town town = new Town(townObject.get("name").getAsString(), townObject.get("description").getAsString(), region, entryPoint);

            // Load places for the town
            loadPlaces(town, townObject);

            // Add the town to the region
            region.addTown(town);
            towns.add(town);
        });
    }

    /**
     * Loads places for a region or town from the corresponding JSON object.
     *
     * @param location The region or town object to which to add places.
     * @param json     The JSON object containing place information.
     */
    private void loadPlaces(Location location, JsonObject json) {
        json.get("places").getAsJsonArray().forEach(place -> {
            String placeName = place.getAsString();
            JsonObject placeObject = findPlaceObject(placeName);

            if (placeObject == null) {
                LOGGER.error("Place " + placeName + " not found!");
                return;
            }

            // Create the place object
            Region region = (location instanceof Region) ? (Region) location : ((Town) location).getRegion();
            Place p = new Place(placeObject.get("name").getAsString(), placeObject.get("description").getAsString(), region);

            // Add the place to the region or town
            if (location instanceof Region) {
                ((Region) location).addPlace(p);
            } else {
                ((Town) location).addPlace(p);
                p.setTown((Town) location);
            }

            this.places.add(p);
        });
    }

    /**
     * Find the JSON object corresponding to the town name in a specific region.
     *
     * @param townName The name of the town to search for.
     * @param region   The name of the region in which to search for the town.
     * @return The corresponding JSON object or null if not found.
     */
    private JsonObject findTownObject(String townName, String region) {
        return this.townsObjects.stream()
                .filter(t -> t.get("name").getAsString().equalsIgnoreCase(townName))
                .filter(t -> t.get("region").getAsString().equalsIgnoreCase(region))
                .findFirst()
                .orElse(null);
    }

    /**
     * Find the JSON object corresponding to the place name.
     *
     * @param placeName The name of the place to search for.
     * @return The corresponding JSON object or null if not found.
     */
    private JsonObject findPlaceObject(String placeName) {
        return this.placesObject.stream()
                .filter(p -> p.get("name").getAsString().equalsIgnoreCase(placeName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Find the JSON object corresponding to the entry point name of a town.
     *
     * @param townObject The JSON object of the town containing the entry point name.
     * @return The corresponding JSON object for the entry point or null if not found.
     */
    private JsonObject findEntryPointObject(JsonObject townObject) {
        String entryPointName = townObject.get("entryPoint").getAsString();
        return this.placesObject.stream()
                .filter(p -> p.get("name").getAsString().equalsIgnoreCase(entryPointName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Load available movements for each place.
     */
    private void loadMoves() {
        for (Place place : this.places) {
            // Find the JSON object corresponding to the current place
            JsonObject placeObject = findPlaceObject(place.getName());

            // Check if the current place is found
            if (placeObject == null) {
                LOGGER.error("Place " + place.getName() + " not found!");
                continue; // Move on to the next place
            }

            // Get the list of available movements for this place
            JsonArray availableMoves = placeObject.getAsJsonArray("availableMoves");

            // Iterate through the list of movements and add them to the current place
            availableMoves.forEach(move -> {
                JsonObject moveObject = move.getAsJsonObject();
                String moveName = moveObject.get("name").getAsString();
                Long duration = moveObject.get("duration").getAsLong();

                Place destination = getPlace(moveName);

                // Check if the destination of the movement is found
                if (destination == null) {
                    LOGGER.error("Place " + moveName + " not found!");
                    return; // Move on to the next movement
                }

                // Add the movement to the current place
                place.addMove(new Journey(place, destination, duration));
            });
        }
    }

    /**
     * Format a location by replacing its name with its icon if available.
     *
     * @param text The name of the location to format.
     * @return The location name with its icon or "Unknown location" if not found.
     */
    public String formatLocation(String text) {
        // Get the location object corresponding to the name
        Location location = this.stMaryClient.getLocationManager().getLocation(text);

        // If the location is found, return its icon followed by its name; otherwise, return "Unknown location"
        return (location != null) ? location.getIcon() + location.getName() : "Localisation inconnue";
    }

    /**
     * Extract the name of a location from a text string in the format "{location:Name}".
     *
     * @param input The text string from which to extract the location name.
     * @return The extracted location name or null if not found.
     */
    public String extractLocationName(String input) {
        // Regular expression to search for the pattern "{location:Name}"
        String regex = "\\{location:([^{}]+)\\}";

        // Compile the regex into a pattern
        Pattern pattern = Pattern.compile(regex);

        // Create a matcher from the input
        Matcher matcher = pattern.matcher(input);

        // If a match is found, return the extracted name; otherwise, return null
        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    /**
     * Load data related to locations, towns, regions, and movements from JSON files.
     */
    private void load() {
        // Load JSON objects of places from JSON files
        this.placesObject.addAll(JSONFileReader.readAllFilesFrom("places", "places"));

        // Load JSON objects of towns from JSON files
        this.townsObjects.addAll(JSONFileReader.readAllFilesFrom("places", "towns"));

        // Load regions
        this.regions = this.loadRegions();

        // Load movements between places
        this.loadMoves();
    }
}
