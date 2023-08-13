package me.aikoo.StMary;

import me.aikoo.StMary.core.JSONFileReader;
import me.aikoo.StMary.core.managers.LocationManager;

public class Main {
    public static void main(String[] args) {
        //new StMaryClient();
        LocationManager locationManager = new LocationManager();
        System.out.println(locationManager.getTown("Talon").getPlace("Place du Griffon Marin").getDescription());
    }
}