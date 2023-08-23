package me.aikoo.StMary.core.bases;

import lombok.Getter;

/**
 * Represents a journey from one Place to another.
 */
public class JourneyBase {
    @Getter
    private final PlaceBase from;
    @Getter
    private final PlaceBase to;
    @Getter
    private final Long time;

    /**
     * Creates a new Journey instance.
     *
     * @param from The starting Place of the journey.
     * @param to   The destination Place of the journey.
     * @param time The time duration of the journey.
     */
    public JourneyBase(PlaceBase from, PlaceBase to, Long time) {
        this.from = from;
        this.to = to;
        this.time = time;
    }
}
