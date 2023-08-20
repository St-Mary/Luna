package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.database.entities.PlayerEntity;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * The Start command allows users to begin their adventure.
 */
public class StartCommand extends AbstractCommand {

    /**
     * Creates a new Start command.
     *
     * @param stMaryClient The StMaryClient instance.
     */
    public StartCommand(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "start";
        this.description = "\uD83C\uDF32 Démarrer l'aventure !";
        this.cooldown = 10000L;
        this.setMustBeRegistered(false);
    }

    /**
     * Executes the Start command.
     *
     * @param client The StMaryClient instance.
     * @param event  The SlashCommandInteractionEvent.
     */
    @Override
    public void execute(StMaryClient client, SlashCommandInteractionEvent event) {
        PlayerEntity player = client.getDatabaseManager().getPlayer(event.getUser().getIdLong());
        LocalDate creationDate = event.getUser().getTimeCreated().toLocalDateTime().toLocalDate();

        // Check if the Discord account was created more than a week ago
        if (creationDate.isAfter(LocalDate.now().minusWeeks(1))) {
            event.reply(client.getTextManager().generateError("Création du Joueur", "Vous devez avoir un compte Discord créé depuis plus d'une semaine pour pouvoir jouer.")).queue();
            return;
        }

        if (player == null) {
            // Create a new player entity
            player = new PlayerEntity();
            player.setLevel(1);
            player.setExperience(BigInteger.valueOf(0));
            player.setMoney(BigInteger.valueOf(0));
            player.setDiscordId(event.getUser().getIdLong());
            player.setTitles(List.of(new String[]{"Néophyte"}), client);
            player.setCreationTimestamp(new Date());
            player.setCurrentLocationRegion("Oraland");
            player.setCurrentLocationTown("Talon");
            player.setCurrentLocationPlace("Place du Griffon Marin");
            player.setCurrentTitle("Néophyte");
            player.setMagicalBook("book_I");

            // Save the new player entity to the database
            client.getDatabaseManager().createOrUpdate(player);

            // Send a welcome message
            String text = client.getTextManager().getText("start_adventure");
            String title = client.getTextManager().getTitle("start_adventure");
            event.reply(client.getTextManager().generateScene(title, text)).queue();
        } else {
            // Player already exists, send an error message
            String error = client.getTextManager().generateError("Démarrage de l'aventure", "Vous ne pouvez pas recommencer l'aventure !");
            event.reply(error).queue();
        }
    }

    /**
     * Auto-complete method for the Start command.
     *
     * @param client The StMaryClient instance.
     * @param event  The CommandAutoCompleteInteractionEvent.
     */
    @Override
    public void autoComplete(StMaryClient client, CommandAutoCompleteInteractionEvent event) {}
}
