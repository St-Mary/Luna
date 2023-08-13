package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.system.places.Town;
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
        Town talon = client.getLocationManager().getTown("Talon");
        event.reply(talon.getPlace("Place du Griffon Marin").getRegion().getDescription()).queue();
    }
}
