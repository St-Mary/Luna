package me.aikoo.StMary.core.managers;

import me.aikoo.StMary.core.bot.StMaryClient;
import me.aikoo.StMary.core.abstracts.ButtonAbstract;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Manages interactive buttons in the bot.
 */
public class ButtonManager extends ListenerAdapter {

    private final StMaryClient stMaryClient;
    private final HashMap<String, ArrayList<ButtonAbstract>> commands = new HashMap<>();

    /**
     * Constructs a ButtonManager with the provided StMaryClient instance.
     *
     * @param stMaryClient The StMaryClient instance.
     */
    public ButtonManager(StMaryClient stMaryClient) {
        this.stMaryClient = stMaryClient;
    }

    /**
     * Handles button interactions.
     *
     * @param event The ButtonInteractionEvent triggered by a button click.
     */
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        // Check if the message ID is associated with any registered buttons
        if (!commands.containsKey(event.getMessageId())) return;

        // Find the button that was clicked based on the component ID
        ButtonAbstract button = commands.get(event.getMessageId()).stream()
                .filter(btn -> btn.getId().equals(event.getComponentId()))
                .findFirst()
                .orElse(null);

        // If the button is not found, return
        if (button == null) return;

        // Execute the onClick action of the button
        button.onClick(event);

        // Remove the executed button from the commands
        commands.remove(event.getId());
    }

    /**
     * Adds a list of buttons associated with a message ID.
     *
     * @param id      The ID of the message the buttons are associated with.
     * @param buttons The list of Button objects to add.
     */
    public void addButtons(String id, ArrayList<ButtonAbstract> buttons) {
        commands.put(id, buttons);
    }
}
