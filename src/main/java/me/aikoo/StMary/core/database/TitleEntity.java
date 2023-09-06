package me.aikoo.StMary.core.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a title entity.
 */
@Entity
@Table(name = "titles")
public class TitleEntity {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "title_id", nullable = false)
    private String titleId;

    /**
     * Get the id of the title entity.
     *
     * @return The id of the title.
     */
    public String getTitleId() {
        return titleId;
    }

}
