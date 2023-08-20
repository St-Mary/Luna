package me.aikoo.StMary.database.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

/**
 * Represents a title entity.
 */
@Entity
public class TitleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    private String name;

    /**
     * Sets the name of the title entity.
     *
     * @param name The name of the title.
     */
    public void setName(String name) {
        this.name = name;
    }
}
