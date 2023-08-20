package me.aikoo.StMary.system;

import lombok.Getter;

public enum ObjectType {
    MAGICAL_BOOK("Livre de la Magie");

    @Getter
    private final String id;

    ObjectType(String id) {
        this.id = id;
    }
}
