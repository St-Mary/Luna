package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.abstracts.CommandAbstract;
import me.aikoo.StMary.core.bot.StMaryClient;
import me.aikoo.StMary.core.database.PlayerEntity;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class LanguageCommand extends CommandAbstract {
    /**
     * Constructor for the AbstractCommand class
     *
     * @param stMaryClient The client instance
     */
    public LanguageCommand(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "language";
        this.description = "Change the language of the bot";
        this.cooldown = 10000L;

        Command.Choice fr = new Command.Choice("\uD83C\uDDEB\uD83C\uDDF7 Français", "fr");
        Command.Choice en = new Command.Choice("\uD83C\uDDEC\uD83C\uDDE7 English", "en");

        this.options.add(new OptionData(OptionType.STRING, "language", stMaryClient.getTextManager().getText("language_cmd_desc", "en"))
                .setDescriptionLocalization(DiscordLocale.FRENCH, stMaryClient.getTextManager().getText("language_cmd_desc", "fr"))
                .addChoices(fr, en)
                .setRequired(true)
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, String language) {
        PlayerEntity player = stMaryClient.getDatabaseManager().getPlayer(event.getUser().getIdLong());
        String newLanguage = event.getOption("language").getAsString();

        if (player == null) {
            event.reply(stMaryClient.getTextManager().createText("language_cmd_error_not_started", language).buildError()).setEphemeral(true).queue();
            return;
        }

        if (player.getLanguage().equals(newLanguage)) {
            event.reply(stMaryClient.getTextManager().createText("language_cmd_error_already_set", language).buildError()).setEphemeral(true).queue();
            return;
        }

        player.setLanguage(newLanguage);
        stMaryClient.getDatabaseManager().update(player);

        event.reply(stMaryClient.getTextManager().createText("language_cmd_success", newLanguage).build()).queue();
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event, String language) {

    }
}
