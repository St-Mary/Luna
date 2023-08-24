package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.bot.StMaryClient;
import me.aikoo.StMary.core.abstracts.CommandAbstract;
import me.aikoo.StMary.core.bases.PlaceBase;
import me.aikoo.StMary.core.database.MoveEntity;
import me.aikoo.StMary.core.database.PlayerEntity;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * A command to finish a journey and arrive at the destination.
 */
public class EndJourneyCommand extends CommandAbstract {

    public EndJourneyCommand(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "endjourney";
        this.description = "\uD83D\uDDFA\uFE0F Arriver à destination.";
        this.cooldown = 15000L;
    }

    /**
     * Arrives at the destination.
     *
     * @param event The interaction event.
     */
    @Override
    public void execute(SlashCommandInteractionEvent event, String language) {
        PlayerEntity player = stMaryClient.getDatabaseManager().getPlayer(event.getUser().getIdLong());
        UUID uuid = player.getId();
        MoveEntity moves = stMaryClient.getDatabaseManager().getMove(uuid);

        if (moves == null) {
            String text = stMaryClient.getTextManager().createText("endjourney_no_journey").build();
            event.reply(text).queue();
            return;
        }

        long start = moves.getStart();
        long time = moves.getTime() * 60000L;
        long now = System.currentTimeMillis();
        long end = start + time;

        PlaceBase destinationPlace = stMaryClient.getLocationManager().getPlaceByName(moves.getTo());
        PlaceBase fromPlace = stMaryClient.getLocationManager().getPlaceByName(moves.getFrom());
        String formatted = (fromPlace.getTown() == destinationPlace.getTown() || !destinationPlace.isTownPlace()) ?
                this.stMaryClient.getLocationManager().formatLocation(destinationPlace.getName(language), language) :
                this.stMaryClient.getLocationManager().formatLocation(destinationPlace.getTown().getName(language), language);

        // Check if the player has arrived at the destination.
        if (now < end) {
            long remaining = end - now;
            long seconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() + remaining);
            String text = stMaryClient.getTextManager().createText("endjourney_journey_not_finsh").replace("time", String.valueOf(seconds)).replace("location", formatted).build();
            event.reply(text).queue();
            return;
        }

        // Update the player's location and remove the journey record from the database.
        player.setCurrentLocationPlace(destinationPlace.getName(language));
        player.setCurrentLocationRegion(destinationPlace.getRegion().getName(language));
        player.setCurrentLocationTown(destinationPlace.isTownPlace() ? destinationPlace.getTown().getName(language) : "");

        // Update the player's location in the database.
        stMaryClient.getDatabaseManager().delete(moves);
        stMaryClient.getDatabaseManager().update(player);

        // Send the arrival message.
        String text = stMaryClient.getTextManager().createText("endjourney_success").replace("location", formatted).build();
        event.reply(text).queue();
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event, String language) {
        // Auto-complete logic can be added here if needed.
    }
}
