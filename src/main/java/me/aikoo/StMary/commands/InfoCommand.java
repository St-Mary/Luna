package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.core.abstracts.AbstractCommand;
import me.aikoo.StMary.core.abstracts.Location;
import me.aikoo.StMary.core.classes.*;
import me.aikoo.StMary.core.classes.Object;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

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
        this.description = "\uD83D\uDDDE️ Obtenir des informations sur un élément du jeu.";
        this.cooldown = 3000L;

        this.options.add(new OptionData(OptionType.STRING, "element", "L'élément sur lequel vous souhaitez obtenir des informations.", true)
                .addChoice(stMaryClient.getTextManager().getText("info_choice_object"), "object")
                .addChoice(stMaryClient.getTextManager().getText("info_choice_place"), "place")
                .addChoice(stMaryClient.getTextManager().getText("info_choice_character"), "character")
                .addChoice(stMaryClient.getTextManager().getText("info_choice_monster"), "monster")
                .addChoice(stMaryClient.getTextManager().getText("info_choice_quest"), "quest")
                .addChoice(stMaryClient.getTextManager().getText("info_choice_title"), "title")
        );
        this.options.add(new OptionData(OptionType.STRING, "name", "Le nom de l'élément sur lequel vous souhaitez obtenir des informations.", false));
    }

    /**
     * Provides information about different things in-game.
     * @param event  The interaction event.
     */
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String element = (event.getOption("element") != null) ? Objects.requireNonNull(event.getOption("element")).getAsString() : null;
        String name = (event.getOption("name") != null) ? Objects.requireNonNull(event.getOption("name")).getAsString() : null;

        switch (Objects.requireNonNull(element)) {
            case "object" -> infoObject(event, name);
            case "place" -> infoPlace(event, name);
            case "character" -> infoCharacter(event, name);
            case "monster" -> infoMonster(event, name);
            case "quest" -> infoQuest(event, name);
            case "title" -> infoTitle(event, name);
            default -> {
                String error = stMaryClient.getTextManager().createText("info_error_not_found").buildError();
                event.reply(error).setEphemeral(true).queue();
            }
        }
    }

    /**
     * Provides information about a place.
     *
     * @param event  The interaction event.
     * @param name   The name of the place.
     */
    private void infoPlace(SlashCommandInteractionEvent event, String name) {
        Location location = stMaryClient.getLocationManager().getLocation(name);

        if (location == null) {
            String error = stMaryClient.getTextManager().createText("info_place_error_dont_exist").buildError();
            event.reply(error).setEphemeral(true).queue();
            return;
        }

        String description = this.formatLocationDescription(location.getDescription());
        String content = stMaryClient.getTextManager().getText("info_place_description").replace("{{icon}}", location.getIcon()).replace("{{name}}", location.getName()).replace("{{type}}", location.getType()).replace("{{description}}", description);

        if (location instanceof Town town) {
            String townPlaces = this.formatTownPlaceDescription(town);
            content += stMaryClient.getTextManager().getText("info_place_description_town").replace("{{town_places}}", townPlaces);
        } else if (location instanceof Region region) {
            String regionPlaces = this.formatRegionPlaceDescription(region);
            content += regionPlaces;
        } else if (location instanceof Place place) {
            String availableDestinations = this.formatAvailableDestination(place);
            content += stMaryClient.getTextManager().getText("info_place_description_place").replace("{{available_destinations}}", availableDestinations);
        }

        String text = stMaryClient.getTextManager().createText("info_place_formatted").replace("place_description", content).build();
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
     * @param  region The region.
     * @return        The formatted description of the region.
     */
    private String formatRegionPlaceDescription(Region region) {
        ArrayList<Place> places = region.getPlaces();
        ArrayList<Town> towns = region.getTowns();
        StringBuilder formattedDescription = new StringBuilder();

        formattedDescription.append(stMaryClient.getTextManager().getText("info_place_region_towns"));
        for (Town town : towns) {
            formattedDescription.append(String.format("**%s** `%s`", town.getIcon(), town.getName()));
            if (towns.indexOf(town) != towns.size() - 1) {
                formattedDescription.append(", ");
            }
        }

        formattedDescription.append(stMaryClient.getTextManager().getText("info_place_region_places"));
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
     * @param event  The interaction event.
     * @param name   The name of the object.
     */
    private void infoObject(SlashCommandInteractionEvent event, String name) {
        if (name == null) {
            String text = stMaryClient.getTextManager().generateError("Information à propos d'un objet", "Veuillez entrer le nom de l'objet que vous souhaitez consulter.");
            event.reply(text).queue();
            return;
        }

        Object object = stMaryClient.getObjectManager().getObjectByName(name);

        if (object == null) {
            String error = stMaryClient.getTextManager().generateError("Information à propos d'un objet", "L'objet demandé n'existe pas.");
            event.reply(error).queue();
            return;
        }

        String description = String.format("**Nom :** %s `%s`\n\n**Type :** `%s`\n\n**Description de l'objet:** `%s`", object.getIcon(), object.getName(), object.getType().getId(), object.getDescription());
        String text = stMaryClient.getTextManager().generateScene("Information à propos d'un objet", description);

        event.reply(text).queue();
    }

    /**
     * Provides information about a title or all titles.
     *
     * @param event  The interaction event.
     * @param name   The name of the title or null for all titles.
     */
    private void infoTitle(SlashCommandInteractionEvent event, String name) {
        String text = (name == null) ? infoAllTitles() : infoOneTitle(name);
        event.reply(text).queue();
    }

    /**
     * Provides information about a character.
     *
     * @param event  The interaction event.
     * @param name   The name of the character.
     */
    private void infoCharacter(SlashCommandInteractionEvent event, String name) {
        String text = stMaryClient.getTextManager().generateScene("Information à propos d'un personnage", "Cette page n'est pas encore disponible !");
        event.reply(text).queue();
    }

    /**
     * Provides information about a monster.
     *
     * @param event  The interaction event.
     * @param name   The name of the monster.
     */
    private void infoMonster(SlashCommandInteractionEvent event, String name) {
        String text = stMaryClient.getTextManager().generateScene("Information à propos d'un monstre", "Cette page n'est pas encore disponible !");
        event.reply(text).queue();
    }

    /**
     * Provides information about a quest.
     *
     * @param event  The interaction event.
     * @param name   The name of the quest.
     */
    private void infoQuest(SlashCommandInteractionEvent event, String name) {
        String text = stMaryClient.getTextManager().generateScene("Information à propos d'une quête", "Cette page n'est pas encore disponible !");
        event.reply(text).queue();
    }

    /**
     * Generates information about all titles.
     *
     * @return A formatted string with information about all titles.
     */
    private String infoAllTitles() {
        ArrayList<Title> titles = new ArrayList<>(stMaryClient.getTitleManager().getTitles().values());
        StringBuilder stringBuilder = new StringBuilder();

        for (Title title : titles) {
            stringBuilder.append(String.format("%s | **%s** - `%s`", title.getIcon(), title.getName(), title.getDescription()));

            if (titles.indexOf(title) != titles.size() - 1) {
                stringBuilder.append("\n\n");
            }
        }

        return stMaryClient.getTextManager().generateScene("Titres", stringBuilder.toString());
    }

    /**
     * Generates information about a specific title.
     *
     * @param name   The name of the title.
     * @return A formatted string with information about the title.
     */
    private String infoOneTitle(String name) {
        Title title = stMaryClient.getTitleManager().getTitle(name);

        if (title == null) {
            return stMaryClient.getTextManager().generateError("Titre", "Le titre demandé n'existe pas.");
        }

        return stMaryClient.getTextManager().generateScene("Titre", String.format("%s | **%s** - `%s`", title.getIcon(), title.getName(), title.getDescription()));
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event) {
        // Auto-complete logic can be added here if needed.
    }
}
