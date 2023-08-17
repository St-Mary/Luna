package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.database.entities.Player;
import me.aikoo.StMary.system.Title;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class SelectTitle extends AbstractCommand {

    public SelectTitle(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "selecttitle";
        this.description = "Select your current title to display";
        this.cooldown = 10000L;
        this.options.add(new OptionData(OptionType.STRING, "title", "Select your current title to display")
                .setAutoComplete(true)
                .setRequired(true));
    }

    @Override
    public void execute(StMaryClient client, SlashCommandInteractionEvent event) {
        String titleName = event.getOption("title").getAsString();
        Player player = client.getDatabaseManager().getPlayer(event.getUser().getIdLong());
        if (player != null) {
            HashMap<String, Title> titles = player.getTitles(stMaryClient);
            if (titles.containsKey(titleName)) {
                player.setCurrentTitle(titleName);
                client.getDatabaseManager().update(player);
                String title = client.getTextManager().getTitle("select_title");
                String text = client.getTextManager().getText("select_title").replace("{{title}}", client.getTitleManager().getTitle(titleName).format());
                event.reply(client.getTextManager().generateScene(title, text)).queue();
            } else {
                event.reply("You don't have this title").queue();
            }
        }
    }

    @Override
    public void autoComplete(StMaryClient client, CommandAutoCompleteInteractionEvent event) {
        Player player = client.getDatabaseManager().getPlayer(event.getUser().getIdLong());
        if (player != null) {
            Collection<Title> titles = player.getTitles(stMaryClient).values();
            ArrayList<Command.Choice> choices = new ArrayList<>();

            for (Title title : titles) {
                choices.add(new Command.Choice(title.format(), title.getName()));

                if (choices.size() == 24) {
                    choices.add(new Command.Choice("Other", "Other"));
                    break;
                }
            }

            event.replyChoices(choices).queue();
        }
    }
}
