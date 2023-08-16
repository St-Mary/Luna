package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.database.entities.Player;
import me.aikoo.StMary.system.Button;
import me.aikoo.StMary.system.Title;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

public class Menu extends AbstractCommand {

    public Menu(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "menu";
        this.description = ":evergreen_tree: Menu principal";
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

            this.buttons.put(inventoryBtn.getId(), inventoryBtn);
            this.buttons.put(profilBtn.getId(), profilBtn);

            event.reply(profil).addActionRow(profilBtn.getButton(), inventoryBtn.getButton()).queue(msg -> {
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
            super("inv_btn", "Inventory", ButtonStyle.PRIMARY, Emoji.fromUnicode("\uD83C\uDFF9"), stMaryClient);

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
}
