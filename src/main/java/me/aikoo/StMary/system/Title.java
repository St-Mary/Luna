package me.aikoo.StMary.system;

import lombok.Getter;

public class Title {

    @Getter
    private final String name;

    @Getter
    private final String description;

    @Getter
    private final String icon;

    public Title(String name, String description, String icon) {
        this.name = name;
        this.description = description;
        this.icon = icon;
    }
}
