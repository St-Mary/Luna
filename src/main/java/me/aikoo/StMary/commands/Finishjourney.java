package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.database.entities.Moves;
import me.aikoo.StMary.database.entities.Player;
import me.aikoo.StMary.system.Place;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Finishjourney extends AbstractCommand {

    public Finishjourney(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "endjourney";
        this.description = "\uD83D\uDDFA\uFE0F Arriver à destination.";
        this.cooldown = 15000L;
    }

    @Override
    public void execute(StMaryClient client, SlashCommandInteractionEvent event) {
        Player player = client.getDatabaseManager().getPlayer(event.getUser().getIdLong());
        UUID uuid = player.getId();
        Moves moves = client.getDatabaseManager().getMoves(uuid);

        if (moves == null) {
            String text = client.getTextManager().generateScene("Fin de Voyage Impossible", "Vous n'avez aucun voyage en cours.");
            event.reply(text).queue();
            return;
        }

        long start = moves.getStart();
        long time = moves.getTime() * 60000L;
        long now = System.currentTimeMillis();
        long end = start + time;

        Place destinationPlace = client.getLocationManager().getPlace(moves.getTo());
        Place fromPlace = client.getLocationManager().getPlace(moves.getFrom());
        String formatted = (fromPlace.getTown() == destinationPlace.getTown()) ? this.stMaryClient.getTextManager().formatLocation(destinationPlace.getName()) : this.stMaryClient.getTextManager().formatLocation(destinationPlace.getTown().getName());

        if (now < end) {
            long remaining = end - now;
            String text = client.getTextManager().generateScene("Fin de Voyage", String.format("Vous n'avez pas encore fini votre voyage vers **%s**. Veuillez attendre <t:%s:R> avant d'arriver à votre destination", formatted, TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() + remaining)));
            event.reply(text).queue();
            return;
        }

        player.setCurrentLocationPlace(destinationPlace.getName());
        player.setCurrentLocationRegion(destinationPlace.getRegion().getName());
        player.setCurrentLocationTown(destinationPlace.getTown().getName());

        client.getDatabaseManager().delete(moves);
        client.getDatabaseManager().update(player);

        String text = client.getTextManager().generateScene("Rapport de Fin de Voyage", String.format("Vous êtes arrivé à **%s**.", formatted));
        event.reply(text).queue();
    }

    @Override
    public void autoComplete(StMaryClient client, CommandAutoCompleteInteractionEvent event) {

    }
}
