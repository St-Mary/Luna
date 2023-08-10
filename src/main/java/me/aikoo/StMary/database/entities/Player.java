package me.aikoo.StMary.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
}
