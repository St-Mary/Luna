package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.database.entities.MoveEntity;
import me.aikoo.StMary.database.entities.PlayerEntity;
import me.aikoo.StMary.core.system.Place;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * A command to finish a journey and arrive at the destination.
 */
public class EndJourneyCommand extends AbstractCommand {

    public EndJourneyCommand(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "endjourney";
        this.description = "\uD83D\uDDFA\uFE0F Arriver à destination.";
        this.cooldown = 15000L;
    }

    /**
     * Arrives at the destination.
     * @param client The StMaryClient instance.
     * @param event  The interaction event.
     */
    @Override
    public void execute(StMaryClient client, SlashCommandInteractionEvent event) {
        PlayerEntity player = client.getDatabaseManager().getPlayer(event.getUser().getIdLong());
        UUID uuid = player.getId();
        MoveEntity moves = client.getDatabaseManager().getMove(uuid);

        if (moves == null) {
            String text = client.getTextManager().createText("endjourney_no_journey").build();
            event.reply(text).queue();
            return;
        }

        long start = moves.getStart();
        long time = moves.getTime() * 60000L;
        long now = System.currentTimeMillis();
        long end = start + time;

        Place destinationPlace = client.getLocationManager().getPlace(moves.getTo());
        Place fromPlace = client.getLocationManager().getPlace(moves.getFrom());
        String formatted = (fromPlace.getTown() == destinationPlace.getTown() || !destinationPlace.isTownPlace()) ?
                this.stMaryClient.getLocationManager().formatLocation(destinationPlace.getName()) :
                this.stMaryClient.getLocationManager().formatLocation(destinationPlace.getTown().getName());

        // Check if the player has arrived at the destination.
        if (now < end) {
            long remaining = end - now;
            long seconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() + remaining);
            String text = client.getTextManager().createText("endjourney_journey_not_finsh").replace("time", String.valueOf(seconds)).replace("location", formatted).build();
            event.reply(text).queue();
            return;
        }

        // Update the player's location and remove the journey record from the database.
        player.setCurrentLocationPlace(destinationPlace.getName());
        player.setCurrentLocationRegion(destinationPlace.getRegion().getName());
        player.setCurrentLocationTown(destinationPlace.isTownPlace() ? destinationPlace.getTown().getName() : "");

        // Update the player's location in the database.
        client.getDatabaseManager().delete(moves);
        client.getDatabaseManager().update(player);

        // Send the arrival message.
        String text = client.getTextManager().createText("endjourney_success").replace("location", formatted).build();
        event.reply(text).queue();
    }

    @Override
    public void autoComplete(StMaryClient client, CommandAutoCompleteInteractionEvent event) {
        // Auto-complete logic can be added here if needed.
    }
}
