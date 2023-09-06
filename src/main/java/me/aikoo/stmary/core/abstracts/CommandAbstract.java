package me.aikoo.stmary.core.abstracts;

import lombok.Getter;
import lombok.Setter;
import me.aikoo.stmary.core.bot.StMaryClient;
import me.aikoo.stmary.core.constants.BotConfigConstant;
import me.aikoo.stmary.core.database.PlayerEntity;
import me.aikoo.stmary.core.managers.ButtonManager;
import me.aikoo.stmary.core.managers.CooldownManager;
import me.aikoo.stmary.core.managers.DatabaseManager;
import me.aikoo.stmary.core.managers.TextManager;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import net.dv8tion.jda.api.interactions.commands.localization.ResourceBundleLocalizationFunction;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Represents an abstract command that can be executed in Discord.
 */
public abstract class CommandAbstract {

    protected final StMaryClient stMaryClient;
    protected final List<OptionData> options = new ArrayList<>();
    private final Logger LOGGER = LoggerFactory.getLogger(CommandAbstract.class);

    /**
     * Get whether the command requires the user to be registered in-game.
     * Set the command to require the user to be registered in-game.
     */
    @Getter
    @Setter
    protected boolean mustBeRegistered = true;

    /**
     * Get whether the command is an administrator command.
     * Set the command to be an administrator command.
     */
    @Getter
    @Setter
    protected boolean isAdminCommand = false;

    /**
     * Get the name of the command.
     */
    @Getter
    protected String name;

    /**
     * Get the description of the command.
     */
    @Getter
    protected String description;

    /**
     * Get the cooldown of the command.
     */
    @Getter
    protected Long cooldown = 5000L;

    /**
     * Constructor for the AbstractCommand class
     *
     * @param stMaryClient The client instance
     */
    protected CommandAbstract(StMaryClient stMaryClient) {
        this.stMaryClient = stMaryClient;
        this.name = this.getClass().getSimpleName().toLowerCase();
        this.description = "No description provided.";
    }

    /**
     * Execute the command
     *
     * @param event    The SlashCommandInteractionEvent triggered when the button is clicked.
     * @param language The language of the player
     */
    public abstract void execute(SlashCommandInteractionEvent event, String language);

    /**
     * Auto complete the command
     *
     * @param event    The CommandAutoCompleteInteractionEvent triggered when the button is clicked.
     * @param language The language of the player
     */
    public abstract void autoComplete(CommandAutoCompleteInteractionEvent event, String language);

    /**
     * Run the command
     *
     * @param event The SlashCommandInteractionEvent triggered when the button is clicked.
     */
    public void run(SlashCommandInteractionEvent event) {
        PlayerEntity player = DatabaseManager.getPlayer(event.getUser().getIdLong());
        String language = (event.getGuild().getLocale() == DiscordLocale.FRENCH) ? "fr" : "en";
        language = (player != null) ? player.getLanguage() : language;

        if (this.isAdminCommand) {
            if (!DatabaseManager.isAdministrator(event.getUser().getIdLong()) && !event.getUser().getId().equals(BotConfigConstant.getOwnerId())) {
                event.reply(TextManager.createText("command_error_not_allowed", language).buildError()).setEphemeral(true).queue();
                return;
            }
        }

        if (this.cooldown > 0) {

            // If the user has a cooldown, we check if it's over. If not, we send an error message, otherwise we add a new cooldown.
            if (CooldownManager.hasCooldown(event.getUser().getId(), this.name) && CooldownManager.getRemainingCooldown(event.getUser().getId(), this.name) > 0) {
                long timeRemaining = CooldownManager.getRemainingCooldown(event.getUser().getId(), this.name);
                long timestampRemaining = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() + timeRemaining);

                String timestamp = "<t:" + timestampRemaining + ":R>";
                String text = TextManager.createText("command_error_cooldown", language).replace("cooldown", timestamp).buildError();
                event.reply(text).setEphemeral(true).queue();
                return;
            }

            // Add a new cooldown
            CooldownManager.addCooldown(event.getUser().getId(), this.name, this.cooldown);
        }

        // Check if user have an action waiter
        if (this.stMaryClient.getCache().get("actionWaiter_" + event.getUser().getId()).isPresent()) {
            event.reply(TextManager.createText("command_error_action_waiter", language).buildError()).setEphemeral(true).queue();
            return;
        }

