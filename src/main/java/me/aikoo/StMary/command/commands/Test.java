package me.aikoo.StMary.command.commands;

import me.aikoo.StMary.command.AbstractCommand;
import me.aikoo.StMary.core.StMaryClient;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Test extends AbstractCommand {
    public Test(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "test";
        this.description = "Test command";
        this.cooldown = 10000L;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.reply("Test command").queue();
    }
}
