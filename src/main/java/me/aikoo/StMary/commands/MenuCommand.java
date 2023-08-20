package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.database.entities.MoveEntity;
import me.aikoo.StMary.database.entities.PlayerEntity;
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

/**
 * A command to display a user's menu, including their profile, inventory, and titles.
 */
public class MenuCommand extends AbstractCommand {

    // Indicates if the menu is closed
    private boolean isClosed = false;

    /**
     * Constructs a Menu command.
     *
     * @param stMaryClient The StMaryClient instance.
     */
    public MenuCommand(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "menu";
        this.description = "\uD83C\uDF32 Menu Principal";
        this.cooldown = 10000L;

        // Define the command options
        this.options.add(new OptionData(OptionType.USER, "user", "L'utilisateur dont voir le menu"));
    }

    /**
     * Executes the Menu command.
     *
     * @param client The StMaryClient instance.
     * @param event  The SlashCommandInteractionEvent triggered by the command.
     */
    @Override
    public void execute(StMaryClient client, SlashCommandInteractionEvent event) {
        // Determine the user to display the menu for
        User user = event.getOption("user") == null ? event.getUser() : Objects.requireNonNull(event.getOption("user")).getAsUser();
        PlayerEntity player = client.getDatabaseManager().getPlayer(user.getIdLong());

        // Check if the user has an adventure account
        if (player == null) {
            event.reply(client.getTextManager().generateError("Menu Joueur", "Cet utilisateur ne possède pas de compte aventure !")).setEphemeral(true).queue();
        } else {
            // Generate and send the user's profile
            String profil = profilEmbed(client, user.getGlobalName(), player);

            // Create buttons for inventory, profile, titles, and close
            InventoryBtn inventoryBtn = new InventoryBtn(player, user.getId());
            ProfilBtn profilBtn = new ProfilBtn(player, user.getId());
            TitlesBtn titlesBtn = new TitlesBtn(player, user.getId());
            CloseBtn closeBtn = new CloseBtn(user.getId());

            // Add buttons to the button manager
            this.buttons.put(inventoryBtn.getId(), inventoryBtn);
            this.buttons.put(profilBtn.getId(), profilBtn);
            this.buttons.put(titlesBtn.getId(), titlesBtn);
            this.buttons.put(closeBtn.getId(), closeBtn);

            // Send the profile with buttons
            event.reply(profil)
                    .addActionRow(profilBtn.getButton(), inventoryBtn.getButton(), titlesBtn.getButton(), closeBtn.getButton())
                    .queue(msg -> msg.retrieveOriginal().queue(res -> {
                        stMaryClient.getButtonManager().addButtons(res.getId(), this.getArrayListButtons());

                        // Schedule the menu to close after 60 seconds
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

    /**
     * Closes the user's menu by disabling buttons and updating the message.
     *
     * @param message The message to close.
     * @param id      The user's ID.
     */
    public void closeMenu(Message message, String id) {
        String text = stMaryClient.getTextManager().generateScene("Menu Joueur fermé", "Le Menu Joueur de <@" + id + "> a été fermé.");
        List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = message.getButtons();
        buttons.replaceAll(net.dv8tion.jda.api.interactions.components.buttons.Button::asDisabled);

        message.editMessage(text).setActionRow(buttons).queue();
    }

    /**
     * Auto-complete handler for the Menu command.
     *
     * @param client The StMaryClient instance.
     * @param event  The CommandAutoCompleteInteractionEvent triggered by auto-complete.
     */
    @Override
    public void autoComplete(StMaryClient client, CommandAutoCompleteInteractionEvent event) {
        // This command doesn't support auto-complete, so this method is left empty.
    }

    /**
     * Generates a formatted profile embed for a user.
     *
     * @param client The StMaryClient instance.
     * @param name   The user's name.
     * @param player The PlayerEntity associated with the user.
     * @return The formatted profile embed as a string.
     */
    private String profilEmbed(StMaryClient client, String name, PlayerEntity player) {
        Title title = player.getCurrentTitle(client);
        String icon = title.getIcon();
        String town = (player.getCurrentLocationTown() != null) ? player.getCurrentLocationTown() : "Aucune ville";
        String place = player.getCurrentLocationPlace();
        String location = "**Localisation :** " + "`%s` - `%s`\n".formatted(town, place);
        MoveEntity move = client.getDatabaseManager().getMoves(player.getId());

        if (move != null) {
            Place to = client.getLocationManager().getPlace(move.getTo());
            Place from = client.getLocationManager().getPlace(move.getFrom());
            if (!to.getTown().equals(from.getTown())) {
                location = "**Localisation :** Voyage vers `%s`\n**Départ :** `%s`\n".formatted(to.getTown().getName(), from.getTown().getName());
            } else {
                location = "**Localisation :** Voyage vers `%s` - `%s`\n**Départ :** `%s`\n".formatted(to.getName(), to.getTown().getName(), from.getName());
            }
        }

        return String.format("### %s | %s | Menu Joueur\n", icon, name) +
                "**Niveau :** `" + player.getLevel() + "`\n" +
                "**Expérience :** `" + player.getExperience() + " EXP`\n" +
                "▬▬▬▬▬▬▬▬▬▬\n" +
                location +
                "**Région :** `%s`\n".formatted(player.getCurrentLocationRegion());
    }


    /**
     * Represents a profile button.
     */
    private class ProfilBtn extends Button {
        private final PlayerEntity player;
        private final String id;

        /**
         * Constructs a ProfilBtn instance.
         *
         * @param player The PlayerEntity associated with the user.
         * @param id     The user's ID.
         */
        public ProfilBtn(PlayerEntity player, String id) {
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


    /**
     * Represents an inventory button.
     */
    private class InventoryBtn extends Button {
        private final PlayerEntity player;
        private final String id;

        /**
         * Constructs an InventoryBtn instance.
         *
         * @param player The PlayerEntity associated with the user.
         * @param id     The user's ID.
         */
        public InventoryBtn(PlayerEntity player, String id) {
            super("inv_btn", "Backpack", ButtonStyle.PRIMARY, Emoji.fromUnicode("\uD83C\uDF92"), stMaryClient);

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

    /**
     * Represents a titles button.
     */
    private class TitlesBtn extends Button {
        private final PlayerEntity player;
        private final String id;

        /**
         * Constructs a TitlesBtn instance.
         *
         * @param player The PlayerEntity associated with the user.
         * @param id     The user's ID.
         */
        public TitlesBtn(PlayerEntity player, String id) {
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
                    stringBuilder.append("   ⬅️ **Actuel**\n");
                } else {
                    stringBuilder.append("\n");
                }
            }
            stringBuilder.append("▬▬▬▬▬▬▬▬▬▬\n**Nombre de Titres:** `").append(titles.size()).append("`");

            event.editMessage(stringBuilder.toString()).queue();
            if (!event.getInteraction().isAcknowledged()) {
                event.deferEdit().queue();
            }
        }
    }


    /**
     * Represents a close button.
     */
    private class CloseBtn extends Button {
        String id;

        /**
         * Constructs a CloseBtn instance.
         *
         * @param id The user's ID.
         */
        public CloseBtn(String id) {
            super("close_btn", "Close", ButtonStyle.DANGER, Emoji.fromUnicode("❌"), stMaryClient);
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
