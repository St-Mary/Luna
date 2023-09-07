package me.aikoo.stmary.core.abstracts;

import lombok.Getter;
import me.aikoo.stmary.core.bot.StMaryClient;
import me.aikoo.stmary.core.managers.ButtonManager;
import me.aikoo.stmary.core.managers.TextManager;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Represents an abstract button that can be interacted with in a Discord message.
 */
public class ButtonAbstract {

    private final Logger LOGGER = LoggerFactory.getLogger(ButtonAbstract.class);

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
     * Get the class method to be executed when the button is clicked.
     */
    @Getter
    private final Object classMethod;
    /**
     * Get the method to be executed when the button is clicked.
     */
    @Getter
    private final Method method;
    /**
     * Get the parameters to be passed to the method when the button is clicked.
     */
    @Getter
    private final Object[] parameters;
    /**
     * Get the emoji associated with the button, if any.
     */
    @Getter
    private Emoji emoji = null;

    /**
     * Creates a new Button instance.
     *
     * @param id           The unique identifier of the button.
     * @param name         The name of the button.
     * @param style        The style (color) of the button.
     * @param emoji        The emoji associated with the button.
     * @param stMaryClient The StMaryClient instance.
     */
    public ButtonAbstract(String id, String name, ButtonStyle style, Emoji emoji, StMaryClient stMaryClient, Object classMethod, Method method, Object... parameters) {
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

        // Get slash command
        String slashCommand = Objects.requireNonNull(event.getMessage().getInteraction()).getName();
        if (this.stMaryClient.getCache().get("actionWaiter_" + event.getUser().getId()).isPresent() && !this.stMaryClient.getCache().get("actionWaiter_" + event.getUser().getId()).get().equals(slashCommand)) {
            event.reply(TextManager.createText("command_error_action_waiter", language).buildError()).setEphemeral(true).queue();
            return;
        }

        Object[] params = new Object[this.parameters.length + 2];

        params[0] = event;
        params[1] = language;

        System.arraycopy(this.parameters, 0, params, 2, this.parameters.length);

        try {
            this.method.invoke(this.classMethod, params);
        } catch (InvocationTargetException | IllegalAccessException e) {
            String errorText = TextManager.createText("command_error", language).buildError();
            event.reply(errorText).setEphemeral(true).queue();

            if (stMaryClient.getCache().get("actionWaiter_" + event.getUser().getId()).isPresent()) {
                stMaryClient.getCache().delete("actionWaiter_" + event.getUser().getId());
            }

            ButtonManager.removeButtons(event.getMessage().getId());

            TextManager.sendError(slashCommand, e);
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
