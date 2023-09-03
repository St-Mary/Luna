package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.abstracts.ButtonAbstract;
import me.aikoo.StMary.core.abstracts.CommandAbstract;
import me.aikoo.StMary.core.bases.CharacterBase;
import me.aikoo.StMary.core.bot.StMaryClient;
import me.aikoo.StMary.core.constants.PlayerConstant;
import me.aikoo.StMary.core.database.PlayerEntity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.*;

/**
 * The Start command allows users to begin their adventure.
 */
public class StartCommand extends CommandAbstract {

    /**
     * Creates a new Start command.
     *
     * @param stMaryClient The StMaryClient instance.
     */
    public StartCommand(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "start";
        this.description = "\uD83D\uDE80 Démarrer l'aventure !";
        this.cooldown = 10000L;
        this.setMustBeRegistered(false);

        this.options.add(new OptionData(OptionType.STRING, "language", "The language to use")
                .addChoice("\uD83C\uDDEB\uD83C\uDDF7 Français", "fr")
                .addChoice("\uD83C\uDDEC\uD83C\uDDE7 English", "en")
                .setRequired(true)
        );
    }

    /**
     * Executes the Start command.
     *
     * @param event The SlashCommandInteractionEvent.
     */
    @Override
    public void execute(SlashCommandInteractionEvent event, String language) {
        PlayerEntity player = stMaryClient.getDatabaseManager().getPlayer(event.getUser().getIdLong());
        LocalDate creationDate = event.getUser().getTimeCreated().toLocalDateTime().toLocalDate();
        language = event.getOption("language").getAsString();

        // Check if the Discord account was created more than a week ago
        if (creationDate.isAfter(LocalDate.now().minusWeeks(PlayerConstant.CREATION_TIME_WEEK_LIMIT))) {
            event.reply(stMaryClient.getTextManager().createText("start_adventure_error_creation_date", language).buildError()).queue();
            return;
        }

        if (player != null) {
            // Player already exists, send an error message
            String error = stMaryClient.getTextManager().createText("start_adventure_error_already_started", language).buildError();
            event.reply(error).setEphemeral(true).queue();
            return;
        }

        try {
            CharacterBase.Information character = this.stMaryClient.getCharacterManager().getCharacter("1").getCharacterInformation(language);
            CharacterBase.Dialog dialog = character.getDialog("1.1");
            CharacterBase.Option optionOne = dialog.getOptions().get("1.1.1");
            CharacterBase.Option optionTwo = dialog.getOptions().get("1.1.2");
            Method yesMethod = StartCommand.class.getMethod("onClickYesBtn", ButtonInteractionEvent.class, String.class);
            Method noMethod = StartCommand.class.getMethod("onClickNoBtn", ButtonInteractionEvent.class, String.class);

            CharacterBase.OptionBtn optionBtn1 = new CharacterBase.OptionBtn(optionOne.getId(), optionOne.getName(), optionOne.getIcon(), optionOne.getStyle(), stMaryClient, this, yesMethod);
            CharacterBase.OptionBtn optionBtn2 = new CharacterBase.OptionBtn(optionTwo.getId(), optionTwo.getName(), optionTwo.getIcon(), optionTwo.getStyle(), stMaryClient, this, noMethod);

            stMaryClient.getCache().put("startCmdLanguage_" + event.getUser().getId(), language);

            String introduction = stMaryClient.getTextManager().createText("start_adventure_introduction", language).build();
            String text = introduction + "\n\n" + stMaryClient.getCharacterManager().formatCharacterDialog(character, dialog);

            Method closeMethod = StartCommand.class.getMethod("closeBtnMenuEvent", Message.class, String.class, SlashCommandInteractionEvent.class);
            ArrayList<ButtonAbstract> buttons = new ArrayList<>(List.of(optionBtn1, optionBtn2));

            this.sendMsgWithButtons(event, text, language, buttons, 5000, closeMethod, this);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeBtnMenuEvent(Message res, String language, SlashCommandInteractionEvent event) {
        closeBtn(res, language, event.getUser());
    }

    public void closeBtnMenuEvent(Message res, String language, ButtonInteractionEvent event) {
        closeBtn(res, language, event.getUser());
    }

    private void closeBtn(Message res, String language, User user) {
        CharacterBase.Information character = this.stMaryClient.getCharacterManager().getCharacter("1").getCharacterInformation(language);
        CharacterBase.Dialog noDialog = character.getDialog("1.1.2");
        String text = stMaryClient.getCharacterManager().formatCharacterDialog(character, noDialog);

        // Remove the cache and buttons
        stMaryClient.getCache().delete("startCmdLanguage_" + user.getId());
        stMaryClient.getButtonManager().removeButtons(res.getId());
        res.editMessage(text).setComponents().queue();
    }

    /**
     * Auto-complete method for the Start command.
     *
     * @param event The CommandAutoCompleteInteractionEvent.
     */
    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event, String language) {
    }

    public void onClickYesBtn(ButtonInteractionEvent event, String language) {
        Optional<String> cacheLanguage = stMaryClient.getCache().get("startCmdLanguage_" + event.getUser().getId());
        language = cacheLanguage.orElse(language);

        CharacterBase.Information character = this.stMaryClient.getCharacterManager().getCharacter("1").getCharacterInformation(language);
        String dialog = stMaryClient.getCharacterManager().formatCharacterDialog(character, character.getDialog("1.1.1"));

        // Create a new player entity
        PlayerEntity player = new PlayerEntity();
        player.setDiscordId(event.getUser().getIdLong());
        player.setLevel(PlayerConstant.LEVEL);
        player.setExperience(PlayerConstant.EXPERIENCE);
        player.setMoney(PlayerConstant.MONEY);
        player.setTitles(PlayerConstant.TITLES, stMaryClient);
        player.setCurrentLocationRegion(PlayerConstant.CURRENT_LOCATION_REGION);
        player.setCurrentLocationTown(PlayerConstant.CURRENT_LOCATION_TOWN);
        player.setCurrentLocationPlace(PlayerConstant.CURRENT_LOCATION_PLACE);
        player.setCurrentTitle(PlayerConstant.CURRENT_TITLE);
        player.setMagicalBook(PlayerConstant.MAGICAL_BOOK);
        player.setLanguage(language);
        player.setCreationTimestamp(new Date());

        // Save the new player entity to the database
        stMaryClient.getDatabaseManager().save(player);

        event.editMessage(dialog).setComponents().queue();
        stMaryClient.getButtonManager().removeButtons(event.getMessageId());
        stMaryClient.getCache().delete("startCmdLanguage_" + event.getUser().getId());
    }

    public void onClickNoBtn(ButtonInteractionEvent event, String language) {
        language = stMaryClient.getCache().get("startCmdLanguage_" + event.getUser().getId()).orElse(language);
        closeBtnMenuEvent(event.getMessage(), language, event);
    }
}
