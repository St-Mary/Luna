package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.abstracts.CommandAbstract;
import me.aikoo.StMary.core.bot.StMaryClient;
import me.aikoo.StMary.core.database.PlayerEntity;
import me.aikoo.StMary.core.managers.DatabaseManager;
import me.aikoo.StMary.core.managers.TitleManager;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Objects;

public class AddTitleCommand extends CommandAbstract {
    public AddTitleCommand(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "addtitle";
        this.description = "Add a title to a user";
        this.cooldown = 10000L;
        this.setMustBeRegistered(false);
        this.setAdminCommand(true);

        this.options.add(new OptionData(OptionType.STRING, "title", "The title id")
                .setRequired(true));
        this.options.add(new OptionData(OptionType.STRING, "userid", "The user to add title")
                .setRequired(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, String language) {
        if (!event.getUser().getId().equals("985986599995187270")) return;
        String titleId = Objects.requireNonNull(event.getOption("title")).getAsString();
        String userId = Objects.requireNonNull(event.getOption("userid")).getAsString();

        if (!userId.matches("[0-9]+")) {
            event.reply("This user doesn't exist").queue();
            return;
        }

        PlayerEntity player = DatabaseManager.getPlayer(Objects.requireNonNull(event.getOption("userid")).getAsLong());

        if (player == null) {
            event.reply("This user doesn't exist").queue();
            return;
        }

        if (TitleManager.getTitle(titleId) == null) {
            event.reply("This title doesn't exist").queue();
            return;
        }
        player.addTitle(titleId, stMaryClient);
        DatabaseManager.update(player);

        event.reply("The title has been added to the user").queue();
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event, String language) {
        // Unused method for this command
    }
}
