package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.StMaryClient;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Start extends AbstractCommand {

    public Start(StMaryClient stMaryClient) {
        super(stMaryClient);

            this.name = "ping";
            this.description = "Pong!";
            this.cooldown = 5000L;
    }

    @Override
    public void execute(StMaryClient client, SlashCommandInteractionEvent event) {
        event.reply("Pong!").queue();
    }
}
