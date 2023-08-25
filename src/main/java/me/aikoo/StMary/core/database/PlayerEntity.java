package me.aikoo.StMary.core.database;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import me.aikoo.StMary.core.bases.ObjectBase;
import me.aikoo.StMary.core.bases.TitleBase;
import me.aikoo.StMary.core.bot.StMaryClient;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigInteger;
import java.util.*;

@Entity()
@Table(name = "players")
public class PlayerEntity {

    @Id
    @Getter
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Getter
    @Setter
    @Column(name = "discord_id", nullable = false)
    private Long discordId;

    @Getter
    @Setter
    @Column(name = "language", nullable = false)
    private String language;

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
    public TitleBase getCurrentTitle(StMaryClient client) {
        System.out.println(this.currentTitle);
        return client.getTitleManager().getTitle(this.currentTitle);
    }

    /**
     * Get all titles owned by the player.
     *
     * @param client The StMaryClient instance.
     * @return A HashMap of title names and Title objects.
     */
    @Transactional
    public HashMap<String, TitleBase> getTitles(StMaryClient client) {
        HashMap<String, TitleBase> titles = new HashMap<>();
        for (TitleEntity title : this.titles) {
            titles.put(title.getId(), client.getTitleManager().getTitle(title.getId()));
        }
        return titles;
    }

    /**
     * Add a title to the player's collection of titles.
     *
     * @param titleId The id of the title to add.
     * @param client    The StMaryClient instance.
     */
    public void addTitle(String titleId, StMaryClient client) {
        TitleEntity titleEntity = new TitleEntity();
        titleEntity.setTitleId(titleId);
        client.getDatabaseManager().save(titleEntity);
        this.titles.add(titleEntity);
    }

    /**
     * Get the magical book owned by the player.
     *
     * @param client The StMaryClient instance.
     * @return The magical book owned by the player.
     */
    public ObjectBase getMagicalBook(StMaryClient client) {
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
        for (String id : strings) {
            TitleEntity titleEntity = new TitleEntity();
            titleEntity.setTitleId(id);
            titles.add(titleEntity);
            client.getDatabaseManager().save(titleEntity);
        }

        this.titles = titles;
    }
}
