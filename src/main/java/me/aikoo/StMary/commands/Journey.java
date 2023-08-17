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
             EmbedBuilder error = client.getTextManager().generateErrorEmbed("Voyage", "Vous avez déjà un déplacement en cours.");
             event.replyEmbeds(error.build()).queue();
             return;
         }

         if (place == null || destinationPlace == null) {
             event.reply("La destination n'existe pas.").queue();
             return;
         }

         me.aikoo.StMary.system.Move move = place.getMove(destination);

         if (!place.getAvailableMoves().contains(move)) {
             event.reply("Vous ne pouvez pas vous déplacer vers cette destination.").queue();
             return;
         }

         ConfirmBtn confirmBtn = new ConfirmBtn(client, move, player);
         CloseBtn closeBtn = new CloseBtn(destinationPlace);
         this.buttons.put(confirmBtn.getId(), confirmBtn);
         this.buttons.put(closeBtn.getId(), closeBtn);

         long time = move.getTime();

         String str = client.getTextManager().generateScene("Déplacement", "Êtes-vous sûr de vouloir vous déplacer vers **" + destinationPlace.getIcon() + destinationPlace.getName() + "** en** `" + time + " minutes` **?");
         event.reply(str).addActionRow(confirmBtn.getButton(), closeBtn.getButton()).queue(msg -> msg.retrieveOriginal().queue(res -> {
             stMaryClient.getButtonManager().addButtons(res.getId(), this.getArrayListButtons());
             new java.util.Timer().schedule(
                     new java.util.TimerTask() {
                         @Override
                         public void run() {close(res, destinationPlace);    }
                     },
                     20000
             );
         }));
    }

    private void close(Message message, Place destinationPlace) {
        String text = stMaryClient.getTextManager().generateScene("Annulation du déplacement", "Le déplacement vers **" + destinationPlace.getIcon() + destinationPlace.getName() + "** a été annulé.");
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
            choices.add(new Command.Choice(destination.getIcon() + " " + destination.getName(), move.getTo().getName()));
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
                Moves moves = new Moves();
                moves.setPlayerId(player.getId());
                moves.setFrom(move.getFrom().getName());
                moves.setTo(move.getTo().getName());
                moves.setTime(move.getTime());
                moves.setStart(System.currentTimeMillis());

                stMaryClient.getDatabaseManager().createOrUpdate(moves);

                String text = stMaryClient.getTextManager().generateScene("Déplacement", "Vous vous déplacez vers **" + move.getTo().getIcon() + move.getTo().getName() + "**. Ce déplacement prendra `" + move.getTime() + "` minutes.");
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
