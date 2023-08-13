package me.aikoo.StMary.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DialectOverride;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;

@Entity
@Table(name = "players")
public class Player {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    @Getter
    @Setter
    @Column(name = "discord_id", nullable = false)
    private Long discordId;

    @Getter
    @Setter
    @ElementCollection
    @Column(name = "titles", nullable = false)
    private HashSet<String> title;

    @Getter
    @Setter
    @Column(name = "current_title", nullable = false)
    private String currentTitle;

    @Getter
    @Setter
    @Column(name = "current_location_region", columnDefinition = "varchar(255) default 'Oraland'", nullable = false)
    private String currentLocationRegion;

    @Getter
    @Setter
    @Column(name = "current_location_town", columnDefinition = "varchar(255) default 'Talon'")
    private String currentLocationTown;

    @Getter
    @Setter
    @Column(name = "current_location_place", columnDefinition = "varchar(255) default 'Place du Griffon Marin'", nullable = false)
    private String currentLocationPlace;

    @Getter
    @Setter
    @Column(name = "level", columnDefinition = "int default 1", nullable = false)
    private Integer level;

    @Getter
    @Setter
    @Column(name = "experience", columnDefinition = "bigint default 0", nullable = false)
    private BigInteger experience;

    @Getter
    @Setter
    @Column(name = "money", nullable = false)
    private BigInteger money;

    @Getter
    @Setter
    @Column(name = "creation_timestamp", nullable = false)
    private Date creationTimestamp;
}
