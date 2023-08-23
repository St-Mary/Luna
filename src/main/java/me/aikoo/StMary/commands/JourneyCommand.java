package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.core.abstracts.AbstractCommand;
import me.aikoo.StMary.core.classes.Journey;
import me.aikoo.StMary.core.database.MoveEntity;
import me.aikoo.StMary.core.database.PlayerEntity;
import me.aikoo.StMary.core.abstracts.Button;
import me.aikoo.StMary.core.classes.Place;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * A command to initiate a journey to a destination.
 */
public class JourneyCommand extends AbstractCommand {

    private boolean isStarted = false;

    /**
     * Constructs a Journey instance.
     *
     * @param stMaryClient The StMaryClient instance.
     */
    public JourneyCommand(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "journey";
        this.description = "\uD83D\uDDFA\uFE0F Voyager vers une destination.";
        this.cooldown = 5000L;

        this.options.add(new OptionData(OptionType.STRING, "destination", "La destination où aller").setAutoComplete(true).setRequired(true));
    }

    /**
     * Executes the "journey" command, allowing a player to initiate a journey to a specified destination.
     *
     * @param event  The SlashCommandInteractionEvent representing the command interaction.
     */
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String destination = event.getOption("destination").getAsString();
        PlayerEntity player = stMaryClient.getDatabaseManager().getPlayer(event.getUser().getIdLong());
        Place place = stMaryClient.getLocationManager().getPlace(player.getCurrentLocationPlace());
        Place destinationPlace = stMaryClient.getLocationManager().getPlace(destination);

        // Check if either the current place or destination place is null.
        if (place == null || destinationPlace == null) {
            String errorText = stMaryClient.getTextManager().createText("journey_destination_not_exist").buildError();
            event.reply(errorText).setEphemeral(true).queue();
            return;
        }

        // Retrieve any existing journey moves for the player.
        MoveEntity moves = stMaryClient.getDatabaseManager().getMove(player.getId());

        // Retrieve the journey move for the specified destination.
        Journey move = place.getMove(destination);

        // Check if there are existing moves or if the move to the destination is not available.
        if (moves != null || !place.getAvailableMoves().contains(move)) {
            String text;

            // Check if the player is already on a journey.
            if (moves != null) {
                Place toPlace = stMaryClient.getLocationManager().getPlace(moves.getTo());
                String formattedText = (place.getTown() == toPlace.getTown()) ? toPlace.getIcon() + toPlace.getName() : toPlace.getTown().getIcon() + toPlace.getTown().getName();

                text = stMaryClient.getTextManager().createText("journey_err_destination_1").replace("name", formattedText).buildError();
            } else {
                text = stMaryClient.getTextManager().createText("journey_err_destination_2").buildError();
            }

            event.reply(text).setEphemeral(true).queue();
            return;
        }

        ConfirmBtn confirmBtn = new ConfirmBtn(move, player);
        CloseBtn closeBtn = new CloseBtn(destinationPlace);
        this.buttons.put(confirmBtn.getId(), confirmBtn);
        this.buttons.put(closeBtn.getId(), closeBtn);

        long time = move.getTime();

        String formattedText = (place.getTown() == destinationPlace.getTown() || !destinationPlace.isTownPlace()) ? stMaryClient.getLocationManager().formatLocation(destinationPlace.getName()) : stMaryClient.getLocationManager().formatLocation(destinationPlace.getTown().getName());
        String str = stMaryClient.getTextManager().createText("journey_confirm").replace("time", String.valueOf(time)).replace("destination", formattedText).build();

