package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.database.entities.Player;
import me.aikoo.StMary.system.Place;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;

public class Move extends AbstractCommand {

    public Move(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "move";
        this.description = "\uD83D\uDDFA\uFE0F Se déplacer vers une autre destination.";
        this.cooldown = 15000L;

        this.options.add(new OptionData(OptionType.STRING, "destination", "La destination où aller").setAutoComplete(true).setRequired(true));
    }

    @Override
    public void execute(StMaryClient client, SlashCommandInteractionEvent event) {
         String destination = event.getOption("destination").getAsString();
         Player player = client.getDatabaseManager().getPlayer(event.getUser().getIdLong());
         Place place = client.getLocationManager().getPlace(player.getCurrentLocationPlace());
         Place destinationPlace = client.getLocationManager().getPlace(destination);

         if (place == null || destinationPlace == null) {
             event.reply("La destination n'existe pas.").queue();
             return;
         }

         if (!place.getAvailableMoves().contains(destination)) {
             event.reply("Vous ne pouvez pas vous déplacer vers cette destination.").queue();
             return;
         }

         // TODO: Confirm move
         // TODO: Move table with duration in miilliiiiseeeecoondes
    }

    @Override
    public void autoComplete(StMaryClient client, CommandAutoCompleteInteractionEvent event) {
        Player player = client.getDatabaseManager().getPlayer(event.getUser().getIdLong());
        if (player == null) {
            return;
        }

        Place place = client.getLocationManager().getPlace(player.getCurrentLocationPlace());
        if (place == null) {
            return;
        }

        if (place.getAvailableMoves().isEmpty()) {
            return;
        }

        ArrayList<Command.Choice> choices = new ArrayList<>();
        for (String move : place.getAvailableMoves()) {
            Place destination = client.getLocationManager().getPlace(move);
            choices.add(new Command.Choice(destination.getIcon() + " " + destination.getName(), move));
        }

        event.replyChoices(choices).queue();
    }
}
