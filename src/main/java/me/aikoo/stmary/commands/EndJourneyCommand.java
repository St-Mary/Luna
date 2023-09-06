package me.aikoo.stmary.commands;

import me.aikoo.stmary.core.abstracts.CommandAbstract;
import me.aikoo.stmary.core.bases.PlaceBase;
import me.aikoo.stmary.core.bot.StMaryClient;
import me.aikoo.stmary.core.database.MoveEntity;
import me.aikoo.stmary.core.database.PlayerEntity;
import me.aikoo.stmary.core.managers.DatabaseManager;
import me.aikoo.stmary.core.managers.LocationManager;
import me.aikoo.stmary.core.managers.TextManager;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * A command to finish a journey and arrive at the destination.
 */
public class EndJourneyCommand extends CommandAbstract {

    /**
     * Constructor for the endjourney command.
     * @param stMaryClient The StMaryClient instance.
     */
    public EndJourneyCommand(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "endjourney";
        this.description = "\uD83D\uDDFA\uFE0F Finish your journey and arrive at the destination.";
        this.cooldown = 15000L;
    }

    /**
     * Arrives at the destination.
     *
     * @param event The interaction event.
     */
    @Override
    public void execute(SlashCommandInteractionEvent event, String language) {
        PlayerEntity player = DatabaseManager.getPlayer(event.getUser().getIdLong());
        UUID uuid = player.getId();
        MoveEntity moves = DatabaseManager.getMove(uuid);

        if (moves == null) {
            String text = TextManager.createText("endjourney_no_journey", language).build();
            event.reply(text).queue();
            return;
        }

        long start = moves.getStart();
        long time = moves.getTime() * 60000L;
        long now = System.currentTimeMillis();
        long end = start + time;

        PlaceBase destinationPlace = LocationManager.getPlaceById(moves.getTo());
        PlaceBase fromPlace = LocationManager.getPlaceById(moves.getFrom());
        String formatted = (fromPlace.getTown() == destinationPlace.getTown() || !destinationPlace.isTownPlace()) ?
                LocationManager.formatLocation(destinationPlace.getId(), language) :
                LocationManager.formatLocation(destinationPlace.getTown().getId(), language);

        // Check if the player has arrived at the destination.
        if (now < end) {
            long remaining = end - now;
            long seconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() + remaining);
            String text = TextManager.createText("endjourney_journey_not_finsh", language).replace("time", String.valueOf(seconds)).replace("location", formatted).build();
            event.reply(text).queue();
            return;
        }

        // Update the player's location and remove the journey record from the database.
        player.setCurrentLocationPlace(destinationPlace.getId());
        player.setCurrentLocationRegion(destinationPlace.getRegion().getId());
        player.setCurrentLocationTown(destinationPlace.isTownPlace() ? destinationPlace.getTown().getId() : "");

        // Update the player's location in the database.
        DatabaseManager.delete(moves);
        DatabaseManager.update(player);

        // Send the arrival message.
        String text = TextManager.createText("endjourney_success", language).replace("location", formatted).build();
        event.reply(text).queue();
    }

    /**
     * The autocomplete method for the command.
     * @param event The CommandAutoCompleteInteractionEvent triggered when the button is clicked.
     * @param language The language of the player
     */
    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event, String language) {
        // Unused method for this command
    }
}
