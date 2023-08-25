package me.aikoo.StMary.core.bases;

import lombok.Getter;
import me.aikoo.StMary.core.enums.ObjectType;

/**
 * Represents an in-game object.
 */
public class ObjectBase {
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

    /**
     * Creates a new Object with the specified attributes.
     *
     * @param id          The unique identifier for the object.
     * @param name        The name of the object.
     * @param icon        The icon associated with the object.
     * @param type        The type of the object.
     * @param description A brief description of the object.
     */
    public ObjectBase(String id, String name, String icon, ObjectType type, String description) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.type = type;
        this.description = description;
    }
}