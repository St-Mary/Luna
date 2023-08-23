package me.aikoo.StMary.core.classes;

import lombok.Getter;

/**
 * Represents a title that can be assigned to a player.
 */
public class Title {

    @Getter
    private final String name;

    @Getter
    private final String description;

    @Getter
    private final String icon;

    /**
     * Creates a new Title instance with the specified name, description, and icon.
     *
     * @param name        The name of the title.
     * @param description A brief description of the title.
     * @param icon        An icon associated with the title.
     */
    public Title(String name, String description, String icon) {
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    /**
     * Formats the title as a string, including its icon and name.
     *
     * @return A formatted string representation of the title.
     */
    public String format() {
        return this.icon + " " + this.name;
    }
}
