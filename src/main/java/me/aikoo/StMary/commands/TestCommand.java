package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.abstracts.CommandAbstract;
import me.aikoo.StMary.core.bases.CharacterBase;
import me.aikoo.StMary.core.bot.StMaryClient;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class TestCommand extends CommandAbstract {
    public TestCommand(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "test";
        this.description = "Test command";
        this.cooldown = 10000L;

        this.setAdminCommand(true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, String language) {
        CharacterBase.Information character = this.stMaryClient.getCharacterManager().getCharacterByName("Maréchal Thorne", language).getCharacterInformation(language);
        CharacterBase.Dialog dialog = character.getDialog("1");

        event.reply("**" + character.getName() + "** - " + dialog.getText()).queue();
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event, String language) {
    }
}
