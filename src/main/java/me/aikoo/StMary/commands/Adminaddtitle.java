package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.core.managers.DatabaseManager;
import me.aikoo.StMary.database.entities.Player;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class Adminaddtitle extends AbstractCommand {
    public Adminaddtitle(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "adminaddtitle";
        this.description = "Add a title to a user";
        this.cooldown = 10000L;
        this.setMustBeRegistered(false);

        this.options.add(new OptionData(OptionType.STRING, "title", "Add a title to a user")
                .setRequired(true));
        this.options.add(new OptionData(OptionType.STRING, "userid", "Add a title to a user")
                .setRequired(true));
    }

    @Override
    public void execute(StMaryClient client, SlashCommandInteractionEvent event) {
        if (!event.getUser().getId().equals("985986599995187270")) return;
        String titleName = event.getOption("title").getAsString();
        Player player = client.getDatabaseManager().getPlayer(event.getOption("userid").getAsLong());
        if (client.getTitleManager().getTitle(titleName) == null) {
            event.reply("This title doesn't exist").queue();
            return;
        }
        player.addTitle(titleName);
        client.getDatabaseManager().update(player);

        event.reply("The title has been added to the user").queue();
    }

    @Override
    public void autoComplete(StMaryClient client, CommandAutoCompleteInteractionEvent event) {}
}
