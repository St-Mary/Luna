package me.aikoo.StMary.core.abstracts;

import lombok.Getter;
import lombok.Setter;
import me.aikoo.StMary.core.bases.CharacterBase;
import me.aikoo.StMary.core.bot.StMaryClient;
import me.aikoo.StMary.core.constants.BotConfigConstant;
import me.aikoo.StMary.core.database.PlayerEntity;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class CommandAbstract {
    protected final StMaryClient stMaryClient;
    protected final List<OptionData> options = new ArrayList<>();
    private final Logger LOGGER = LoggerFactory.getLogger(CommandAbstract.class);
    @Getter
    @Setter
    protected boolean mustBeRegistered = true;
    @Getter
    @Setter
    protected boolean isAdminCommand = false;
    protected String name;
    protected String description;
    protected Long cooldown = 5000L;

    /**
     * Constructor for the AbstractCommand class
     *
     * @param stMaryClient The client instance
     */
    public CommandAbstract(StMaryClient stMaryClient) {
        this.stMaryClient = stMaryClient;
        this.name = this.getClass().getSimpleName().toLowerCase();
        this.description = "No description provided.";
    }

    public abstract void execute(SlashCommandInteractionEvent event, String language);

    public abstract void autoComplete(CommandAutoCompleteInteractionEvent event, String language);

    public void run(SlashCommandInteractionEvent event) {
        PlayerEntity player = this.stMaryClient.getDatabaseManager().getPlayer(event.getUser().getIdLong());
        String language = (event.getGuild().getLocale() == DiscordLocale.FRENCH) ? "fr" : "en";
        language = (player != null) ? player.getLanguage() : language;

        if (this.isAdminCommand) {
            if (!this.stMaryClient.getDatabaseManager().isAdministrator(event.getUser().getIdLong()) && !event.getUser().getId().equals(BotConfigConstant.getOwnerId())) {
                event.reply(stMaryClient.getTextManager().createText("command_error_not_allowed", language).buildError()).setEphemeral(true).queue();
                return;
            }
        }

        if (this.cooldown > 0) {

            // If the user has a cooldown, we check if it's over. If not, we send an error message, otherwise we add a new cooldown.
            if (this.stMaryClient.getCooldownManager().hasCooldown(event.getUser().getId(), this.name) && this.stMaryClient.getCooldownManager().getRemainingCooldown(event.getUser().getId(), this.name) > 0) {
                long timeRemaining = this.stMaryClient.getCooldownManager().getRemainingCooldown(event.getUser().getId(), this.name);
                long timestampRemaining = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() + timeRemaining);

                String timestamp = "<t:" + timestampRemaining + ":R>";
                String text = stMaryClient.getTextManager().createText("command_error_cooldown", language).replace("cooldown", timestamp).buildError();
                event.reply(text).setEphemeral(true).queue();
                return;
            }

            // Add a new cooldown
            this.stMaryClient.getCooldownManager().addCooldown(event.getUser().getId(), this.name, this.cooldown);
        }

        // Check if user have an action waiter
        if (this.stMaryClient.getCache().get("actionWaiter_" + event.getUser().getId()).isPresent()) {
            event.reply(this.stMaryClient.getTextManager().createText("command_error_action_waiter", language).buildError()).setEphemeral(true).queue();
            return;
        }

        // If the command requires the user to be registered in-game, we check if he is. If not, we send an error message.
        if (this.mustBeRegistered && this.stMaryClient.getDatabaseManager().getPlayer(event.getUser().getIdLong()) == null) {
            event.reply(this.stMaryClient.getTextManager().createText("command_error_must_be_player", language).buildError()).setEphemeral(true).queue();
            return;
        }

        this.execute(event, language);
    }

    public void runAutoComplete(CommandAutoCompleteInteractionEvent event) {
        String language = (event.getGuild().getLocale() == DiscordLocale.FRENCH) ? "fr" : "en";
        PlayerEntity player = this.stMaryClient.getDatabaseManager().getPlayer(event.getUser().getIdLong());
        language = (player != null) ? player.getLanguage() : language;
        this.autoComplete(event, language);
    }

    public void sendMsgWithButtons(SlashCommandInteractionEvent event, String text, String language, ArrayList<ButtonAbstract> buttons, int time, Method closeMethod, Object methodClass, Object ...parameters) {
        ArrayList<Button> buttonList = new ArrayList<>();
        buttons.forEach(button -> buttonList.add(button.getButton()));
        event.reply(text).addActionRow(buttonList).queue(msg -> msg.retrieveOriginal().queue(res -> {
            stMaryClient.getButtonManager().addButtons(res.getId(), buttons);

            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            if (stMaryClient.getButtonManager().isButtons(res.getId())) {
                                stMaryClient.getButtonManager().removeButtons(res.getId());

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

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Long getCooldown() {
        return cooldown;
    }

    public SlashCommandData buildCommandData() {
        LocalizationFunction localizationFunction = ResourceBundleLocalizationFunction.fromBundles("bundles/Commands", DiscordLocale.FRENCH).build();
        SlashCommandData data = Commands.slash(this.name, this.description).setLocalizationFunction(localizationFunction);

        if (!this.options.isEmpty()) {
            data.addOptions(this.options);
        }
        return data;
    }
}
