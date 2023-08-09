package me.aikoo.StMary.command.commands;

import me.aikoo.StMary.command.AbstractCommand;
import me.aikoo.StMary.core.DatabaseManager;
import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.database.entities.Player;
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
        // Check if player exists in database
        Player playerT = DatabaseManager.getPlayer(event.getUser().getIdLong());

        if (playerT == null) {
            Player player = new Player();
            player.setDiscordId(event.getUser().getIdLong());

            DatabaseManager.save(player);

            event.reply("Test command!").queue();
        } else {
            event.reply("Player found!").queue();
        }
    }
}
