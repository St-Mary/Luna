package me.aikoo.StMary.system;

import lombok.Getter;

import java.util.ArrayList;

public class Town extends Location {
    @Getter
    private final String name;

    @Getter
    private final String description;

    @Getter
    private final String icon = "\uD83C\uDFD8\uFE0F ";

    @Getter
    private final Region region;

    @Getter
    private final ArrayList<Place> places = new ArrayList<>();

    @Getter
    private final Place entryPoint;

    public Town(String name, String description, Region region, Place entryPoint) {
        this.name = name;
        this.description = description;
        this.region = region;
        this.entryPoint = entryPoint;
    }

    public void addPlace(Place place) {
        this.places.add(place);
    }

    public Place getPlace(String name) {
        return this.places.stream().filter(place -> place.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
