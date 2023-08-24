package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.abstracts.ButtonAbstract;
import me.aikoo.StMary.core.abstracts.CommandAbstract;
import me.aikoo.StMary.core.bases.ObjectBase;
import me.aikoo.StMary.core.bases.PlaceBase;
import me.aikoo.StMary.core.bases.TitleBase;
import me.aikoo.StMary.core.bot.StMaryClient;
import me.aikoo.StMary.core.database.MoveEntity;
import me.aikoo.StMary.core.database.PlayerEntity;
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
public class MenuCommand extends CommandAbstract {

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
     * @param event The SlashCommandInteractionEvent triggered by the command.
     */
    @Override
    public void execute(SlashCommandInteractionEvent event, String language) {
        // Determine the user to display the menu for
        User user = event.getOption("user") == null ? event.getUser() : Objects.requireNonNull(event.getOption("user")).getAsUser();
        PlayerEntity player = stMaryClient.getDatabaseManager().getPlayer(user.getIdLong());

        // Check if the user has an adventure account
        if (player == null) {
            event.reply(stMaryClient.getTextManager().createText("menu_no_account", language).buildError()).setEphemeral(true).queue();
        } else {
            // Generate and send the user's profile
            String profil = profilEmbed(user.getGlobalName(), player, language);

            // Create buttons for inventory, profile, titles, and close
            InventoryBtn inventoryBtn = new InventoryBtn(player, user.getId(), language);
            ProfilBtn profilBtn = new ProfilBtn(player, user.getId(), language);
            TitlesBtn titlesBtn = new TitlesBtn(player, user.getId(), language);
            CloseBtn closeBtn = new CloseBtn(user.getId(), language);

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
        List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = message.getButtons();
        buttons.replaceAll(net.dv8tion.jda.api.interactions.components.buttons.Button::asDisabled);

        message.editMessage(message.getContentRaw()).setActionRow(buttons).queue();
    }

    /**
     * Auto-complete handler for the Menu command.
     *
     * @param event The CommandAutoCompleteInteractionEvent triggered by auto-complete.
     */
    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event, String language) {
        // This command doesn't support auto-complete, so this method is left empty.
    }

    /**
     * Generates a formatted profile embed for a user.
     *
     * @param name   The user's name.
     * @param player The PlayerEntity associated with the user.
     * @return The formatted profile as a string.
     */
    private String profilEmbed(String name, PlayerEntity player, String language) {
        TitleBase title = player.getCurrentTitle(stMaryClient);
        String icon = title.getIcon();
        String place = stMaryClient.getLocationManager().getPlaceById(player.getCurrentLocationPlace()).getName(player.getLanguage());

        // Get the player's current location
        String town = (!player.getCurrentLocationTown().equals("")) ? stMaryClient.getLocationManager().getTownById(player.getCurrentLocationTown()).getName(player.getLanguage()) : stMaryClient.getTextManager().getText("menu_no_town", language);
        String location = stMaryClient.getTextManager().getText("menu_location_1", language).replace("{{town}}", town).replace("{{place}}", place);
        MoveEntity move = stMaryClient.getDatabaseManager().getMove(player.getId());

        if (move != null) {
            PlaceBase to = stMaryClient.getLocationManager().getPlaceById(move.getTo());
            PlaceBase from = stMaryClient.getLocationManager().getPlaceById(move.getFrom());

            // Format the destination and departure names based on whether they are town places or not
            String destinationName = to.isTownPlace() ? to.getTown().getName(player.getLanguage()) + " - " + to.getName(player.getLanguage()) : to.getName(player.getLanguage());
            String departureName = from.getName(player.getLanguage()) + (from.isTownPlace() ? " - " + from.getTown().getName(player.getLanguage()) : "");

            if (!to.isTownPlace()) {
                destinationName = to.getName(player.getLanguage()) + " - " + to.getRegion().getName(player.getLanguage());
            }

            location = stMaryClient.getTextManager().getText("menu_location_2", language).replace("{{destination}}", destinationName).replace("{{departure}}", departureName);
        }

        // Return the text to send
        return stMaryClient.getTextManager().getText("menu_player", language)
                .replace("{{icon}}", icon)
                .replace("{{name}}", name)
                .replace("{{level}}", player.getLevel().toString())
                .replace("{{exp}}", player.getExperience().toString())
                .replace("{{location}}", location)
                .replace("{{region}}", stMaryClient.getLocationManager().getRegionById(player.getCurrentLocationRegion()).getName(player.getLanguage()));
    }


    /**
     * Represents a profile button.
     */
    private class ProfilBtn extends ButtonAbstract {
        private final PlayerEntity player;
        private final String id;

