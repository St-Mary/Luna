package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.abstracts.CommandAbstract;
import me.aikoo.StMary.core.bot.StMaryClient;
import me.aikoo.StMary.core.database.PlayerEntity;
import me.aikoo.StMary.core.managers.DatabaseManager;
import me.aikoo.StMary.core.managers.TextManager;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Objects;

public class LanguageCommand extends CommandAbstract {
    /**
     * Constructor for the AbstractCommand class
     *
     * @param stMaryClient The client instance
     */
    public LanguageCommand(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "language";
        this.description = "\uD83D\uDDE3\uFE0F Change the bot language";
        this.cooldown = 10000L;

        Command.Choice fr = new Command.Choice("\uD83C\uDDEB\uD83C\uDDF7 Français", "fr");
        Command.Choice en = new Command.Choice("\uD83C\uDDEC\uD83C\uDDE7 English", "en");

        this.options.add(new OptionData(OptionType.STRING, "language", "The new language")
                .addChoices(fr, en)
                .setRequired(true)
        );
    }

    /**
     * Execute the command
     *
     * @param event    The event
     * @param language The language of the user
     */
    @Override
    public void execute(SlashCommandInteractionEvent event, String language) {
        PlayerEntity player = DatabaseManager.getPlayer(event.getUser().getIdLong());
        String newLanguage = Objects.requireNonNull(event.getOption("language")).getAsString();

        if (player == null) {
            event.reply(TextManager.createText("language_cmd_error_not_started", language).buildError()).setEphemeral(true).queue();
            return;
        }

        if (player.getLanguage().equals(newLanguage)) {
            event.reply(TextManager.createText("language_cmd_error_already_set", language).buildError()).setEphemeral(true).queue();
            return;
        }

        player.setLanguage(newLanguage);
        DatabaseManager.update(player);

        event.reply(TextManager.createText("language_cmd_success", newLanguage).build()).queue();
    }

    /**
     * Auto complete the command
     *
     * @param event    The event
     * @param language The language of the user
     */
    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event, String language) {
        // Unused method for this command
    }
}
