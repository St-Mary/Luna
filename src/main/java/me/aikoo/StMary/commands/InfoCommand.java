package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.abstracts.CommandAbstract;
import me.aikoo.StMary.core.abstracts.LocationAbstract;
import me.aikoo.StMary.core.bases.*;
import me.aikoo.StMary.core.bot.StMaryClient;
import me.aikoo.StMary.core.managers.LocationManager;
import me.aikoo.StMary.core.managers.ObjectManager;
import me.aikoo.StMary.core.managers.TextManager;
import me.aikoo.StMary.core.managers.TitleManager;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * A command to obtain information about a game element.
 */
public class InfoCommand extends CommandAbstract {

    /**
     * Constructor of the Info command.
     *
     * @param stMaryClient The StMaryClient instance.
     */
    public InfoCommand(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "info";
        this.description = "\uD83D\uDDDE️ Get information about an in-game element.";
        this.cooldown = 3000L;

        Command.Choice objectChoice = new Command.Choice(TextManager.getText("info_choice_object", "en"), "object")
                .setNameLocalization(DiscordLocale.FRENCH, TextManager.getText("info_choice_object", "fr"));
        Command.Choice placeChoice = new Command.Choice(TextManager.getText("info_choice_place", "en"), "place")
                .setNameLocalization(DiscordLocale.FRENCH, TextManager.getText("info_choice_place", "fr"));
        Command.Choice characterChoice = new Command.Choice(TextManager.getText("info_choice_character", "en"), "character")
                .setNameLocalization(DiscordLocale.FRENCH, TextManager.getText("info_choice_character", "fr"));
        Command.Choice monsterChoice = new Command.Choice(TextManager.getText("info_choice_monster", "en"), "monster")
                .setNameLocalization(DiscordLocale.FRENCH, TextManager.getText("info_choice_monster", "fr"));
        Command.Choice questChoice = new Command.Choice(TextManager.getText("info_choice_quest", "en"), "quest")
                .setNameLocalization(DiscordLocale.FRENCH, TextManager.getText("info_choice_quest", "fr"));
        Command.Choice titleChoice = new Command.Choice(TextManager.getText("info_choice_title", "en"), "title")
                .setNameLocalization(DiscordLocale.FRENCH, TextManager.getText("info_choice_title", "fr"));

        this.options.add(new OptionData(OptionType.STRING, "element", "The element to get informations about", true)
                .addChoices(objectChoice, placeChoice, characterChoice, monsterChoice, questChoice, titleChoice)
        );

        this.options.add(new OptionData(OptionType.STRING, "name", "The element name", false));
    }

    /**
     * Provides information about different things in-game.
     *
     * @param event The interaction event.
     */
    @Override
    public void execute(SlashCommandInteractionEvent event, String language) {
        String element = (event.getOption("element") != null) ? Objects.requireNonNull(event.getOption("element")).getAsString() : null;
        String name = (event.getOption("name") != null) ? Objects.requireNonNull(event.getOption("name")).getAsString() : null;

        switch (Objects.requireNonNull(element)) {
            case "object" -> infoObject(event, name, language);
            case "place" -> infoPlace(event, name, language);
            case "character" -> infoCharacter(event, name, language);
            case "monster" -> infoMonster(event, name, language);
            case "quest" -> infoQuest(event, name, language);
            case "title" -> infoTitle(event, name, language);
            default -> {
                String error = TextManager.createText("info_error_not_found", language).buildError();
                event.reply(error).setEphemeral(true).queue();
            }
        }
    }

    /**
     * Provides information about a place.
     *
     * @param event The interaction event.
     * @param name  The name of the place.
     */
    private void infoPlace(SlashCommandInteractionEvent event, String name, String language) {
        LocationAbstract location = LocationManager.getLocationByName(name, language);

        if (location == null) {
            String error = TextManager.createText("info_place_error_dont_exist", language).buildError();
            event.reply(error).setEphemeral(true).queue();
            return;
        }

        String description = this.formatLocationDescription(location.getDescription(language));
        String content = TextManager.getText("info_place_description", language).replace("{{icon}}", location.getIcon()).replace("{{name}}", location.getName(language)).replace("{{type}}", location.getType()).replace("{{description}}", description);

        if (location instanceof TownBase town) {
            String townPlaces = this.formatTownPlaceDescription(town, language);
            content += TextManager.getText("info_place_description_town", language).replace("{{town_places}}", townPlaces);
        } else if (location instanceof RegionBase region) {
            String regionPlaces = this.formatRegionPlaceDescription(region, language);
            content += regionPlaces;
        } else if (location instanceof PlaceBase place) {
            String availableDestinations = this.formatAvailableDestination(place, language);
            content += TextManager.getText("info_place_description_place", language).replace("{{available_destinations}}", availableDestinations);
        }

        String text = TextManager.createText("info_place_formatted", language).replace("place_description", content).build();
        event.reply(text).queue();
    }

    /**
     * Format the availables description from a place.
     *
     * @param place The place.
     * @return The formatted description of the place.
     */
    private String formatAvailableDestination(PlaceBase place, String language) {
        ArrayList<JourneyBase> availableDestinationsJourney = place.getAvailableMoves();
        StringBuilder availableDestinations = new StringBuilder();

        for (JourneyBase journey : availableDestinationsJourney) {
            availableDestinations.append(String.format("%s `%s`", journey.getTo().getIcon(), journey.getTo().getName(language)));
            if (availableDestinationsJourney.indexOf(journey) != availableDestinationsJourney.size() - 1) {
                availableDestinations.append(", ");
            }
        }

        return availableDestinations.toString();
    }

