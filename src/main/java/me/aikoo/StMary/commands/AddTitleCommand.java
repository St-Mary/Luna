package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.database.entities.PlayerEntity;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class AddTitleCommand extends AbstractCommand {
    public AddTitleCommand(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "adminaddtitle";
        this.description = "Add a title to a user";
        this.cooldown = 10000L;
        this.setMustBeRegistered(false);
        this.setAdminCommand(true);

        this.options.add(new OptionData(OptionType.STRING, "title", "Add a title to a user")
                .setRequired(true));
        this.options.add(new OptionData(OptionType.STRING, "userid", "Add a title to a user")
                .setRequired(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!event.getUser().getId().equals("985986599995187270")) return;
        String titleName = event.getOption("title").getAsString();
        String userId = event.getOption("userid").getAsString();

        if (!userId.matches("[0-9]+")) {
            event.reply("This user doesn't exist").queue();
            return;
        }

        PlayerEntity player = stMaryClient.getDatabaseManager().getPlayer(event.getOption("userid").getAsLong());

        if (player == null) {
            event.reply("This user doesn't exist").queue();
            return;
        }

        if (stMaryClient.getTitleManager().getTitle(titleName) == null) {
            event.reply("This title doesn't exist").queue();
            return;
        }
        player.addTitle(titleName, stMaryClient);
        stMaryClient.getDatabaseManager().update(player);

        event.reply("The title has been added to the user").queue();
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event) {}
}
