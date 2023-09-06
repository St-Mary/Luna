package me.aikoo.stmary.core.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

/**
 * The AdministratorEntity class represents an administrator in the database.
 */
@Getter
@Entity
@Table(name = "administrators")
public class AdministratorEntity {

    /**
     * The id of the administrator.
     */
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    /**
     * The discord id of the administrator.
     * Set the Discord id of the administrator.
     */
    @Setter
    @Column(name = "discord_id", nullable = false)
    private Long discordId;
}
