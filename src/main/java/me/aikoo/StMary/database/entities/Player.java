package me.aikoo.StMary.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.system.Title;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "players")
public class Player {

    @Id
    @Getter
    @GeneratedValue(generator = "uuid-hibernate-generator")
    @GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Getter
    @Setter
    @Column(name = "discord_id", nullable = false)
    private Long discordId;

    @Setter
    @Getter
    @ElementCollection
    @Column(name = "titles")
    private List<String> titles;

    @Setter
    @Column(name = "current_title", nullable = false)
    private String currentTitle;

    @Getter
    @Setter
    @Column(name = "current_location_region", nullable = false)
    private String currentLocationRegion;

    @Getter
    @Setter
    @Column(name = "current_location_town")
    private String currentLocationTown;

    @Getter
    @Setter
    @Column(name = "current_location_place", nullable = false)
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
    @Column(name = "money", columnDefinition = "bigint default 0", nullable = false)
    private BigInteger money;

    @Getter
    @Setter
    @Column(name = "creation_timestamp", nullable = false)
    private Date creationTimestamp;

    public Title getCurrentTitle(StMaryClient client) {
        return client.getTitleManager().getTitle(this.currentTitle);
    }

    public HashMap<String, Title> getTitles(StMaryClient client) {
        HashMap<String, Title> titles = new HashMap<>();
        for (String title : this.titles) {
            titles.put(title, client.getTitleManager().getTitle(title));
        }
        return titles;
    }
}
