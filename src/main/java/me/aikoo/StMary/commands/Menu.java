package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.database.entities.Player;
import me.aikoo.StMary.system.Button;
import me.aikoo.StMary.system.Title;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.util.HashMap;
import java.util.List;

public class Menu extends AbstractCommand {

    public Menu(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "menu";
        this.description = "\uD83C\uDF32 Menu principal";
        this.cooldown = 10000L;
    }

    @Override
    public void execute(StMaryClient client, SlashCommandInteractionEvent event) {
        Player player = client.getDatabaseManager().getPlayer(event.getUser().getIdLong());

        if (player == null) {
            event.reply("Vous n'avez pas encore commencé l'aventure !").queue();
        } else {
            String profil = profilEmbed(client, event.getUser().getGlobalName(), player);

            InventoryBtn inventoryBtn = new InventoryBtn(player);
            ProfilBtn profilBtn = new ProfilBtn(player);
            TitlesBtn titlesBtn = new TitlesBtn(player);

            this.buttons.put(inventoryBtn.getId(), inventoryBtn);
            this.buttons.put(profilBtn.getId(), profilBtn);
            this.buttons.put(titlesBtn.getId(), titlesBtn);

            event.reply(profil).addActionRow(profilBtn.getButton(), inventoryBtn.getButton(), titlesBtn.getButton()).queue(msg -> {
                msg.retrieveOriginal().queue(res -> {
                    stMaryClient.getButtonManager().addButtons(res.getId(), this.getArrayListButtons());
                });
            });
        }
    }

    private String profilEmbed(StMaryClient client, String name, Player player) {
        Title title = player.getCurrentTitle(client);
        String icon = title.getIcon();
        String town = (player.getCurrentLocationTown() != null) ? player.getCurrentLocationTown() : "Aucune ville";
        String place = player.getCurrentLocationPlace();

        String stringBuilder = String.format("### %s | %s | Menu Joueur\n", icon, name) +
                "**Niveau :** `" + player.getLevel() + "`\n" +
                "**Expérience :** `" + player.getExperience() + " EXP`\n" +
                "▬▬▬▬▬▬▬▬▬▬\n" +
                "**Localisation :** `%s` - `%s`\n".formatted(town, place) +
                "**Région :** `%s`\n".formatted(player.getCurrentLocationRegion());

        return stringBuilder;
    }

    private class ProfilBtn extends Button {
            private final Player player;

            public ProfilBtn(Player player) {
                super("profil_btn", "Profil", ButtonStyle.PRIMARY, Emoji.fromUnicode("\uD83D\uDCDD"), stMaryClient);

                this.player = player;
            }

            @Override
            public void onClick(ButtonInteractionEvent event) {
                event.editMessage(profilEmbed(stMaryClient, event.getUser().getGlobalName(), player)).queue();
            }
    }

    private class InventoryBtn extends Button {

        private final Player player;

        public InventoryBtn(Player player) {
            super("inv_btn", "Sac à Dos", ButtonStyle.PRIMARY, Emoji.fromUnicode("\uD83C\uDF92"), stMaryClient);

            this.player = player;
        }

        @Override
        public void onClick(ButtonInteractionEvent event) {
            Title title = player.getCurrentTitle(stMaryClient);
            String icon = title.getIcon();

            String stringBuilder = String.format("### %s | %s | Sac à Dos\n", icon, event.getUser().getGlobalName()) +
                    "**Argent :** `" + player.getMoney() + "`\n" +
                    "▬▬▬▬▬▬▬▬▬▬\n";

            event.editMessage(stringBuilder).queue();
        }
    }

    private class TitlesBtn extends Button {

        private final Player player;

        public TitlesBtn(Player player) {
            super("titles_btn", "Titres", ButtonStyle.DANGER, Emoji.fromUnicode("\uD83C\uDFC5"), stMaryClient);

            this.player = player;
        }

        @Override
        public void onClick(ButtonInteractionEvent event) {
            Title title = player.getCurrentTitle(stMaryClient);
            String icon = title.getIcon();

            StringBuilder stringBuilder = new StringBuilder(String.format("### %s | %s | Titres\n", icon, event.getUser().getGlobalName()));
            HashMap<String, Title> titles = player.getTitles(stMaryClient);

            for (Title t : titles.values()) {
                stringBuilder.append(t.getIcon()).append(" | `").append(t.getName()).append("`");
                if (t.getName().equals(title.getName())) {
                    stringBuilder.append("   ⬅\uFE0F **Actif**\n");
                } else {
                    stringBuilder.append("\n");
                }
            }
            stringBuilder.append("▬▬▬▬▬▬▬▬▬▬\n**Nombre de titres:** `").append(titles.size()).append("`");

            event.editMessage(stringBuilder.toString()).queue();
        }
    }
}
