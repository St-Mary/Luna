package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.system.Object;
import me.aikoo.StMary.system.Title;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.Objects;

public class Info extends AbstractCommand {
    public Info(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "info";
        this.description = "\uD83D\uDDDE\uFE0F Obtenir des informations sur un élément du jeu.";
        this.cooldown = 3000L;

        this.options.add(new OptionData(OptionType.STRING, "element", "L'élément sur lequel vous souhaitez obtenir des informations.", true)
                .addChoice("\uD83D\uDDE1\uFE0F Objet", "object")
                .addChoice("\uD83C\uDF32 Lieu", "place")
                .addChoice("\uD83E\uDDD9 Personnage", "character")
                .addChoice("\uD83D\uDC17 Monstre", "monster")
                .addChoice("\uD83D\uDCDD Quête", "quest")
                .addChoice("\uD83C\uDFC5 Titre", "title")
        );
        this.options.add(new OptionData(OptionType.STRING, "name", "Le nom de l'élément sur lequel vous souhaitez obtenir des informations.", false));
    }

    @Override
    public void execute(StMaryClient client, SlashCommandInteractionEvent event) {
        String element = (event.getOption("element") != null) ? event.getOption("element").getAsString() : null;
        String name = (event.getOption("name") != null) ? event.getOption("name").getAsString() : null;

        switch (element) {
            case "object" -> {
                infoObject(client, event, name);
            }
            case "place" -> {
                infoPlace(client, event, name);
            }
            case "character" -> {
                infoCharacter(client, event, name);
            }
            case "monster" -> {
                infoMonster(client, event, name);
            }
            case "quest" -> {
                infoQuest(client, event, name);
            }
            case "title" -> {
                infoTitle(client, event, name);
            }
            default -> {
                String error = client.getTextManager().generateError("Informations", "L'élément demandé n'existe pas.");
                event.reply(error).queue();
            }
        }
    }

    private void infoPlace(StMaryClient client, SlashCommandInteractionEvent event, String name) {
        String text = client.getTextManager().generateScene("Information à propos d'un lieu", "Cette page n'est pas encore disponible !");
        event.reply(text).queue();
    }

    private void infoObject(StMaryClient client, SlashCommandInteractionEvent event, String name) {
        if (name == null) {
            String text = client.getTextManager().generateError("Information à propos d'un objet", "Veuillez entrer le nom de l'objet que vous souhaitez consulter.");
            event.reply(text).queue();
            return;
        }

        Object object = client.getObjectManager().getObjectByName(name);

        if (object == null) {
            String error = client.getTextManager().generateError("Information à propos d'un objet", "L'objet demandé n'existe pas.");
            event.reply(error).queue();
            return;
        }

        String description = String.format("**Nom :** %s `%s`\n\n**Type :** `%s`\n\n**Description de l'objet:** `%s`", object.getIcon(), object.getName(), object.getType().getId(), object.getDescription());
        String text = client.getTextManager().generateScene("Information à propos d'un objet", description);

        event.reply(text).queue();
    }

    private void infoTitle(StMaryClient client, SlashCommandInteractionEvent event, String name) {
        String text = (name == null) ? infoAllTitles(client) : infoOneTitle(client, name);
        event.reply(text).queue();
    }

    private void infoCharacter(StMaryClient client, SlashCommandInteractionEvent event, String name) {
        String text = client.getTextManager().generateScene("Information à propos d'un personnage", "Cette page n'est pas encore disponible !");
        event.reply(text).queue();
    }

    private void infoMonster(StMaryClient client, SlashCommandInteractionEvent event, String name) {
        String text = client.getTextManager().generateScene("Information à propos d'un monstre", "Cette page n'est pas encore disponible !");
        event.reply(text).queue();
    }

    private void infoQuest(StMaryClient client, SlashCommandInteractionEvent event, String name) {
        String text = client.getTextManager().generateScene("Information à propos d'une quête", "Cette page n'est pas encore disponible !");
        event.reply(text).queue();
    }

    private String infoAllTitles(StMaryClient client) {
        ArrayList<Title> titles = new ArrayList<>(client.getTitleManager().getTitles().values());
        StringBuilder stringBuilder = new StringBuilder();

        for (Title title : titles) {
            stringBuilder.append(String.format("%s | **%s** - `%s`", title.getIcon(), title.getName(), title.getDescription()));

            if (titles.indexOf(title) != titles.size() - 1) {
                stringBuilder.append("\n\n");
            }
        }

        return client.getTextManager().generateScene("Titres", stringBuilder.toString());
    }

    private String infoOneTitle(StMaryClient client, String name) {
        Title title = client.getTitleManager().getTitle(name);

        if (title == null) {
            return client.getTextManager().generateError("Titre", "Le titre demandé n'existe pas.");
        }

        return client.getTextManager().generateScene("Titre", String.format("%s | **%s** - `%s`", title.getIcon(), title.getName(), title.getDescription()));
    }

    @Override
    public void autoComplete(StMaryClient client, CommandAutoCompleteInteractionEvent event) {

    }
}
