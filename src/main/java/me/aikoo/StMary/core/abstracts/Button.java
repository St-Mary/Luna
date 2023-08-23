package me.aikoo.StMary.core.abstracts;

import lombok.Getter;
import me.aikoo.StMary.core.StMaryClient;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

/**
 * Represents an abstract button that can be interacted with in a Discord message.
 */
public abstract class Button {

    /**
     * Get the name of the button.
     *
     * @return The name of the button.
     */
    @Getter
    private final String name;

    /**
     * Get the unique identifier of the button.
     *
     * @return The unique identifier of the button.
     */
    @Getter
    private final String id;

    /**
     * Get the style (color) of the button.
     *
     * @return The style of the button.
     */
    @Getter
    private final ButtonStyle style;

    private final StMaryClient stMaryClient;

    /**
     * Get the emoji associated with the button, if any.
     *
     * @return The emoji associated with the button.
     */
    @Getter
    private Emoji emoji = null;

    /**
     * Creates a new Button instance.
     *
     * @param id            The unique identifier of the button.
     * @param name          The name of the button.
     * @param style         The style (color) of the button.
     * @param emoji         The emoji associated with the button.
     * @param stMaryClient  The StMaryClient instance.
     */
    public Button(String id, String name, ButtonStyle style, Emoji emoji, StMaryClient stMaryClient) {
        this.id = id;
        this.name = name;
        this.style = style;
        this.emoji = emoji;
        this.stMaryClient = stMaryClient;
    }

    /**
     * Defines the action to be performed when the button is clicked.
     *
     * @param event The ButtonInteractionEvent triggered when the button is clicked.
     */
    public abstract void onClick(ButtonInteractionEvent event);

    /**
     * Get a JDA button representation of this custom button.
     *
     * @return The JDA button representation of this custom button.
     */
    public net.dv8tion.jda.api.interactions.components.buttons.Button getButton() {
        return net.dv8tion.jda.api.interactions.components.buttons.Button.of(this.style, this.id, this.name, this.emoji);
    }
}