        event.reply(str).addActionRow(confirmBtn.getButton(), closeBtn.getButton()).queue(msg -> msg.retrieveOriginal().queue(res -> {
            // Add buttons to the message for user interaction.
            stMaryClient.getButtonManager().addButtons(res.getId(), this.getArrayListButtons());

            // Schedule a timer to close the journey if it is not started.
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            if (!isStarted) {
                                close(res, destinationPlace);
                            }
                        }
                    },
                    20000
            );
        }));
    }


    /**
     * Closes the journey message.
     *
     * @param message          The message to edit.
     * @param destinationPlace The destination place.
     */
    private void close(Message message, Place destinationPlace) {
        String formattedLocation = stMaryClient.getLocationManager().formatLocation(destinationPlace.getName());
        String text = stMaryClient.getTextManager().createText("journey_cancel").replace("destination", formattedLocation).build();
        List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = message.getButtons();
        buttons.replaceAll(net.dv8tion.jda.api.interactions.components.buttons.Button::asDisabled);

        message.editMessage(text).setActionRow(buttons).queue();
    }

    /**
     * Handles auto-completion of a command based on the user's current location.
     *
     * @param event  The CommandAutoCompleteInteractionEvent triggered by the user.
     */
    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event) {
        PlayerEntity player = stMaryClient.getDatabaseManager().getPlayer(event.getUser().getIdLong());

        if (player == null) {
            return;
        }

        Place place = stMaryClient.getLocationManager().getPlace(player.getCurrentLocationPlace());

        if (place == null) {
            return;
        }

        if (place.getAvailableMoves().isEmpty()) {
            return;
        }

        // Create a list to store auto-completion choices.
        ArrayList<Command.Choice> choices = new ArrayList<>();

        // Iterate through available moves and generate choices.
        for (Journey move : place.getAvailableMoves()) {
            // Retrieve the destination information.
            String destinationName = move.getTo().getName();
            Place destination = stMaryClient.getLocationManager().getPlace(destinationName);

            // Determine the display name based on the destination's town.
            String name;
            if (destination.isTownPlace() && place.isTownPlace() && place.getTown() != destination.getTown()) {
                name = this.stMaryClient.getLocationManager().formatLocation(destination.getTown().getName());
            } else {
                name = this.stMaryClient.getLocationManager().formatLocation(destinationName);
            }

            choices.add(new Command.Choice(name, destinationName));
        }

        // Reply with the auto-completion choices.
        event.replyChoices(choices).queue();
    }


    /**
     * Represents a confirm button for journey.
     */
    private class ConfirmBtn extends Button {

        private final Journey move;
        private final PlayerEntity player;

        /**
         * Constructs a ConfirmBtn instance.
         *
         * @param move         The journey move.
         * @param player       The player associated with the user.
         */
        public ConfirmBtn( Journey move, PlayerEntity player) {
            super("confirm_move", stMaryClient.getTextManager().getText("journey_btn_confirm"), ButtonStyle.SUCCESS, Emoji.fromUnicode("\uD83D\uDDFA\uFE0F"), stMaryClient);

            this.move = move;
            this.player = player;
        }

        @Override
        public void onClick(ButtonInteractionEvent event) {
            // Get the old place and destination place based on the player's current location and destination.
            Place oldPlace = stMaryClient.getLocationManager().getPlace(player.getCurrentLocationPlace());
            Place destinationPlace = stMaryClient.getLocationManager().getPlace(move.getTo().getName());

            // Create a new MoveEntity to track the journey details.
            MoveEntity moves = new MoveEntity();
            moves.setPlayerId(player.getId());
            moves.setFrom(move.getFrom().getName());
            moves.setTo(move.getTo().getName());
            moves.setTime(move.getTime());
            moves.setStart(System.currentTimeMillis());

            // Save the journey details in the database.
            stMaryClient.getDatabaseManager().save(moves);

            // Set the journey as started.
            isStarted = true;

            // Determine the formatted text based on the town of the destination.
            String formattedText = (oldPlace.getTown() == destinationPlace.getTown() || !destinationPlace.isTownPlace()) ?
                    stMaryClient.getLocationManager().formatLocation(destinationPlace.getName()) :
                    stMaryClient.getLocationManager().formatLocation(destinationPlace.getTown().getName());

            // Generate a message to inform the user about the journey.
            String text = stMaryClient.getTextManager().createText("journey_success").replace("destination", formattedText).replace("time", move.getTime().toString()).build();

            // Get the list of buttons from the event message and disable them.
            List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = event.getMessage().getButtons();
            buttons.replaceAll(net.dv8tion.jda.api.interactions.components.buttons.Button::asDisabled);

            // Edit the message to update the journey details and disabled buttons.
            event.getMessage().editMessage(text).setActionRow(buttons).queue();

            // Defer the edit of the interaction.
            if (!event.isAcknowledged()) {
                event.deferEdit().queue();
            }
        }
    }

    /**
     * Represents a close button for journey.
     */
    private class CloseBtn extends Button {

        private final Place destinationPlace;

        /**
         * Constructs a CloseBtn instance.
         *
         * @param destinationPlace The destination place.
         */
        public CloseBtn(Place destinationPlace) {
            super("close_btn", stMaryClient.getTextManager().getText("journey_btn_cancel"), ButtonStyle.DANGER, Emoji.fromUnicode("❌"), stMaryClient);
            this.destinationPlace = destinationPlace;
        }

        @Override
        public void onClick(ButtonInteractionEvent event) {
            close(event.getMessage(), destinationPlace);
            isStarted = true;
            event.deferEdit().queue();
        }
    }
}
