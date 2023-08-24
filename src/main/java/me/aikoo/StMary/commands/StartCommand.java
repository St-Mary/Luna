package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.bot.StMaryClient;
import me.aikoo.StMary.core.abstracts.CommandAbstract;
import me.aikoo.StMary.core.constants.PlayerConstant;
import me.aikoo.StMary.core.database.PlayerEntity;
import me.aikoo.StMary.core.managers.TextManager;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.LocalDate;
import java.util.Date;

/**
 * The Start command allows users to begin their adventure.
 */
public class StartCommand extends CommandAbstract {

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

        this.options.add(new OptionData(OptionType.STRING, "language", "The language to use")
                        .addChoice("\uD83C\uDDEB\uD83C\uDDF7 Français", "fr")
                        .addChoice("\uD83C\uDDEC\uD83C\uDDE7 English", "en")
                        .setRequired(true)
        );
    }

    /**
     * Executes the Start command.
     *
     * @param event The SlashCommandInteractionEvent.
     */
    @Override
    public void execute(SlashCommandInteractionEvent event, String language) {
        PlayerEntity player = stMaryClient.getDatabaseManager().getPlayer(event.getUser().getIdLong());
        LocalDate creationDate = event.getUser().getTimeCreated().toLocalDateTime().toLocalDate();
        language = event.getOption("language").getAsString();

        // Check if the Discord account was created more than a week ago
        if (creationDate.isAfter(LocalDate.now().minusWeeks(PlayerConstant.CREATION_TIME_WEEK_LIMIT))) {
            event.reply(stMaryClient.getTextManager().createText("start_adventure_error_creation_date", language).buildError()).queue();
            return;
        }

        if (player != null) {
            // Player already exists, send an error message
            String error = stMaryClient.getTextManager().createText("start_adventure_error_already_started", language).buildError();
            event.reply(error).setEphemeral(true).queue();
            return;
        }

        // Create a new player entity
        player = new PlayerEntity();
        player.setDiscordId(event.getUser().getIdLong());
        player.setLevel(PlayerConstant.LEVEL);
        player.setExperience(PlayerConstant.EXPERIENCE);
        player.setMoney(PlayerConstant.MONEY);
        player.setTitles(PlayerConstant.TITLES, stMaryClient);
        player.setCurrentLocationRegion(PlayerConstant.CURRENT_LOCATION_REGION);
        player.setCurrentLocationTown(PlayerConstant.CURRENT_LOCATION_TOWN);
        player.setCurrentLocationPlace(PlayerConstant.CURRENT_LOCATION_PLACE);
        player.setCurrentTitle(PlayerConstant.CURRENT_TITLE);
        player.setMagicalBook(PlayerConstant.MAGICAL_BOOK);
        player.setLanguage(language);
        player.setCreationTimestamp(new Date());

        // Save the new player entity to the database
        stMaryClient.getDatabaseManager().save(player);

        TextManager.Text text = stMaryClient.getTextManager().createText("start_adventure", language);
        event.reply(text.build()).queue();
    }

    /**
     * Auto-complete method for the Start command.
     *
     * @param event The CommandAutoCompleteInteractionEvent.
     */
    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event, String language) {
    }
}
