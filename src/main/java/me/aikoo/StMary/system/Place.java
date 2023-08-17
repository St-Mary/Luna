package me.aikoo.StMary.system;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class Place extends Location {
    @Getter
    private final String name;

    @Getter
    private final String description;

    @Getter
    private final String icon = "\uD83D\uDCCD";

    @Getter
    private final Region region;

    @Getter
    @Setter
    private Town town = null;

    @Getter
    private final ArrayList<String> availableMoves = new ArrayList<>();

    public Place(String name, String description, Region region) {
        this.name = name;
        this.description = description;
        this.region = region;
    }

    public boolean isTownPlace() {
        return this.town != null;
    }

    public void setTownPlace(Town town) {
        if (this.town == null) {
            this.setTown(town);
        }
    }

    public void addMove(Place p) {
        this.availableMoves.add(p.getName());
    }
}
