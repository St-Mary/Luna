package me.aikoo.stmary.core.managers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.aikoo.stmary.core.abstracts.LocationAbstract;
import me.aikoo.stmary.core.bases.JourneyBase;
import me.aikoo.stmary.core.bases.PlaceBase;
import me.aikoo.stmary.core.bases.RegionBase;
import me.aikoo.stmary.core.bases.TownBase;
import me.aikoo.stmary.core.utils.JSONFileReaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manages locations, including regions, towns, and places, and their associated data.
 */
public class LocationManager {

    private static final ArrayList<TownBase> towns = new ArrayList<>();
    private static final ArrayList<PlaceBase> places = new ArrayList<>();
    private static final ArrayList<JsonObject> placesObject = new ArrayList<>();
    private static final ArrayList<JsonObject> townsObjects = new ArrayList<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(LocationManager.class);
    private static HashMap<String, RegionBase> regions = new HashMap<>();

    static {
        load();
    }

    /**
     * Gets a location by its name, which can be a region, town, or place.
     *
     * @param name The name of the location to retrieve.
     * @return The corresponding location or null if not found.
     */
    public static LocationAbstract getLocationByName(String name, String language) {
        LocationAbstract location = getRegionByName(name);
        location = (location != null) ? location : getTownByName(name, language);
        location = (location != null) ? location : getPlaceByName(name, language);

        return location;
    }

    /**
     * Gets a location by its id, which can be a region, town, or place.
     *
     * @param id The id of the location to retrieve.
     * @return The corresponding location or null if not found.
     */
    public static LocationAbstract getLocationById(String id) {
        LocationAbstract location = getRegionById(id);
        location = (location != null) ? location : getTownById(id);
        location = (location != null) ? location : getPlaceById(id);

        return location;
    }

    /**
     * Gets a region by its id.
     *
     * @param id The id of the region to retrieve.
     * @return The region object or null if not found.
     */
    public static LocationAbstract getRegionById(String id) {
        return regions.get(id);
    }