        /**
         * Constructs a ProfilBtn instance.
         *
         * @param player The PlayerEntity associated with the user.
         * @param id     The user's ID.
         */
        public ProfilBtn(PlayerEntity player, String id, String language) {
            super("profil_btn", stMaryClient.getTextManager().getText("menu_btn_profil", language), ButtonStyle.PRIMARY, Emoji.fromUnicode("\uD83D\uDCDD"), stMaryClient);

            this.player = player;
            this.id = id;
        }

        @Override
        public void onClick(ButtonInteractionEvent event, String language) {
            if (!event.getUser().getId().equals(id)) return;
            event.editMessage(profilEmbed(event.getUser().getGlobalName(), player, language)).queue();
            if (!event.getInteraction().isAcknowledged()) {
                event.deferEdit().queue();
            }
        }
    }


    /**
     * Represents an inventory button.
     */
    private class InventoryBtn extends ButtonAbstract {
        private final PlayerEntity player;
        private final String id;

        /**
         * Constructs an InventoryBtn instance.
         *
         * @param player The PlayerEntity associated with the user.
         * @param id     The user's ID.
         */
        public InventoryBtn(PlayerEntity player, String id, String language) {
            super("inv_btn", stMaryClient.getTextManager().getText("menu_btn_backpack", language), ButtonStyle.PRIMARY, Emoji.fromUnicode("\uD83C\uDF92"), stMaryClient);

            this.player = player;
            this.id = id;
        }

        @Override
        public void onClick(ButtonInteractionEvent event, String language) {
            if (!event.getUser().getId().equals(id)) return;
            TitleBase title = player.getCurrentTitle(stMaryClient);
            String icon = title.getIcon();
            ObjectBase magicalBook = player.getMagicalBook(stMaryClient);
            String magicalBookName = (magicalBook != null) ? "%s `%s`".formatted(magicalBook.getIcon(), magicalBook.getName()) : stMaryClient.getTextManager().getText("menu_no_magical_book", language);

            // Generate the inventory text
            String text = stMaryClient.getTextManager().getText("menu_backpack", language)
                    .replace("{{icon}}", icon)
                    .replace("{{name}}", event.getUser().getGlobalName())
                    .replace("{{money}}", player.getMoney().toString())
                    .replace("{{magical_book}}", magicalBookName);

            event.editMessage(text).queue();
            if (!event.getInteraction().isAcknowledged()) {
                event.deferEdit().queue();
            }
        }
    }

    /**
     * Represents a titles button.
     */
    private class TitlesBtn extends ButtonAbstract {
        private final PlayerEntity player;
        private final String id;

        /**
         * Constructs a TitlesBtn instance.
         *
         * @param player The PlayerEntity associated with the user.
         * @param id     The user's ID.
         */
        public TitlesBtn(PlayerEntity player, String id, String language) {
            super("titles_btn", stMaryClient.getTextManager().getText("menu_btn_titles", language), ButtonStyle.PRIMARY, Emoji.fromUnicode("\uD83C\uDFC5"), stMaryClient);

            this.player = player;
            this.id = id;
        }

        @Override
        public void onClick(ButtonInteractionEvent event, String language) {
            if (!event.getUser().getId().equals(id)) return;

            TitleBase title = player.getCurrentTitle(stMaryClient);
            String icon = title.getIcon();

            HashMap<String, TitleBase> titles = player.getTitles(stMaryClient);

            StringBuilder stringBuilder = new StringBuilder();
            for (TitleBase t : titles.values()) {
                stringBuilder.append(t.getIcon()).append(" | `").append(t.getName()).append("`");
                if (t.getName().equals(title.getName())) {
                    stringBuilder.append(stMaryClient.getTextManager().getText("menu_titles_actual", language));
                } else {
                    stringBuilder.append("\n");
                }
            }

            // Generate the text to send
            String text = stMaryClient.getTextManager().getText("menu_titles", language)
                    .replace("{{icon}}", icon)
                    .replace("{{name}}", event.getUser().getGlobalName())
                    .replace("{{titles}}", stringBuilder.toString())
                    .replace("{{nb_titles}}", String.valueOf(titles.size()));

            event.editMessage(text).queue();
            if (!event.getInteraction().isAcknowledged()) {
                event.deferEdit().queue();
            }
        }
    }


    /**
     * Represents a close button.
     */
    private class CloseBtn extends ButtonAbstract {
        String id;

        /**
         * Constructs a CloseBtn instance.
         *
         * @param id The user's ID.
         */
        public CloseBtn(String id, String language) {
            super("close_btn", stMaryClient.getTextManager().getText("menu_btn_close", language), ButtonStyle.DANGER, Emoji.fromUnicode("❌"), stMaryClient);
            this.id = id;
        }

        @Override
        public void onClick(ButtonInteractionEvent event, String language) {
            closeMenu(event.getMessage(), id);
            isClosed = true;
            if (!event.getInteraction().isAcknowledged()) {
                event.deferEdit().queue();
            }
        }
    }
}
