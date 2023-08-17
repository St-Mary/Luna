package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.StMaryClient;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Ping extends AbstractCommand {
    public Ping(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "ping";
        this.description = "Pong!";
        this.cooldown = 5000L;
        this.setMustBeRegistered(false);
    }

    @Override
    public void execute(StMaryClient client, SlashCommandInteractionEvent event) {
        event.reply("Pong!").queue();
    }

    @Override
    public void autoComplete(StMaryClient client, CommandAutoCompleteInteractionEvent event) {}
}
