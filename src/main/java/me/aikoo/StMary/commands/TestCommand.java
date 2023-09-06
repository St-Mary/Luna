package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.abstracts.CommandAbstract;
import me.aikoo.StMary.core.bot.StMaryClient;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * The Test command.
 */
public class TestCommand extends CommandAbstract {

    /**
     * Constructs a Test command.
     * @param stMaryClient The StMaryClient instance.
     */
    public TestCommand(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "test";
        this.description = "Test command";
        this.cooldown = 10000L;

        this.setAdminCommand(true);
    }

    /**
     * Executes the Test command.
     * @param event The SlashCommandInteractionEvent triggered when the button is clicked.
     * @param language The language of the player
     */
    @Override
    public void execute(SlashCommandInteractionEvent event, String language) {
        event.reply("Test command").queue();
    }

    /**
     * Auto complete method for the Test command.
     * @param event The CommandAutoCompleteInteractionEvent triggered when the button is clicked.
     * @param language The language of the player
     */
    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event, String language) {
        // Unused method for this command
    }
}