    /**
     * Gets a town by its name.
     *
     * @param name The name of the town to retrieve.
     * @return The town object or null if not found.
     */
    public static TownBase getTownByName(String name, String language) {
        return towns.stream().filter(town -> town.getName(language).equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Gets a region by its name.
     *
     * @param name The name of the region to retrieve.
     * @return The region object or null if not found.
     */
    public static RegionBase getRegionByName(String name) {
        return regions.get(name);
    }

    /**
     * Gets a place by its name.
     *
     * @param name The name of the place to retrieve.
     * @return The place object or null if not found.
     */
    public static PlaceBase getPlaceByName(String name, String language) {
        return places.stream().filter(place -> place.getName(language).equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Gets a place by its id.
     *
     * @param id The id of the place to retrieve.
     * @return The place object or null if not found.
     */
    public static PlaceBase getPlaceById(String id) {
        return places.stream().filter(place -> place.getId().equals(id)).findFirst().orElse(null);
    }

    /**
     * Gets a town by its id.
     *
     * @param id The id of the town to retrieve.
     * @return The town object or null if not found.
     */
    public static TownBase getTownById(String id) {
        return towns.stream().filter(town -> town.getId().equals(id)).findFirst().orElse(null);
    }

    /**
     * Loads regions from JSON files and creates corresponding region objects.
     *
     * @return A map of regions with their names as keys and corresponding region objects as values.
     */
    private static HashMap<String, RegionBase> loadRegions() {
        List<JsonObject> regions = JSONFileReaderUtils.readAllFilesFrom("places", "regions");
        HashMap<String, RegionBase> regionHashMap = new HashMap<>();

        for (JsonObject regionObject : regions) {
            String id = regionObject.get("id").getAsString();

            // Create the region object
            RegionBase region = new RegionBase(id);
            region.setName("en", regionObject.get("name").getAsJsonObject().get("en").getAsString());
            region.setName("fr", regionObject.get("name").getAsJsonObject().get("fr").getAsString());
            region.setDescription("en", regionObject.get("description").getAsJsonObject().get("en").getAsString());
            region.setDescription("fr", regionObject.get("description").getAsJsonObject().get("fr").getAsString());

            // Load towns for the region
            loadTowns(region, regionObject);

            // Load places for the region
            loadPlaces(region, regionObject);

            // Add the region to the map of regions
            regionHashMap.put(id, region);
        }

        return regionHashMap;
    }

    /**
     * Loads towns for a region from the JSON object of the region.
     *
     * @param region       The region object to which to add towns.
     * @param regionObject The JSON object of the region containing town information.
     */
    private static void loadTowns(RegionBase region, JsonObject regionObject) {
        regionObject.get("towns").getAsJsonArray().forEach(townObj -> {
            String townId = townObj.getAsString();
            JsonObject townObject = findTownObject(townId, region.getId());

            if (townObject == null) {
                LOGGER.error("Town " + townId + " not found!");
                System.exit(1);
            }

            JsonObject entryPointObject = findEntryPointObject(townObject);

            if (entryPointObject == null) {
                LOGGER.error("Entry point " + townObject.get("entryPoint").getAsString() + " not found!");
                System.exit(1);
            }

            // Create the entry point object
            PlaceBase entryPoint = new PlaceBase(entryPointObject.get("id").getAsString(), region);
            entryPoint.setName("en", entryPointObject.get("name").getAsJsonObject().get("en").getAsString());
            entryPoint.setName("fr", entryPointObject.get("name").getAsJsonObject().get("fr").getAsString());
            entryPoint.setDescription("en", entryPointObject.get("description").getAsJsonObject().get("en").getAsString());
            entryPoint.setDescription("fr", entryPointObject.get("description").getAsJsonObject().get("fr").getAsString());

            // Create the town object
            TownBase town = new TownBase(townObject.get("id").getAsString(), region, entryPoint);
            town.setName("en", townObject.get("name").getAsJsonObject().get("en").getAsString());
            town.setName("fr", townObject.get("name").getAsJsonObject().get("fr").getAsString());
            town.setDescription("en", townObject.get("description").getAsJsonObject().get("en").getAsString());
            town.setDescription("fr", townObject.get("description").getAsJsonObject().get("fr").getAsString());

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
    private static void loadPlaces(LocationAbstract location, JsonObject json) {
        json.get("places").getAsJsonArray().forEach(place -> {
            String placeId = place.getAsString();
            JsonObject placeObject = findPlaceObject(placeId);

            if (placeObject == null) {
                LOGGER.error("Place " + placeId + " not found!");
                System.exit(1);
            }

            // Create the place object
            RegionBase region = (location instanceof RegionBase) ? (RegionBase) location : ((TownBase) location).getRegion();
            PlaceBase p = new PlaceBase(placeObject.get("id").getAsString(), region);
            p.setName("en", placeObject.get("name").getAsJsonObject().get("en").getAsString());
            p.setName("fr", placeObject.get("name").getAsJsonObject().get("fr").getAsString());
            p.setDescription("en", placeObject.get("description").getAsJsonObject().get("en").getAsString());
            p.setDescription("fr", placeObject.get("description").getAsJsonObject().get("fr").getAsString());

            // Add the place to the region or town
            if (location instanceof RegionBase) {
                ((RegionBase) location).addPlace(p);
            } else {
                ((TownBase) location).addPlace(p);
                p.setTown((TownBase) location);
            }

            places.add(p);
        });
    }

    /**
     * Find the JSON object corresponding to the town name in a specific region.
     *
     * @param townId The id of the town to search for.
     * @param region The name of the region in which to search for the town.
     * @return The corresponding JSON object or null if not found.
     */
    private static JsonObject findTownObject(String townId, String region) {
        System.out.println(townId + " " + region);
        return townsObjects.stream()
                .filter(t -> t.get("id").getAsString().equals(townId))
                .filter(t -> t.get("region").getAsString().equalsIgnoreCase(region))
                .findFirst()
                .orElse(null);
    }

    /**
     * Find the JSON object corresponding to the place name.
     *
     * @param placeId The id of the place to search for.
     * @return The corresponding JSON object or null if not found.
     */
    private static JsonObject findPlaceObject(String placeId) {
        return placesObject.stream()
                .filter(p -> p.get("id").getAsString().equalsIgnoreCase(placeId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Find the JSON object corresponding to the entry point name of a town.
     *
     * @param townObject The JSON object of the town containing the entry point name.
     * @return The corresponding JSON object for the entry point or null if not found.
     */
    private static JsonObject findEntryPointObject(JsonObject townObject) {
        String entryPointName = townObject.get("entryPoint").getAsString();
        return placesObject.stream()
                .filter(p -> p.get("id").getAsString().equalsIgnoreCase(entryPointName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Load available movements for each place.
     */
    private static void loadMoves() {
        for (PlaceBase place : places) {
            // Find the JSON object corresponding to the current place
            JsonObject placeObject = findPlaceObject(place.getId());

            // Check if the current place is found
            if (placeObject == null) {
                LOGGER.error("Place " + place.getId() + " not foundd!");
                System.exit(1);
            }

            // Get the list of available movements for this place
            JsonArray availableMoves = placeObject.getAsJsonArray("availableMoves");

            // Iterate through the list of movements and add them to the current place
            availableMoves.forEach(move -> {
                JsonObject moveObject = move.getAsJsonObject();
                String moveId = moveObject.get("id").getAsString();
                Long duration = moveObject.get("duration").getAsLong();

                PlaceBase destination = getPlaceById(moveId);

                // Check if the destination of the movement is found
                if (destination == null) {

                    LOGGER.error("Place " + moveId + " not found!");
                    System.exit(1);
                }

                // Add the movement to the current place
                place.addMove(new JourneyBase(place, destination, duration));
            });
        }
    }

    /**
     * Format a location by replacing its name with its icon if available.
     *
     * @param id The id of the location to format.
     * @return The location name with its icon or "Unknown location" if not found.
     */
    public static String formatLocation(String id, String language) {
        // Get the location object corresponding to the name
        LocationAbstract location = getLocationById(id);

        // If the location is found, return its icon followed by its name; otherwise, return "Unknown location"
        return (location != null) ? location.getIcon() + location.getName(language) : "Unknown Location";
    }

    /**
     * Extract the name of a location from a text string in the format "{location:Name}".
     *
     * @param input The text string from which to extract the location name.
     * @return The extracted location name or null if not found.
     */
    public static String extractLocationName(String input) {
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
    private static void load() {
        // Load JSON objects of places from JSON files
        placesObject.addAll(JSONFileReaderUtils.readAllFilesFrom("places", "places"));

        // Load JSON objects of towns from JSON files
        townsObjects.addAll(JSONFileReaderUtils.readAllFilesFrom("places", "towns"));

        // Load regions
        regions = loadRegions();

        // Load movements between places
        loadMoves();
    }
}
