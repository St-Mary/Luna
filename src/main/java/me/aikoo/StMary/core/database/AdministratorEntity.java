package me.aikoo.StMary.core.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "administrators")
public class AdministratorEntity {
    @Id
    @Getter
    @GeneratedValue(generator = "uuid-hibernate-generator")
    @GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Getter
    @Setter
    @Column(name = "discord_id", nullable = false)
    private Long discordId;
}
