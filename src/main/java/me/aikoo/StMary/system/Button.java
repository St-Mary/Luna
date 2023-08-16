package me.aikoo.StMary.system;

import lombok.Getter;
import me.aikoo.StMary.core.StMaryClient;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

public abstract class Button {

    @Getter
    private final String name;

    @Getter
    private final String id;

    @Getter
    private final ButtonStyle style;

    @Getter
    private Emoji emoji = null;

    private final StMaryClient stMaryClient;

    public Button(String id, String name, ButtonStyle style, Emoji emoji, StMaryClient stMaryClient) {
        this.id = id;
        this.name = name;
        this.style = style;
        this.emoji = emoji;
        this.stMaryClient = stMaryClient;
    }

    public abstract void onClick(ButtonInteractionEvent event);

    public net.dv8tion.jda.api.interactions.components.buttons.Button getButton() {
        return net.dv8tion.jda.api.interactions.components.buttons.Button.of(this.style, this.id, this.name, this.emoji);
    }
}
