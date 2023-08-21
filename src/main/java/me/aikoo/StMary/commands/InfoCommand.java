package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.core.system.*;
import me.aikoo.StMary.core.system.Object;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A command to obtain information about a game element.
 */
public class InfoCommand extends AbstractCommand {

    /**
     * Constructor of the Info command.
     * @param stMaryClient The StMaryClient instance.
     */
    public InfoCommand(StMaryClient stMaryClient) {
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

    /**
     * Provides information about different things in-game.
     * @param client The StMaryClient instance.
     * @param event  The interaction event.
     */
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

    /**
     * Provides information about a place.
     *
     * @param client The StMaryClient instance.
     * @param event  The interaction event.
     * @param name   The name of the place.
     */
    private void infoPlace(StMaryClient client, SlashCommandInteractionEvent event, String name) {
        Location location = client.getLocationManager().getLocation(name);

        if (location == null) {
            String error = client.getTextManager().generateError("Information à propos d'un lieu", "Le lieu demandé n'existe pas.");
            event.reply(error).queue();
            return;
        }

        String description = this.formatLocationDescription(location.getDescription());
        String content = String.format("**Nom :** %s `%s`\n\n**Type :** `%s`\n\n**Description du lieu :** %s", location.getIcon(), location.getName(), location.getType(), description);

        if (location instanceof Town town) {
            String townPlaces = this.formatTownPlaceDescription(town);
            content += String.format("\n\n**Lieux de la ville :** %s", townPlaces);
        } else if (location instanceof Region region) {
            String regionPlaces = this.formatRegionPlaceDescription(region);
            content += regionPlaces;
        } else if (location instanceof Place place) {
            String availableDestinations = this.formatAvailableDestination(place);
            content += String.format("\n\n**Destinations disponibles :** %s", availableDestinations);
        }

        String text = client.getTextManager().generateScene("Information à propos d'un lieu", content);
        event.reply(text).queue();
    }

    /**
     * Format the availables description from a place.
     * @param  place The place.
     * @return The formatted description of the place.
     */
    private String formatAvailableDestination(Place place) {
        ArrayList<Journey> availableDestinationsJourney = place.getAvailableMoves();
        StringBuilder availableDestinations = new StringBuilder();

        for (Journey journey : availableDestinationsJourney) {
            availableDestinations.append(String.format("%s `%s`", journey.getTo().getIcon(), journey.getTo().getName()));
            if (availableDestinationsJourney.indexOf(journey) != availableDestinationsJourney.size() - 1) {
                availableDestinations.append(", ");
            }
        }

        return availableDestinations.toString();
    }

    /**
     * Format the description of a location
     * @param description The description of the location.
     * @return The formatted description of the location.
     */
    private String formatLocationDescription(String description) {
        ArrayList<String> lines = new ArrayList<>(Arrays.asList(description.split("\n\n")));
        StringBuilder formattedDescription = new StringBuilder();

        for (String line : lines) {
            formattedDescription.append(String.format("`%s`", line));
            if (lines.indexOf(line) != lines.size() - 1) {
                formattedDescription.append("\n\n");
            }
        }

        return formattedDescription.toString();
    }

    /**
     * Format the description of a town place
     * @param  town The town place.
     * @return The formatted description of the town place.
     */
    private String formatTownPlaceDescription(Town town) {
        ArrayList<Place> places = town.getPlaces();
        StringBuilder formattedDescription = new StringBuilder();

        for (Place place : places) {
            formattedDescription.append(String.format("**%s** `%s`", place.getIcon(), place.getName()));
            if (places.indexOf(place) != places.size() - 1) {
                formattedDescription.append(", ");
            }
        }

        return formattedDescription.toString();
    }

    /**
     * Format places and towns description of a region
     * @param  region
     * @return The formatted description of the region.
     */
    private String formatRegionPlaceDescription(Region region) {
        ArrayList<Place> places = region.getPlaces();
        ArrayList<Town> towns = region.getTowns();
        StringBuilder formattedDescription = new StringBuilder();

        formattedDescription.append("\n\n**Villages :** ");
        for (Town town : towns) {
            formattedDescription.append(String.format("**%s** `%s`", town.getIcon(), town.getName()));
            if (towns.indexOf(town) != towns.size() - 1) {
                formattedDescription.append(", ");
            }
        }

        formattedDescription.append("\n\n**Lieux :** ");
        for (Place place : places) {
            formattedDescription.append(String.format("**%s** `%s`", place.getIcon(), place.getName()));
            if (places.indexOf(place) != places.size() - 1) {
                formattedDescription.append(", ");
            }
        }

        return formattedDescription.toString();
    }

    /**
     * Provides information about an object.
     *
     * @param client The StMaryClient instance.
     * @param event  The interaction event.
     * @param name   The name of the object.
     */
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

    /**
     * Provides information about a title or all titles.
     *
     * @param client The StMaryClient instance.
     * @param event  The interaction event.
     * @param name   The name of the title or null for all titles.
     */
    private void infoTitle(StMaryClient client, SlashCommandInteractionEvent event, String name) {
        String text = (name == null) ? infoAllTitles(client) : infoOneTitle(client, name);
        event.reply(text).queue();
    }

    /**
     * Provides information about a character.
     *
     * @param client The StMaryClient instance.
     * @param event  The interaction event.
     * @param name   The name of the character.
     */
    private void infoCharacter(StMaryClient client, SlashCommandInteractionEvent event, String name) {
        String text = client.getTextManager().generateScene("Information à propos d'un personnage", "Cette page n'est pas encore disponible !");
        event.reply(text).queue();
    }

    /**
     * Provides information about a monster.
     *
     * @param client The StMaryClient instance.
     * @param event  The interaction event.
     * @param name   The name of the monster.
     */
    private void infoMonster(StMaryClient client, SlashCommandInteractionEvent event, String name) {
        String text = client.getTextManager().generateScene("Information à propos d'un monstre", "Cette page n'est pas encore disponible !");
        event.reply(text).queue();
    }

    /**
     * Provides information about a quest.
     *
     * @param client The StMaryClient instance.
     * @param event  The interaction event.
     * @param name   The name of the quest.
     */
    private void infoQuest(StMaryClient client, SlashCommandInteractionEvent event, String name) {
        String text = client.getTextManager().generateScene("Information à propos d'une quête", "Cette page n'est pas encore disponible !");
        event.reply(text).queue();
    }

    /**
     * Generates information about all titles.
     *
     * @param client The StMaryClient instance.
     * @return A formatted string with information about all titles.
     */
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

    /**
     * Generates information about a specific title.
     *
     * @param client The StMaryClient instance.
     * @param name   The name of the title.
     * @return A formatted string with information about the title.
     */
    private String infoOneTitle(StMaryClient client, String name) {
        Title title = client.getTitleManager().getTitle(name);

        if (title == null) {
            return client.getTextManager().generateError("Titre", "Le titre demandé n'existe pas.");
        }

        return client.getTextManager().generateScene("Titre", String.format("%s | **%s** - `%s`", title.getIcon(), title.getName(), title.getDescription()));
    }

    @Override
    public void autoComplete(StMaryClient client, CommandAutoCompleteInteractionEvent event) {
        // Auto-complete logic can be added here if needed.
    }
}
