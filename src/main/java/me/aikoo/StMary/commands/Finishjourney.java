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
            EmbedBuilder error = client.getTextManager().generateErrorEmbed("Fin de Voyage", "Vous n'avez pas de déplacement en cours.");
            event.replyEmbeds(error.build()).queue();
            return;
        }

        // check if move is finished
        long start = moves.getStart();
        long time = moves.getTime() * 60000L;
        long now = System.currentTimeMillis();
        long end = start + time;

        if (now < end) {
            long remaining = end - now;
            String text = client.getTextManager().generateScene("Fin de Voyage", String.format("Vous n'avez pas encore fini votre déplacement. Veuillez attend <t:%s:R> avant d'arriver à votre destination", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() + remaining)));
            event.reply(text).queue();
            return;
        }

        Place destinationPlace = client.getLocationManager().getPlace(moves.getTo());

        player.setCurrentLocationPlace(destinationPlace.getName());
        player.setCurrentLocationRegion(destinationPlace.getRegion().getName());
        player.setCurrentLocationTown(destinationPlace.getTown().getName());

        client.getDatabaseManager().delete(moves);
        client.getDatabaseManager().update(player);

        String text = client.getTextManager().generateScene("Rapport de Fin de Voyage", String.format("Vous êtes arrivé à **%s**.", destinationPlace.getIcon() + destinationPlace.getName()));
        event.reply(text).queue();
    }

    @Override
    public void autoComplete(StMaryClient client, CommandAutoCompleteInteractionEvent event) {

    }
}
