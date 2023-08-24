package me.aikoo.StMary.core.abstracts;

import lombok.Getter;
import lombok.Setter;
import me.aikoo.StMary.core.bot.StMaryClient;
import me.aikoo.StMary.core.constants.BotConfigConstant;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class CommandAbstract {
    protected final StMaryClient stMaryClient;
    protected final List<OptionData> options = new ArrayList<>();
    @Getter
    protected final HashMap<String, ButtonAbstract> buttons = new HashMap<>();
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

    public abstract void execute(SlashCommandInteractionEvent event);

    public abstract void autoComplete(CommandAutoCompleteInteractionEvent event);

    public void run(SlashCommandInteractionEvent event) {

        if (this.isAdminCommand) {
            if (!this.stMaryClient.getDatabaseManager().isAdministrator(event.getUser().getIdLong()) && !event.getUser().getId().equals(BotConfigConstant.getOwnerId())) {
                event.reply(stMaryClient.getTextManager().createText("command_error_not_allowed").buildError()).setEphemeral(true).queue();
                return;
            }
        }

        if (this.cooldown > 0) {

            // If the user has a cooldown, we check if it's over. If not, we send an error message, otherwise we add a new cooldown.
            if (this.stMaryClient.getCooldownManager().hasCooldown(event.getUser().getId(), this.name) && this.stMaryClient.getCooldownManager().getRemainingCooldown(event.getUser().getId(), this.name) > 0) {
                long timeRemaining = this.stMaryClient.getCooldownManager().getRemainingCooldown(event.getUser().getId(), this.name);
                long timestampRemaining = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() + timeRemaining);

                String timestamp = "<t:" + timestampRemaining + ":R>";
                String text = stMaryClient.getTextManager().createText("command_error_cooldown").replace("cooldown", timestamp).buildError();
                event.reply(text).setEphemeral(true).queue();
                return;
            }

            // Add a new cooldown
            this.stMaryClient.getCooldownManager().addCooldown(event.getUser().getId(), this.name, this.cooldown);
        }

        // If the command requires the user to be registered in-game, we check if he is. If not, we send an error message.
        if (this.mustBeRegistered && this.stMaryClient.getDatabaseManager().getPlayer(event.getUser().getIdLong()) == null) {
            event.reply(this.stMaryClient.getTextManager().createText("command_error_must_be_player").buildError()).setEphemeral(true).queue();
            return;
        }

        this.execute(event);
    }

    protected ArrayList<ButtonAbstract> getArrayListButtons() {
        return new ArrayList<>(this.buttons.values());
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

    public CommandData buildCommandData() {
        SlashCommandData data = Commands.slash(this.name, this.description);
        if (!this.options.isEmpty()) data.addOptions(this.options);
        return data;
    }
}
