package me.aikoo.StMary.command;

import me.aikoo.StMary.core.StMaryClient;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class AbstractCommand {
    private final Logger LOGGER = LoggerFactory.getLogger(AbstractCommand.class);
    protected String name;
    protected String description;
    protected Long cooldown = 0L;
    protected final StMaryClient stMaryClient;
    protected final List<OptionData> options = new ArrayList<>();

    public AbstractCommand(StMaryClient stMaryClient) {
        this.stMaryClient = stMaryClient;
        this.name = this.getClass().getSimpleName().toLowerCase();
        this.description = "No description provided.";
    }

    public abstract void execute(SlashCommandInteractionEvent event);

    public void run(SlashCommandInteractionEvent event) {
        if (this.cooldown > 0) {
            if (this.stMaryClient.getCooldownManager().hasCooldown(event.getUser().getId(), this.name) && this.stMaryClient.getCooldownManager().getRemainingCooldown(event.getUser().getId(), this.name) > 0) {
                long timeRemaining = this.stMaryClient.getCooldownManager().getRemainingCooldown(event.getUser().getId(), this.name);
                long timestampRemaining = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() + timeRemaining);

                event.reply(String.format("Please wait <t:%s:R>", timestampRemaining)).queue();
                return;
            }

            this.stMaryClient.getCooldownManager().addCooldown(event.getUser().getId(), this.name, this.cooldown);
        }

        this.execute(event);
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
