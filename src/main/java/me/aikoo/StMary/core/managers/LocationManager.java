package me.aikoo.StMary.core.managers;

import me.aikoo.StMary.commands.AbstractCommand;
import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.system.places.Places;
import me.aikoo.StMary.system.places.Region;
import me.aikoo.StMary.system.places.Town;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class LocationManager {

    private HashMap<String, Region> regions = new HashMap<>();
    private final ArrayList<Town> towns = new ArrayList<>();
    private final ArrayList<Places> places = new ArrayList<>();
    private final Logger LOGGER = LoggerFactory.getLogger(LocationManager.class);

    public LocationManager() {
        this.load();
    }

    public Town getTown(String name) {
        for (Town town : this.towns) {
            if (town.getName().equalsIgnoreCase(name)) {
                return town;
            }
        }

        return null;
    }

    public Places getPlaceFromTown(String townName, String placeName) {
        Town town = this.getTown(townName);
        if (town == null) {
            return null;
        }

        for (Places place : town.getPlaces()) {
            if (place.getName().equalsIgnoreCase(placeName)) {
                return place;
            }
        }

        return null;
    }

    private HashMap<String, Region> loadRegions() {
        HashMap<String, Region> tmpRegions = new HashMap<>();
        Reflections reflections = new Reflections("me.aikoo.StMary.system.places", new org.reflections.scanners.Scanner[0]);
        Set<Class<? extends Region>> classes = reflections.getSubTypesOf(Region.class);
        for (Class<? extends Region> s : classes) {
            try {
                if (Modifier.isAbstract(s.getModifiers()))
                    continue;

                Region c = s.getConstructor().newInstance();
                if (!regions.containsKey(c.getName())) {
                    LOGGER.info("Loaded region: " + c.getName());
                    tmpRegions.put(c.getName(), c);
                }
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        return tmpRegions;
    }

    private void load() {
        this.regions = this.loadRegions();

        for (Region region : this.regions.values()) {
            this.towns.addAll(region.getTowns());
            this.places.addAll(region.getPlaces());
        }
    }
}
