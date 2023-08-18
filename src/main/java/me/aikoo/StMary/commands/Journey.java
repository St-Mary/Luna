package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.database.entities.Moves;
import me.aikoo.StMary.database.entities.Player;
import me.aikoo.StMary.system.Button;
import me.aikoo.StMary.system.Place;
import net.dv8tion.jda.api.EmbedBuilder;
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

public class Journey extends AbstractCommand {

    private boolean isStarted = false;

    public Journey(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "journey";
        this.description = "\uD83D\uDDFA\uFE0F Se déplacer vers une autre destination.";
        this.cooldown = 15000L;

        this.options.add(new OptionData(OptionType.STRING, "destination", "La destination où aller").setAutoComplete(true).setRequired(true));
    }

    @Override
    public void execute(StMaryClient client, SlashCommandInteractionEvent event) {
         String destination = event.getOption("destination").getAsString();
         Player player = client.getDatabaseManager().getPlayer(event.getUser().getIdLong());
         Moves moves = client.getDatabaseManager().getMoves(player.getId());
         Place place = client.getLocationManager().getPlace(player.getCurrentLocationPlace());
         Place destinationPlace = client.getLocationManager().getPlace(destination);

         if (moves != null) {
             Place toPlace = client.getLocationManager().getPlace(moves.getTo());
             String formattedText = (place.getTown() == toPlace.getTown()) ? toPlace.getIcon() + toPlace.getName() : toPlace.getTown().getIcon() + toPlace.getTown().getName();
             String text = client.getTextManager().generateScene("Voyage en Cours", "Vous êtes déjà en voyage vers **" + formattedText + "**.\n\nUtilisez la commande `/endjourney` pour terminer votre voyage, ou voir le temps restant.");
             event.reply(text).queue();
             return;
         }

         if (place == null || destinationPlace == null) {
             EmbedBuilder error = client.getTextManager().generateErrorEmbed("Voyage Impossible", "La destination n'existe pas.");
             event.replyEmbeds(error.build()).queue();
             return;
         }

         me.aikoo.StMary.system.Move move = place.getMove(destination);

         if (!place.getAvailableMoves().contains(move)) {
             EmbedBuilder error = client.getTextManager().generateErrorEmbed("Voyage Impossible", "Vous ne pouvez pas vous déplacer vers cette destination.");
             event.replyEmbeds(error.build()).queue();
             return;
         }

         ConfirmBtn confirmBtn = new ConfirmBtn(client, move, player);
         CloseBtn closeBtn = new CloseBtn(destinationPlace);
         this.buttons.put(confirmBtn.getId(), confirmBtn);
         this.buttons.put(closeBtn.getId(), closeBtn);

         long time = move.getTime();

         String formattedText = (place.getTown() == destinationPlace.getTown()) ? destinationPlace.getIcon() + destinationPlace.getName() : destinationPlace.getTown().getIcon() + destinationPlace.getTown().getName();
         String str = client.getTextManager().generateScene("Voyage", "Êtes-vous sûr de vouloir vous déplacer vers **" + formattedText + "** en** `" + time + " minutes` **?");
         event.reply(str).addActionRow(confirmBtn.getButton(), closeBtn.getButton()).queue(msg -> msg.retrieveOriginal().queue(res -> {
             stMaryClient.getButtonManager().addButtons(res.getId(), this.getArrayListButtons());
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

    private void close(Message message, Place destinationPlace) {
        String text = stMaryClient.getTextManager().generateScene("Annulation du voyage", "Le voyage vers **" + destinationPlace.getIcon() + destinationPlace.getName() + "** a été annulé.");
        List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = message.getButtons();
        buttons.replaceAll(net.dv8tion.jda.api.interactions.components.buttons.Button::asDisabled);

        message.editMessage(text).setActionRow(buttons).queue();
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
        for (me.aikoo.StMary.system.Move move : place.getAvailableMoves()) {
            Place destination = client.getLocationManager().getPlace(move.getTo().getName());
            String name = (place.getTown() == destination.getTown()) ? destination.getIcon() + " " + destination.getName() : destination.getTown().getIcon() + " " + destination.getTown().getName();
            choices.add(new Command.Choice(name, move.getTo().getName()));
        }

        event.replyChoices(choices).queue();
    }

    private class ConfirmBtn extends Button {

            private final me.aikoo.StMary.system.Move move;
            private final Player player;

            public ConfirmBtn(StMaryClient stMaryClient, me.aikoo.StMary.system.Move move, Player player) {
                super("confirm_move", "Confirmer", ButtonStyle.SUCCESS, Emoji.fromUnicode("\uD83D\uDDFA\uFE0F"), stMaryClient);

                this.move = move;
                this.player = player;
            }

            @Override
            public void onClick(ButtonInteractionEvent event) {
                Place oldPlace = stMaryClient.getLocationManager().getPlace(player.getCurrentLocationPlace());
                Place destinationPlace = stMaryClient.getLocationManager().getPlace(move.getTo().getName());
                Moves moves = new Moves();
                moves.setPlayerId(player.getId());
                moves.setFrom(move.getFrom().getName());
                moves.setTo(move.getTo().getName());
                moves.setTime(move.getTime());
                moves.setStart(System.currentTimeMillis());

                stMaryClient.getDatabaseManager().createOrUpdate(moves);
                isStarted = true;

                String formattedText = (oldPlace.getTown() == destinationPlace.getTown()) ? destinationPlace.getIcon() + destinationPlace.getName() : destinationPlace.getTown().getIcon() + destinationPlace.getTown().getName();

                String text = stMaryClient.getTextManager().generateScene("Voyage", "Vous voyagez vers **" + formattedText + "**. Ce déplacement prendra `" + move.getTime() + "` minutes.\n\nUtilisez la commande `/endjourney` pour terminer votre voyage ou voir le temps restant.");
                List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = event.getMessage().getButtons();
                buttons.replaceAll(net.dv8tion.jda.api.interactions.components.buttons.Button::asDisabled);

                event.getMessage().editMessage(text).setActionRow(buttons).queue();
                event.deferEdit().queue();
            }
    }

    private class CloseBtn extends Button {

        private final Place destinationPlace;
        public CloseBtn(Place destinationPlace) {
            super("close_btn", "Annuler", ButtonStyle.DANGER, Emoji.fromUnicode("❌"), stMaryClient);
            this.destinationPlace = destinationPlace;
        }

        @Override
        public void onClick(ButtonInteractionEvent event) {
            close(event.getMessage(), destinationPlace);
            event.deferEdit().queue();
        }
    }
}
