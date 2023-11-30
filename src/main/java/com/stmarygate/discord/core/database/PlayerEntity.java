package com.stmarygate.discord.core.database;

import com.stmarygate.discord.core.bases.ObjectBase;
import com.stmarygate.discord.core.bases.TitleBase;
import com.stmarygate.discord.core.bot.StMaryClient;
import com.stmarygate.discord.core.managers.DatabaseManager;
import com.stmarygate.discord.core.managers.ObjectManager;
import com.stmarygate.discord.core.managers.TitleManager;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import java.math.BigInteger;
import java.util.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

/** The PlayerEntity class represents a player in the database. */
@Entity()
@Table(name = "players")
public class PlayerEntity {

  /** The id of the player. Get the id of the player. */
  @Getter @Id @GeneratedValue @UuidGenerator private UUID id;

  /** Get the Discord id of the player. Set the Discord id of the player. */
  @Getter
  @Setter
  @Column(name = "discord_id", nullable = false)
  private Long discordId;

  /** Get the language of the player. Set the language of the player. */
  @Getter
  @Setter
  @Column(name = "language", nullable = false)
  private String language;

  /** The titles of the player. */
  @OneToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "player_titles", joinColumns = @JoinColumn(name = "player_id"))
  @Column(name = "title")
  private List<TitleEntity> titles;

  /** Set the current title of the player. */
  @Setter
  @Column(name = "current_title", nullable = false)
  private String currentTitle;

  /**
   * Get the current region location of the player. Set the current region location of the player.
   */
  @Getter
  @Setter
  @Column(name = "current_location_region", nullable = false)
  private String currentLocationRegion;

  /** Get the current town location of the player. Set the current town location of the player. */
  @Getter
  @Setter
  @Column(name = "current_location_town")
  private String currentLocationTown;

  /** Get the current place location of the player. Set the current place location of the player. */
  @Getter
  @Setter
  @Column(name = "current_location_place", nullable = false)
  private String currentLocationPlace;

  /** Get the current level of the player. Set the current level of the player. */
  @Getter
  @Setter
  @Column(name = "level", columnDefinition = "int default 1", nullable = false)
  private Integer level;

  /** Get the current experience of the player. Set the current experience of the player. */
  @Getter
  @Setter
  @Column(name = "experience", columnDefinition = "bigint default 0", nullable = false)
  private BigInteger experience;

  /** Get the current money of the player. Set the current money of the player. */
  @Getter
  @Setter
  @Column(name = "money", columnDefinition = "bigint default 0", nullable = false)
  private BigInteger money;

  /** Get the creation timestamp of the player. Set the creation timestamp of the player. */
  @Getter
  @Setter
  @Column(name = "creation_timestamp", nullable = false)
  private Date creationTimestamp;

  /** Set the magical book owned by the player. */
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
    return TitleManager.getTitle(this.currentTitle);
  }

  /**
   * Get all titles owned by the player.
   *
   * @param client The StMaryClient instance.
   * @return A HashMap of title names and Title objects.
   */
  @Transactional
  public Map<String, TitleBase> getTitles(StMaryClient client) {
    Map<String, TitleBase> titles = new HashMap<>();
    for (TitleEntity title : this.titles) {
      titles.put(title.getTitleId(), TitleManager.getTitle(title.getTitleId()));
    }
    return titles;
  }

  /**
   * Add a title to the player's collection of titles.
   *
   * @param titleId The id of the title to add.
   * @param client The StMaryClient instance.
   */
  public void addTitle(String titleId, StMaryClient client) {
    TitleEntity titleEntity = new TitleEntity();
    titleEntity.setTitleId(titleId);
    DatabaseManager.save(titleEntity);
    this.titles.add(titleEntity);
  }

  /**
   * Get the magical book owned by the player.
   *
   * @param client The StMaryClient instance.
   * @return The magical book owned by the player.
   */
  public ObjectBase getMagicalBook(StMaryClient client) {
    return ObjectManager.getObject(this.magicalBook);
  }

  /**
   * Set the titles of the player.
   *
   * @param strings The list of title names.
   * @param client The StMaryClient instance.
   */
  public void setTitles(List<String> strings, StMaryClient client) {
    ArrayList<TitleEntity> titles = new ArrayList<>();
    for (String id : strings) {
      TitleEntity titleEntity = new TitleEntity();
      titleEntity.setTitleId(id);
      titles.add(titleEntity);
      DatabaseManager.save(titleEntity);
    }

    this.titles = titles;
  }
}
