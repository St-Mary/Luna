package me.aikoo.stmary.core.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a title entity.
 */
@Getter
@Entity
@Table(name = "titles")
public class TitleEntity {

    /**
     * Get the id of the title.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Get the title id of the title.
     * Set the title id of the title.
     */
    @Setter
    @Column(name = "title_id", nullable = false)
    private String titleId;
}
