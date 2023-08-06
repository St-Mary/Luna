package me.aikoo.StMary.command;

import me.aikoo.StMary.StMaryClient;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCommand {
    private final Logger LOGGER = LoggerFactory.getLogger(AbstractCommand.class);
    protected String name;
    protected String description;
    private final StMaryClient stMaryClient;
    private final List<OptionData> options = new ArrayList<>();

    public AbstractCommand(StMaryClient stMaryClient) {
        this.stMaryClient = stMaryClient;
        this.name = this.getClass().getSimpleName().toLowerCase();
        this.description = "No description provided.";
    }

    public abstract void execute(SlashCommandInteractionEvent event);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public CommandData buildCommandData() {
        SlashCommandData data = Commands.slash(this.name, this.description);
        if (!this.options.isEmpty()) data.addOptions(this.options);
        return data;
    }
}
