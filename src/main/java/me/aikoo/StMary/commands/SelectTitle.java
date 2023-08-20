package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.database.entities.PlayerEntity;
import me.aikoo.StMary.system.Title;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

/**
 * A command to select and display a title for the user.
 */
public class SelectTitle extends AbstractCommand {

    /**
     * Constructs a SelectTitle command.
     *
     * @param stMaryClient The StMaryClient instance.
     */
    public SelectTitle(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "selecttitle";
        this.description = "\uD83C\uDFC5 Sélectionnez votre titre actuel à afficher.";
        this.cooldown = 15000L;

        // Define the command options
        this.options.add(new OptionData(OptionType.STRING, "title", "Le titre à afficher.")
                .setAutoComplete(true)
                .setRequired(true));
    }

    /**
     * Executes the SelectTitle command.
     *
     * @param client The StMaryClient instance.
     * @param event  The SlashCommandInteractionEvent triggered by the command.
     */
    @Override
    public void execute(StMaryClient client, SlashCommandInteractionEvent event) {
        String titleName = Objects.requireNonNull(event.getOption("title")).getAsString();

        // Check if the selected title exists
        if (client.getTitleManager().getTitle(titleName) == null) {
            String errorText = client.getTextManager().generateError("Séléction du Titre Actuel", "Ce titre n'existe pas.");
            event.reply(errorText).setEphemeral(true).queue();
            return;
        }

        PlayerEntity player = client.getDatabaseManager().getPlayer(event.getUser().getIdLong());
        String title = client.getTextManager().getTitle("select_title");
        String text = client.getTextManager().getText("select_title").replace("{{title}}", client.getTitleManager().getTitle(titleName).format());

        // Perform verifications and update the current title
        if (!verifications(player, titleName, event)) return;

        player.setCurrentTitle(titleName);
        client.getDatabaseManager().update(player);
        event.reply(client.getTextManager().generateScene(title, text)).queue();
    }

    /**
     * Perform verifications before updating the current title.
     *
     * @param player    The player entity.
     * @param titleName The name of the selected title.
     * @param event     The SlashCommandInteractionEvent.
     * @return True if verifications pass, false otherwise.
     */
    public boolean verifications(PlayerEntity player, String titleName, SlashCommandInteractionEvent event) {
        if (player == null) return false;
        HashMap<String, Title> titles = player.getTitles(stMaryClient);

        // Check if the player owns the selected title
        if (!titles.containsKey(titleName)) {
            String errorText = this.stMaryClient.getTextManager().generateError("Séléction du Titre Actuel", "Vous ne possédez pas ce titre.");
            event.reply(errorText).setEphemeral(true).queue();
            return false;
        }

        // Check if the selected title is already the current title
        if (player.getCurrentTitle(stMaryClient).getName().equals(titleName)) {
            String errorText = this.stMaryClient.getTextManager().generateError("Séléction du Titre Actuel", "Ce titre est déjà votre titre actuel.");
            event.reply(errorText).setEphemeral(true).queue();
            return false;
        }

        return true;
    }

    /**
     * Provides auto-complete choices for the title selection.
     *
     * @param client The StMaryClient instance.
     * @param event  The CommandAutoCompleteInteractionEvent triggered by the auto-complete request.
     */
    @Override
    public void autoComplete(StMaryClient client, CommandAutoCompleteInteractionEvent event) {
        PlayerEntity player = client.getDatabaseManager().getPlayer(event.getUser().getIdLong());

        if (player != null) {
            Collection<Title> titles = player.getTitles(stMaryClient).values();
            ArrayList<Command.Choice> choices = new ArrayList<>();

            // Add title choices
            for (Title title : titles) {
                choices.add(new Command.Choice(title.format(), title.getName()));

                if (choices.size() == 24) {
                    choices.add(new Command.Choice("Autre", "Autre"));
                    break;
                }
            }

            event.replyChoices(choices).queue();
        }
    }
}

