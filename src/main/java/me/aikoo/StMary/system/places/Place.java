package me.aikoo.StMary.system.places;

import lombok.Getter;
import lombok.Setter;

public class Place {
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

    public Place(String name, String description, Region region) {
        this.name = name;
        this.description = description;
        this.region = region;
    }

    public void setTownPlace(Town town) {
        if (this.town == null) {
            this.setTown(town);
        }
    }

    public boolean isTownPlace() {
        return this.town != null;
    }
}
