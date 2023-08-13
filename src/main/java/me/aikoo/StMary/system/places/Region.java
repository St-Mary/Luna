package me.aikoo.StMary.system.places;

import lombok.Getter;

import java.util.ArrayList;

public class Region {
    @Getter
    private final String name;

    @Getter
    private final String description;

    @Getter
    private final String icon = "\uD83C\uDF0D";

    @Getter
    private final ArrayList<Town> towns = new ArrayList<>();

    @Getter
    private final ArrayList<Place> places = new ArrayList<>();

    public Region(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void addTown(Town town) {
        this.towns.add(town);
    }

    public void addPlace(Place place) {
        this.places.add(place);
    }
}
