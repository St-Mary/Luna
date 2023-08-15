package me.aikoo.StMary.commands;

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
    public void execute(StMaryClient client, SlashCommandInteractionEvent event) {
        String text = client.getTextManager().getText("start_adventure");
        String title = client.getTextManager().getTitle("start_adventure");
        event.reply(client.getTextManager().generateScene(title, text)).queue();
    }
}
