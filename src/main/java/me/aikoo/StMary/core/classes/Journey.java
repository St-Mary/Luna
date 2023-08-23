package me.aikoo.StMary.core.classes;

import lombok.Getter;

/**
 * Represents a journey from one Place to another.
 */
public class Journey {
    @Getter
    private final Place from;
    @Getter
    private final Place to;
    @Getter
    private final Long time;

    /**
     * Creates a new Journey instance.
     *
     * @param from The starting Place of the journey.
     * @param to   The destination Place of the journey.
     * @param time The time duration of the journey.
     */
    public Journey(Place from, Place to, Long time) {
        this.from = from;
        this.to = to;
        this.time = time;
    }
}
