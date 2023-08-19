package me.aikoo.StMary.system;

import lombok.Getter;

public class Object {
    @Getter
    private final String id;
    @Getter
    private final String name;
    @Getter
    private final String icon;
    @Getter
    private final ObjectType type;
    @Getter
    private final String description;

    public Object(String id, String name, String icon, ObjectType type, String description) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.type = type;
        this.description = description;
    }
}
