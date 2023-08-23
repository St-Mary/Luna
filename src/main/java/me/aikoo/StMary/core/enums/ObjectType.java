package me.aikoo.StMary.core.enums;

import lombok.Getter;

/**
 * Represents the type of an in-game object, such as a magical book.
 */
public enum ObjectType {

    MAGICAL_BOOK("Livre de la Magie"); // You can add more object types here.

    @Getter
    private final String id;

    /**
     * Creates a new ObjectType with the specified identifier.
     *
     * @param id The unique identifier for the object type.
     */
    ObjectType(String id) {
        this.id = id;
    }
}
