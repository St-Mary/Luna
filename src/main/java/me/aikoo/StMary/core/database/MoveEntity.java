package me.aikoo.StMary.core.database;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.uuid.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "moves")
public class MoveEntity {
    @Id
    @Getter
    @GeneratedValue(generator = "uuid-hibernate-generator")
    @GenericGenerator(name = "uuid-hibernate-generator", type = UuidGenerator.class)
    private UUID id;

    private String fromValue;

    private String toValue;

    @Getter
    @Setter
    private Long time;

    @Getter
    @Setter
    private Long start;

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
