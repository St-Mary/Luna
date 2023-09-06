package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.abstracts.ButtonAbstract;
import me.aikoo.StMary.core.abstracts.CommandAbstract;
import me.aikoo.StMary.core.bases.JourneyBase;
import me.aikoo.StMary.core.bases.PlaceBase;
import me.aikoo.StMary.core.bot.StMaryClient;
import me.aikoo.StMary.core.constants.BotConfigConstant;
import me.aikoo.StMary.core.database.MoveEntity;
import me.aikoo.StMary.core.database.PlayerEntity;
import me.aikoo.StMary.core.managers.DatabaseManager;
import me.aikoo.StMary.core.managers.LocationManager;
import me.aikoo.StMary.core.managers.TextManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * A command to initiate a journey to a destination.
 */
public class JourneyCommand extends CommandAbstract {


    /**
     * Constructs a Journey instance.
     *
     * @param stMaryClient The StMaryClient instance.
     */
    public JourneyCommand(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "journey";
        this.description = "\uD83D\uDDFA\uFE0F Travel to a destination.";
        this.cooldown = 5000L;

        this.options.add(new OptionData(OptionType.STRING, "destination", "The destination where to go").setAutoComplete(true).setRequired(true));
    }

    /**
     * Executes the "journey" command, allowing a player to initiate a journey to a specified destination.
     *
     * @param event The SlashCommandInteractionEvent representing the command interaction.
     */
    @Override
    public void execute(SlashCommandInteractionEvent event, String language) {
        String destination = event.getOption("destination").getAsString();
        PlayerEntity player = DatabaseManager.getPlayer(event.getUser().getIdLong());
        PlaceBase place = LocationManager.getPlaceById(player.getCurrentLocationPlace());
        PlaceBase destinationPlace = LocationManager.getPlaceById(destination);

        // Check if either the current place or destination place is null.
        if (place == null || destinationPlace == null) {
            String errorText = TextManager.createText("journey_destination_not_exist", language).buildError();
            event.reply(errorText).setEphemeral(true).queue();
            return;
        }

        // Retrieve any existing journey moves for the player.
        MoveEntity moves = DatabaseManager.getMove(player.getId());

        // Retrieve the journey move for the specified destination.
        JourneyBase move = place.getMove(destination);

        // Check if there are existing moves or if the move to the destination is not available.
        if (moves != null || !place.getAvailableMoves().contains(move)) {
            String text;

            // Check if the player is already on a journey.
            if (moves != null) {
                PlaceBase toPlace = LocationManager.getPlaceById(moves.getTo());
                String formattedText = (place.getTown() == toPlace.getTown()) ? LocationManager.formatLocation(toPlace.getId(), language) : LocationManager.formatLocation(toPlace.getTown().getId(), language);

                text = TextManager.createText("journey_err_destination_1", language).replace("name", formattedText).buildError();
            } else {
                text = TextManager.createText("journey_err_destination_2", language).buildError();
            }

            event.reply(text).setEphemeral(true).queue();
            return;
        }

        try {
            Method confirmMethod = JourneyCommand.class.getMethod("confirmBtn", ButtonInteractionEvent.class, String.class, JourneyBase.class, PlayerEntity.class);
            Method closeMethod = JourneyCommand.class.getMethod("closeBtn", ButtonInteractionEvent.class, String.class, String.class, PlaceBase.class);
            Method close = JourneyCommand.class.getMethod("close", Message.class, String.class, String.class, PlaceBase.class);

            ButtonAbstract confirmBtn = new ButtonAbstract("confirmBtn", TextManager.getText("journey_btn_confirm", language), ButtonStyle.SUCCESS, Emoji.fromFormatted(BotConfigConstant.getEmote("yes")), stMaryClient, this, confirmMethod, move, player);
            ButtonAbstract closeBtn = new ButtonAbstract("closeBtn", TextManager.getText("journey_btn_cancel", language), ButtonStyle.DANGER, Emoji.fromFormatted(BotConfigConstant.getEmote("no")), stMaryClient, this, closeMethod, event.getUser().getId(), destinationPlace);

            long time = move.getTime();

            String formattedText = (place.getTown() == destinationPlace.getTown() || !destinationPlace.isTownPlace()) ? LocationManager.formatLocation(destinationPlace.getId(), language) : LocationManager.formatLocation(destinationPlace.getTown().getId(), language);
            String str = TextManager.createText("journey_confirm", language).replace("time", String.valueOf(time)).replace("destination", formattedText).build();

            stMaryClient.getCache().put("actionWaiter_" + event.getUser().getId(), "journey");
            this.sendMsgWithButtons(event, str, language, new ArrayList<>(List.of(confirmBtn, closeBtn)), 20000, close, this, event.getUser().getId(), destinationPlace);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }


    /**
     * Closes the journey message.
     *
     * @param message          The message to edit.
     * @param destinationPlace The destination place.
     */
    public void close(Message message, String language, String id, PlaceBase destinationPlace) {
        String formattedLocation = LocationManager.formatLocation(destinationPlace.getId(), language);
        String text = TextManager.createText("journey_cancel", language).replace("destination", formattedLocation).build();

        message.editMessage(text).setComponents().queue();
        stMaryClient.getCache().delete("actionWaiter_" + id);
    }

    /**
     * Confirms the journey.
     *
     * @param event    The ButtonInteractionEvent triggered when the button is clicked.
     * @param language The language of the player.
     * @param move     The journey move.
     * @param player   The PlayerEntity instance.
     */
    public void confirmBtn(ButtonInteractionEvent event, String language, JourneyBase move, PlayerEntity player) {
        if (player.getDiscordId() != event.getUser().getIdLong()) {
            event.reply(TextManager.createText("command_error_button_only_author", language).buildError()).setEphemeral(true).queue();
            return;
        }

        // Get the old place and destination place based on the player's current location and destination.
        PlaceBase oldPlace = LocationManager.getPlaceById(player.getCurrentLocationPlace());
        PlaceBase destinationPlace = LocationManager.getPlaceById(move.getTo().getId());

        // Create a new MoveEntity to track the journey details.
        MoveEntity moves = new MoveEntity();
        moves.setPlayerId(player.getId());
        moves.setFrom(move.getFrom().getId());
        moves.setTo(move.getTo().getId());
        moves.setTime(move.getTime());
        moves.setStart(System.currentTimeMillis());

        // Save the journey details in the database.
        DatabaseManager.save(moves);

        // Determine the formatted text based on the town of the destination.
        String formattedText = (oldPlace.getTown() == destinationPlace.getTown() || !destinationPlace.isTownPlace()) ?
                LocationManager.formatLocation(destinationPlace.getId(), language) :
                LocationManager.formatLocation(destinationPlace.getTown().getId(), language);

        // Generate a message to inform the user about the journey.
        String text = TextManager.createText("journey_success", language).replace("destination", formattedText).replace("time", move.getTime().toString()).build();

        // Get the list of buttons from the event message and disable them.
        List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = event.getMessage().getButtons();
        buttons.replaceAll(net.dv8tion.jda.api.interactions.components.buttons.Button::asDisabled);

        // Edit the message to update the journey details and disabled buttons.
        event.getMessage().editMessage(text).setActionRow(buttons).queue();
        stMaryClient.getCache().delete("actionWaiter_" + player.getDiscordId());

        // Defer the edit of the interaction.
        if (!event.isAcknowledged()) {
            event.deferEdit().queue();
        }
    }

    /**
     * Closes the journey message.
     * @param event The ButtonInteractionEvent triggered when the button is clicked.
     * @param language The language of the player.
     * @param id The Discord ID of the player.
     * @param destinationPlace The destination place.
     */
    public void closeBtn(ButtonInteractionEvent event, String language, String id, PlaceBase destinationPlace) {
        if (!event.getUser().getId().equals(id)) {
            event.reply(TextManager.createText("command_error_button_only_author", language).buildError()).setEphemeral(true).queue();
            return;
        }

        close(event.getMessage(), language, id, destinationPlace);

        if (!event.isAcknowledged()) {
            event.deferEdit().queue();
        }
    }

    /**
     * Handles auto-completion of a command based on the user's current location.
     *
     * @param event The CommandAutoCompleteInteractionEvent triggered by the user.
     */
    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event, String language) {
        PlayerEntity player = DatabaseManager.getPlayer(event.getUser().getIdLong());

        if (player == null) {
            return;
        }

        PlaceBase place = LocationManager.getPlaceById(player.getCurrentLocationPlace());

        if (place == null) {
            return;
        }

        if (place.getAvailableMoves().isEmpty()) {
            return;
        }

        // Create a list to store auto-completion choices.
        ArrayList<Command.Choice> choices = new ArrayList<>();

        // Iterate through available moves and generate choices.
        for (JourneyBase move : place.getAvailableMoves()) {
            // Retrieve the destination information.
            String destinationId = move.getTo().getId();
            PlaceBase destination = LocationManager.getPlaceById(destinationId);

            // Determine the display name based on the destination's town.
            String name;
            if (destination.isTownPlace() && place.isTownPlace() && place.getTown() != destination.getTown()) {
                name = LocationManager.formatLocation(destination.getTown().getId(), language);
            } else {
                name = LocationManager.formatLocation(destinationId, language);
            }

            choices.add(new Command.Choice(name, destinationId));
        }

        // Reply with the auto-completion choices.
        event.replyChoices(choices).queue();
    }
}
