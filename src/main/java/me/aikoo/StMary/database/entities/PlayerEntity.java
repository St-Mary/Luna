package me.aikoo.StMary.database.entities;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.system.Object;
import me.aikoo.StMary.system.Title;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigInteger;
import java.util.*;

@Entity
@Table(name = "players")
public class PlayerEntity {

    @Id
    @Getter
    @GeneratedValue(generator = "uuid-hibernate-generator")
    @GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Getter
    @Setter
    @Column(name = "discord_id", nullable = false)
    private Long discordId;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "player_titles", joinColumns = @JoinColumn(name = "player_id"))
    @Column(name = "title")
    private List<TitleEntity> titles;

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

    @Setter
    @Column(name = "magical_book", nullable = false)
    private String magicalBook;

    /**
     * Get the current title of the player.
     *
     * @param client The StMaryClient instance.
     * @return The current title of the player.
     */
    public Title getCurrentTitle(StMaryClient client) {
        return client.getTitleManager().getTitle(this.currentTitle);
    }

    /**
     * Get all titles owned by the player.
     *
     * @param client The StMaryClient instance.
     * @return A HashMap of title names and Title objects.
     */
    @Transactional
    public HashMap<String, Title> getTitles(StMaryClient client) {
        HashMap<String, Title> titles = new HashMap<>();
        for (TitleEntity title : this.titles) {
            titles.put(title.getName(), client.getTitleManager().getTitle(title.getName()));
        }
        return titles;
    }

    /**
     * Add a title to the player's collection of titles.
     *
     * @param titleName The name of the title to add.
     * @param client    The StMaryClient instance.
     */
    public void addTitle(String titleName, StMaryClient client) {
        TitleEntity titleEntity = new TitleEntity();
        titleEntity.setName(titleName);
        client.getDatabaseManager().save(titleEntity);
        this.titles.add(titleEntity);
    }

    /**
     * Get the magical book owned by the player.
     *
     * @param client The StMaryClient instance.
     * @return The magical book owned by the player.
     */
    public Object getMagicalBook(StMaryClient client) {
        return client.getObjectManager().getObject(this.magicalBook);
    }

    /**
     * Set the titles of the player.
     *
     * @param strings The list of title names.
     * @param client  The StMaryClient instance.
     */
    public void setTitles(List<String> strings, StMaryClient client) {
        ArrayList<TitleEntity> titles = new ArrayList<>();
        for (String name : strings) {
            TitleEntity titleEntity = new TitleEntity();
            titleEntity.setName(name);
            titles.add(titleEntity);
            client.getDatabaseManager().save(titleEntity);
        }

        this.titles = titles;
    }
}
