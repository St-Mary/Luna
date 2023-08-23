package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.core.abstracts.CommandAbstract;
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

        this.options.add(new OptionData(OptionType.STRING, "destination", "Test option").setAutoComplete(true).setRequired(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.reply("Test <:book_1:1142570454834499724>").queue();
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event) {
    }
}
