package me.aikoo.StMary.system.places.regions;

import me.aikoo.StMary.system.places.Region;
import me.aikoo.StMary.system.places.oraland.Talon;

public class Oraland extends Region {
    public Oraland() {
        super("Oraland");

        this.getTowns().add(new Talon(this));
    }
}
