package me.aikoo.StMary.core.enums;

import lombok.Getter;

import java.util.HashMap;

/**
 * Represents the type of an in-game object, such as a magical book.
 */
public enum ObjectType {

    MAGICAL_BOOK(new Name("magical_book").setName("fr", "Livre de la Magie").setName("en", "Magical Book")); // You can add more object types here.

    private final Name name;

    /**
     * Creates a new ObjectType with the specified identifier.
     *
     * @param name The Name of the object type.
     */
    ObjectType(Name name) {
        this.name = name;
    }

    public String getName(String language) {
        return name.getName(language);
    }


    private static class Name {

        @Getter
        private final String id;
        private final HashMap<String, String> names = new HashMap<>();

        public Name(String id, String ...names) {
            this.id = id;
        }

        /**
         * Get the name of the location in the specified language.
         *
         * @param language The language to get the name in.
         * @return The name of the location in the specified language.
         */
        public String getName(String language) {
            return names.getOrDefault(language, "No name available.");
        }

        /**
         * Set the name of the location in the specified language.
         *
         * @param language The language to set the name in.
         * @param name     The name of the location in the specified language.
         */
        public Name setName(String language, String name) {
            names.put(language, name);
            return this;
        }
    }
}
