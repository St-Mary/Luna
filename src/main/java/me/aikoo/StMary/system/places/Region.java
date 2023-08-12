package me.aikoo.StMary.system.places;

import lombok.Getter;

import java.util.ArrayList;

public class Region {
    @Getter
    private final String name;

    @Getter
    private final String icon = "\uD83C\uDF0D";

    @Getter
    private final ArrayList<Town> towns = new ArrayList<>();

    @Getter
    private final ArrayList<Places> places = new ArrayList<>();

    public Region(String name) {
        this.name = name;
    }
}
