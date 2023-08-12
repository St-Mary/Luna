package me.aikoo.StMary.system.places;

import lombok.Getter;

import java.util.ArrayList;

public class Town {
    @Getter
    private final String name;

    @Getter
    private final String description;

    @Getter
    private final String icon = "\uD83C\uDFD8\uFE0F";

    @Getter
    private final Region region;

    @Getter
    private final ArrayList<Places> places = new ArrayList<>();

    public Town(String name, String description, Region region) {
        this.name = name;
        this.description = description;
        this.region = region;
    }
}
