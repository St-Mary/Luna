package me.aikoo.StMary.database.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "moves")
public class Moves {
    @Id
    @Getter
    @GeneratedValue(generator = "uuid-hibernate-generator")
    @GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
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

    public String getFrom() {
        return this.fromValue;
    }

    public void setFrom(String from) {
        this.fromValue = from;
    }

    public String getTo() {
        return this.toValue;
    }

    public void setTo(String to) {
        this.toValue = to;
    }
}
