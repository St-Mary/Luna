package me.aikoo.StMary.commands;

import lombok.Getter;
import lombok.Setter;
import me.aikoo.StMary.BotConfig;
import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.core.system.Button;
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

public abstract class AbstractCommand {
    protected final StMaryClient stMaryClient;
    protected final List<OptionData> options = new ArrayList<>();
    @Getter
    protected final HashMap<String, Button> buttons = new HashMap<>();

    @Getter
    @Setter
    protected boolean mustBeRegistered = true;

    @Getter
    @Setter
    protected boolean isAdminCommand = false;

    private final Logger LOGGER = LoggerFactory.getLogger(AbstractCommand.class);
    protected String name;
    protected String description;
    protected Long cooldown = 5000L;

    /**
     * Constructor for the AbstractCommand class
     * @param stMaryClient The client instance
     */
    public AbstractCommand(StMaryClient stMaryClient) {
        this.stMaryClient = stMaryClient;
        this.name = this.getClass().getSimpleName().toLowerCase();
        this.description = "No description provided.";
    }

    public abstract void execute(SlashCommandInteractionEvent event);

    public abstract void autoComplete(CommandAutoCompleteInteractionEvent event);

    public void run(SlashCommandInteractionEvent event) {

        if (this.isAdminCommand) {
            if (!this.stMaryClient.getDatabaseManager().isAdministrator(event.getUser().getIdLong()) && !event.getUser().getId().equals(BotConfig.getOwnerId())) {
                event.reply(stMaryClient.getTextManager().generateError("Exécution de la commande", "Vous n'avez pas la permission d'exécuter cette commande!")).setEphemeral(true).queue();
                return;
            }
        }

        if (this.cooldown > 0) {

            // If the user has a cooldown, we check if it's over. If not, we send an error message, otherwise we add a new cooldown.
            if (this.stMaryClient.getCooldownManager().hasCooldown(event.getUser().getId(), this.name) && this.stMaryClient.getCooldownManager().getRemainingCooldown(event.getUser().getId(), this.name) > 0) {
                long timeRemaining = this.stMaryClient.getCooldownManager().getRemainingCooldown(event.getUser().getId(), this.name);
                long timestampRemaining = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() + timeRemaining);

                String text = stMaryClient.getTextManager().generateError("Doucement, jeune aventurier!", String.format("Patiente <t:%s:R> avant de pouvoir repartir au galop.", timestampRemaining));
                event.reply(text).setEphemeral(true).queue();
                return;
            }

            // Add a new cooldown
            this.stMaryClient.getCooldownManager().addCooldown(event.getUser().getId(), this.name, this.cooldown);
        }

        // If the command requires the user to be registered in-game, we check if he is. If not, we send an error message.
        if (this.mustBeRegistered && this.stMaryClient.getDatabaseManager().getPlayer(event.getUser().getIdLong()) == null) {
            event.reply(this.stMaryClient.getTextManager().generateError("Exécution de la commande", "Vous devez posséder un compte aventure pour exécuter cette commande!")).setEphemeral(true).queue();
            return;
        }

        // Execute the command
        this.execute(event);
    }

    protected ArrayList<Button> getArrayListButtons() {
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
