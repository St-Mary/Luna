package me.aikoo.StMary.command.commands;

import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.command.AbstractCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Ping extends AbstractCommand {
    public Ping(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "ping";
        this.description = "Pong!";
        this.cooldown = 5000L;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.reply("Pong!").queue();
    }
}