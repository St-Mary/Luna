package me.aikoo.StMary.system;

import lombok.Getter;

public class Move {
    @Getter
    private final Place from;
    @Getter
    private final Place to;
    @Getter
    private final Long time;

    public Move(Place from, Place to, Long time) {
        this.from = from;
        this.to = to;
        this.time = time;
    }
}
