package me.aikoo.StMary.core.abstracts;

import lombok.Getter;
import me.aikoo.StMary.core.bot.StMaryClient;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.lang.reflect.Method;

/**
 * Represents an abstract button that can be interacted with in a Discord message.
 */
public class ButtonAbstract {

    /**
     * Get the name of the button.
     */
    @Getter
    private final String name;

    /**
     * Get the unique identifier of the button.
     */
    @Getter
    private final String id;

    /**
     * Get the style (color) of the button.
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

    @Getter
    private Object classMethod;

    @Getter
    private Method method;

    @Getter
    private Object[] parameters;

    /**
     * Creates a new Button instance.
     *
     * @param id           The unique identifier of the button.
     * @param name         The name of the button.
     * @param style        The style (color) of the button.
     * @param emoji        The emoji associated with the button.
     * @param stMaryClient The StMaryClient instance.
     */
    public ButtonAbstract(String id, String name, ButtonStyle style, Emoji emoji, StMaryClient stMaryClient, Object classMethod, Method method, Object ...parameters) {
        this.id = id;
        this.name = name;
        this.style = style;
        this.emoji = emoji;
        this.stMaryClient = stMaryClient;
        this.classMethod = classMethod;
        this.method = method;
        this.parameters = parameters;
    }

    /**
     * Defines the action to be performed when the button is clicked.
     *
     * @param event The ButtonInteractionEvent triggered when the button is clicked.
     */
    public void onClick(ButtonInteractionEvent event, String language) {
        if (this.method == null) {
            return;
        }

        Object[] params = new Object[this.parameters.length + 2];

        params[0] = event;
        params[1] = language;

        System.arraycopy(this.parameters, 0, params, 2, this.parameters.length);

        try {
            this.method.invoke(this.classMethod, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a JDA button representation of this custom button.
     *
     * @return The JDA button representation of this custom button.
     */
    public net.dv8tion.jda.api.interactions.components.buttons.Button getButton() {
        return net.dv8tion.jda.api.interactions.components.buttons.Button.of(this.style, this.id, this.name, this.emoji);
    }
}