    /**
     * Format the description of a location
     *
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
     *
     * @param town The town place.
     * @return The formatted description of the town place.
     */
    private String formatTownPlaceDescription(TownBase town, String language) {
        ArrayList<PlaceBase> places = town.getPlaces();
        StringBuilder formattedDescription = new StringBuilder();

        for (PlaceBase place : places) {
            formattedDescription.append(String.format("**%s** `%s`", place.getIcon(), place.getName(language)));
            if (places.indexOf(place) != places.size() - 1) {
                formattedDescription.append(", ");
            }
        }

        return formattedDescription.toString();
    }

    /**
     * Format places and towns description of a region
     *
     * @param region The region.
     * @return The formatted description of the region.
     */
    private String formatRegionPlaceDescription(RegionBase region, String language) {
        ArrayList<PlaceBase> places = region.getPlaces();
        ArrayList<TownBase> towns = region.getTowns();
        StringBuilder formattedDescription = new StringBuilder();

        formattedDescription.append(TextManager.getText("info_place_region_towns", language));
        for (TownBase town : towns) {
            formattedDescription.append(String.format("**%s** `%s`", town.getIcon(), town.getName(language)));
            if (towns.indexOf(town) != towns.size() - 1) {
                formattedDescription.append(", ");
            }
        }

        formattedDescription.append(TextManager.getText("info_place_region_places", language));
        for (PlaceBase place : places) {
            formattedDescription.append(String.format("**%s** `%s`", place.getIcon(), place.getName(language)));
            if (places.indexOf(place) != places.size() - 1) {
                formattedDescription.append(", ");
            }
        }

        return formattedDescription.toString();
    }

    /**
     * Provides information about an object.
     *
     * @param event The interaction event.
     * @param name  The name of the object.
     */
    private void infoObject(SlashCommandInteractionEvent event, String name, String language) {
        if (name == null) {
            String text = TextManager.createText("info_object_error_no_object", language).buildError();
            event.reply(text).setEphemeral(true).queue();
            return;
        }

        ObjectBase object = ObjectManager.getObjectByName(name, language);

        if (object == null) {
            String error = TextManager.createText("info_object_error_not_found", language).buildError();
            event.reply(error).setEphemeral(true).queue();
            return;
        }

        String text = TextManager.createText("info_object_description", language)
                .replace("icon", object.getIcon())
                .replace("name", object.getName(language))
                .replace("description", object.getDescription(language))
                .replace("type", object.getType().getName(language)).build();

        event.reply(text).queue();
    }

    /**
     * Provides information about a title or all titles.
     *
     * @param event The interaction event.
     * @param name  The name of the title or null for all titles.
     */
    private void infoTitle(SlashCommandInteractionEvent event, String name, String language) {
        String text = (name == null) ? infoAllTitles(language) : infoOneTitle(name, language);
        event.reply(text).queue();
    }

    /**
     * Provides information about a character.
     *
     * @param event The interaction event.
     * @param name  The name of the character.
     */
    private void infoCharacter(SlashCommandInteractionEvent event, String name, String language) {
        String text = TextManager.createText("info_character_not_available", language).buildError();
        event.reply(text).queue();
    }

    /**
     * Provides information about a monster.
     *
     * @param event The interaction event.
     * @param name  The name of the monster.
     */
    private void infoMonster(SlashCommandInteractionEvent event, String name, String language) {
        String text = TextManager.createText("info_monster_not_available", language).buildError();
        event.reply(text).queue();
    }

    /**
     * Provides information about a quest.
     *
     * @param event The interaction event.
     * @param name  The name of the quest.
     */
    private void infoQuest(SlashCommandInteractionEvent event, String name, String language) {
        String text = TextManager.createText("info_quest_not_available", language).buildError();
        event.reply(text).queue();
    }

    /**
     * Generates information about all titles.
     *
     * @return A formatted string with information about all titles.
     */
    private String infoAllTitles(String language) {
        ArrayList<TitleBase> titles = new ArrayList<>(TitleManager.getTitles().values());
        StringBuilder stringBuilder = new StringBuilder();

        for (TitleBase title : titles) {
            stringBuilder.append(String.format("%s | **%s** - `%s`", title.getIcon(), title.getName(language), title.getDescription(language)));

            if (titles.indexOf(title) != titles.size() - 1) {
                stringBuilder.append("\n\n");
            }
        }

        return TextManager.createText("info_title_titles", language).replace("titles", stringBuilder.toString()).build();
    }

    /**
     * Generates information about a specific title.
     *
     * @param id The id of the title.
     * @return A formatted string with information about the title.
     */
    private String infoOneTitle(String id, String language) {
        TitleBase title = TitleManager.getTitleByName(id, language);

        if (title == null) {
            return TextManager.createText("info_title_error_not_exist", language).buildError();
        }

        return TextManager.createText("info_title_title", language).replace("icon", title.getIcon()).replace("name", title.getName(language)).replace("description", title.getDescription(language)).build();
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event, String language) {
        // Auto-complete logic can be added here if needed.
    }
}