        // If the command requires the user to be registered in-game, we check if he is. If not, we send an error message.
        if (this.mustBeRegistered && DatabaseManager.getPlayer(event.getUser().getIdLong()) == null) {
            event.reply(TextManager.createText("command_error_must_be_player", language).buildError()).setEphemeral(true).queue();
            return;
        }

        try {
            this.execute(event, language);
        } catch (Exception e) {
            LOGGER.error("Error while executing command: " + e);

            String errorText = TextManager.createText("command_error", language).buildError();
            event.reply(errorText).setEphemeral(true).queue();
        }
    }

    /**
     * Run the autocomplete of the command
     *
     * @param event The CommandAutoCompleteInteractionEvent triggered when the button is clicked.
     */
    public void runAutoComplete(CommandAutoCompleteInteractionEvent event) {
        String language = (event.getGuild().getLocale() == DiscordLocale.FRENCH) ? "fr" : "en";
        PlayerEntity player = DatabaseManager.getPlayer(event.getUser().getIdLong());
        language = (player != null) ? player.getLanguage() : language;
        this.autoComplete(event, language);
    }

    /**
     * Send a message with buttons
     *
     * @param event       The SlashCommandInteractionEvent triggered when the button is clicked.
     * @param text        The text to send
     * @param language    The language of the player
     * @param buttons     The list of Button objects to add.
     * @param time        The time before the message is deleted
     * @param closeMethod The method to execute when the message is deleted
     * @param methodClass The class of the method to execute
     * @param parameters  The parameters of the method to execute
     */
    public void sendMsgWithButtons(SlashCommandInteractionEvent event, String text, String language, List<ButtonAbstract> buttons, int time, Method closeMethod, Object methodClass, Object... parameters) {
        ArrayList<Button> buttonList = new ArrayList<>();
        buttons.forEach(button -> buttonList.add(button.getButton()));
        event.reply(text).addActionRow(buttonList).queue(msg -> msg.retrieveOriginal().queue(res -> {
            ButtonManager.addButtons(res.getId(), buttons);

            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            if (event.isAcknowledged()) {
                                return;
                            }

                            if (ButtonManager.isButtons(res.getId())) {
                                ButtonManager.removeButtons(res.getId());

                                if (closeMethod != null) {
                                    try {
                                        Object[] params = new Object[parameters.length + 2];
                                        params[0] = res;
                                        params[1] = language;
                                        System.arraycopy(parameters, 0, params, 2, parameters.length);

                                        closeMethod.invoke(methodClass, params);

                                        if (stMaryClient.getCache().get("actionWaiter_" + event.getUser().getId()).isPresent()) {
                                            stMaryClient.getCache().delete("actionWaiter_" + event.getUser().getId());
                                        }
                                    } catch (Exception e) {
                                        LOGGER.error("Error while executing closeMethod: " + e);
                                    }
                                } else {
                                    res.editMessage(text).setComponents().queue();

                                    if (stMaryClient.getCache().get("actionWaiter_" + event.getUser().getId()).isPresent()) {
                                        stMaryClient.getCache().delete("actionWaiter_" + event.getUser().getId());
                                    }
                                }
                                if (stMaryClient.getCache().get("actionWaiter_" + event.getUser().getId()).isPresent()) {
                                    stMaryClient.getCache().delete("actionWaiter_" + event.getUser().getId());
                                }
                            }
                            if (stMaryClient.getCache().get("actionWaiter_" + event.getUser().getId()).isPresent()) {
                                stMaryClient.getCache().delete("actionWaiter_" + event.getUser().getId());
                            }
                        }
                    },
                    time
            );
        }));
    }

    /**
     * Send a message with buttons
     *
     * @return The SlashCommandData of the command
     */
    public SlashCommandData buildCommandData() {
        LocalizationFunction localizationFunction = ResourceBundleLocalizationFunction.fromBundles("bundles/Commands", DiscordLocale.FRENCH).build();
        SlashCommandData data = Commands.slash(this.name, this.description).setLocalizationFunction(localizationFunction);

        if (!this.options.isEmpty()) {
            data.addOptions(this.options);
        }
        return data;
    }
}
