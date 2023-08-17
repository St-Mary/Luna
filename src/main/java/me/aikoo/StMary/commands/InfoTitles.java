package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.system.Title;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;

public class InfoTitles extends AbstractCommand {
    public InfoTitles(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "infotitles";
        this.description = "\uD83C\uDFC5 Afficher les informations sur les titres disponibles";
        this.cooldown = 5000L;
    }

    @Override
    public void execute(StMaryClient client, SlashCommandInteractionEvent event) {
        ArrayList<Title> titles = new ArrayList<>(client.getTitleManager().getTitles().values());
        StringBuilder stringBuilder = new StringBuilder();

        for (Title title : titles) {
            stringBuilder.append(String.format("%s | **%s** - `%s`", title.getIcon(), title.getName(), title.getDescription()));

            if (titles.indexOf(title) != titles.size() - 1) {
                stringBuilder.append("\n\n");
            }
        }

        String message = client.getTextManager().generateScene("Titres", stringBuilder.toString());
        event.reply(message).queue();
    }

    @Override
    public void autoComplete(StMaryClient client, CommandAutoCompleteInteractionEvent event) {

    }
}
