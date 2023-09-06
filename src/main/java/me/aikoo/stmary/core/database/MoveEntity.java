package me.aikoo.stmary.core.database;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

/**
 * The MoveEntity class represents a move in the database.
 */
@Entity
@Table(name = "moves")
public class MoveEntity {

    /**
     * The id of the move.
     */
    @Getter
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    /**
     * The starting location of the move.
     */
    private String fromValue;

    /**
     * The destination location of the move.
     */
    private String toValue;

    /**
     * Get the time of the move.
     * Set the time of the move.
     */
    @Getter
    @Setter
    private Long time;

    /**
     * Get the start of the move.
     * Set the start of the move.
     */
    @Getter
    @Setter
    private Long start;

    /**
     * Get the end of the move.
     * Set the end of the move.
     */
    @Getter
    @Setter
    private UUID playerId;

    /**
     * Get the starting location of the move.
     *
     * @return The starting location of the move.
     */
    public String getFrom() {
        return this.fromValue;
    }

    /**
     * Set the starting location of the move.
     *
     * @param from The starting location of the move.
     */
    public void setFrom(String from) {
        this.fromValue = from;
    }

    /**
     * Get the destination location of the move.
     *
     * @return The destination location of the move.
     */
    public String getTo() {
        return this.toValue;
    }

    /**
     * Set the destination location of the move.
     *
     * @param to The destination location of the move.
     */
    public void setTo(String to) {
        this.toValue = to;
    }
}
