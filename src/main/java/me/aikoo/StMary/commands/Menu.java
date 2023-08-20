package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.database.entities.Moves;
import me.aikoo.StMary.database.entities.Player;
import me.aikoo.StMary.system.Button;
import me.aikoo.StMary.system.Object;
import me.aikoo.StMary.system.Place;
import me.aikoo.StMary.system.Title;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Menu extends AbstractCommand {

    private boolean isClosed = false;

    public Menu(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "menu";
        this.description = "\uD83C\uDF32 Menu principal";
        this.cooldown = 10000L;

        this.options.add(new OptionData(OptionType.USER, "user", "L'utilisateur dont voir le menu"));
    }

    @Override
    public void execute(StMaryClient client, SlashCommandInteractionEvent event) {
        User user = event.getOption("user") == null ? event.getUser() : Objects.requireNonNull(event.getOption("user")).getAsUser();
        Player player = client.getDatabaseManager().getPlayer(user.getIdLong());

        if (player == null) {
            event.reply(client.getTextManager().generateError("Menu Joueur", "Cet utilisateur n'a pas de compte aventure!")).setEphemeral(true).queue();
        } else {
            String profil = profilEmbed(client, user.getGlobalName(), player);

            InventoryBtn inventoryBtn = new InventoryBtn(player, user.getId());
            ProfilBtn profilBtn = new ProfilBtn(player, user.getId());
            TitlesBtn titlesBtn = new TitlesBtn(player, user.getId());
            CloseBtn closeBtn = new CloseBtn(user.getId());

            this.buttons.put(inventoryBtn.getId(), inventoryBtn);
            this.buttons.put(profilBtn.getId(), profilBtn);
            this.buttons.put(titlesBtn.getId(), titlesBtn);
            this.buttons.put(closeBtn.getId(), closeBtn);

            event.reply(profil).addActionRow(profilBtn.getButton(), inventoryBtn.getButton(), titlesBtn.getButton(), closeBtn.getButton()).queue(msg -> msg.retrieveOriginal().queue(res -> {
                stMaryClient.getButtonManager().addButtons(res.getId(), this.getArrayListButtons());
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                if (!isClosed) {
                                    closeMenu(res, user.getId());
                                }
                            }
                        },
                        60000
                );
            }));
        }
    }

    public void closeMenu(Message message, String id) {
        String text = stMaryClient.getTextManager().generateScene("Fermeture du Menu Joueur", "Le Menu Joueur de <@" + id + "> a été fermé.");
        List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = message.getButtons();
        buttons.replaceAll(net.dv8tion.jda.api.interactions.components.buttons.Button::asDisabled);

        message.editMessage(text).setActionRow(buttons).queue();
    }

    @Override
    public void autoComplete(StMaryClient client, CommandAutoCompleteInteractionEvent event) {}

    private String profilEmbed(StMaryClient client, String name, Player player) {
        Title title = player.getCurrentTitle(client);
        String icon = title.getIcon();
        String town = (player.getCurrentLocationTown() != null) ? player.getCurrentLocationTown() : "Aucune ville";
        String place = player.getCurrentLocationPlace();
        String location = "**Localisation :** " + "`%s` - `%s`\n".formatted(town, place);
        Moves move = client.getDatabaseManager().getMoves(player.getId());

        if (move != null) {
            Place to = client.getLocationManager().getPlace(move.getTo());
            Place from = client.getLocationManager().getPlace(move.getFrom());
            if (!to.getTown().equals(from.getTown())) {
                location = "**Localisation :** Voyage vers `%s`\n**Départ:** `%s`\n".formatted(to.getTown().getName(), from.getTown().getName());
            } else location = "**Localisation :** Voyage vers `%s` - `%s`\n**Départ:** `%s`\n".formatted(to.getName(), to.getTown().getName(), from.getName());
        }

        return String.format("### %s | %s | Menu Joueur\n", icon, name) +
                "**Niveau :** `" + player.getLevel() + "`\n" +
                "**Expérience :** `" + player.getExperience() + " EXP`\n" +
                "▬▬▬▬▬▬▬▬▬▬\n" +
                location +
                "**Région :** `%s`\n".formatted(player.getCurrentLocationRegion());
    }

    private class ProfilBtn extends Button {
            private final Player player;
            private final String id;

            public ProfilBtn(Player player, String id) {
                super("profil_btn", "Profil", ButtonStyle.PRIMARY, Emoji.fromUnicode("\uD83D\uDCDD"), stMaryClient);

                this.player = player;
                this.id = id;
            }

            @Override
            public void onClick(ButtonInteractionEvent event) {
                if (!event.getUser().getId().equals(id)) return;
                event.editMessage(profilEmbed(stMaryClient, event.getUser().getGlobalName(), player)).queue();
                if (!event.getInteraction().isAcknowledged()) {
                    event.deferEdit().queue();
                }
            }
    }

    private class InventoryBtn extends Button {

        private final Player player;
        private final String id;

        public InventoryBtn(Player player, String id) {
            super("inv_btn", "Sac à Dos", ButtonStyle.PRIMARY, Emoji.fromUnicode("\uD83C\uDF92"), stMaryClient);

            this.player = player;
            this.id = id;
        }

        @Override
        public void onClick(ButtonInteractionEvent event) {
            if (!event.getUser().getId().equals(id)) return;
            Title title = player.getCurrentTitle(stMaryClient);
            String icon = title.getIcon();
            Object magicalBook = player.getMagicalBook(stMaryClient);
            String magicalBookName = (magicalBook != null) ? "%s `%s`\n".formatted(magicalBook.getIcon(), magicalBook.getName()) : "`Aucun`\n";

            String stringBuilder = String.format("### %s | %s | Sac à Dos\n", icon, event.getUser().getGlobalName()) +
                    String.format("**Argent :** `%s`\n", player.getMoney()) +
                    String.format("**Livre Magique :** %s\n", magicalBookName) +
                    "▬▬▬▬▬▬▬▬▬▬\n";

            event.editMessage(stringBuilder).queue();
            if (!event.getInteraction().isAcknowledged()) {
                event.deferEdit().queue();
            }
        }
    }

    private class TitlesBtn extends Button {

        private final Player player;
        private final String id;

        public TitlesBtn(Player player, String id) {
            super("titles_btn", "Titres", ButtonStyle.PRIMARY, Emoji.fromUnicode("\uD83C\uDFC5"), stMaryClient);

            this.player = player;
            this.id = id;
        }

        @Override
        public void onClick(ButtonInteractionEvent event) {
            if (!event.getUser().getId().equals(id)) return;

            Title title = player.getCurrentTitle(stMaryClient);
            String icon = title.getIcon();

            StringBuilder stringBuilder = new StringBuilder(String.format("### %s | %s | Titres\n", icon, event.getUser().getGlobalName()));
            HashMap<String, Title> titles = player.getTitles(stMaryClient);

            for (Title t : titles.values()) {
                stringBuilder.append(t.getIcon()).append(" | `").append(t.getName()).append("`");
                if (t.getName().equals(title.getName())) {
                    stringBuilder.append("   ⬅️ **Actif**\n");
                } else {
                    stringBuilder.append("\n");
                }
            }
            stringBuilder.append("▬▬▬▬▬▬▬▬▬▬\n**Nombre de titres:** `").append(titles.size()).append("`");

            event.editMessage(stringBuilder.toString()).queue();
            if (!event.getInteraction().isAcknowledged()) {
                event.deferEdit().queue();
            }
        }
    }

    private class CloseBtn extends Button {

            String id;
            public CloseBtn(String id) {
                super("close_btn", "Fermer", ButtonStyle.DANGER, Emoji.fromUnicode("❌"), stMaryClient);
                this.id = id;
            }

            @Override
            public void onClick(ButtonInteractionEvent event) {
                closeMenu(event.getMessage(), id);
                isClosed = true;
                if (!event.getInteraction().isAcknowledged()) {
                    event.deferEdit().queue();
                }
            }
    }
}
