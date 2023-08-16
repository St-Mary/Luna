package me.aikoo.StMary.core.managers;

import me.aikoo.StMary.commands.AbstractCommand;
import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.system.Button;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class ButtonManager extends ListenerAdapter {

    private final StMaryClient stMaryClient;
    private final HashMap<String, ArrayList<Button>> commands = new HashMap<>();
    private final HashMap<String, Button> buttons = new HashMap<>();

    public ButtonManager(StMaryClient stMaryClient) {
        this.stMaryClient = stMaryClient;
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (!commands.containsKey(event.getMessageId())) return;
        Button button = commands.get(event.getMessageId()).stream().filter(btn -> btn.getId().equals(event.getComponentId())).findFirst().orElse(null);
        if (button == null) return;

        button.onClick(event);
        commands.remove(event.getId());
    }

    public void addButtons(String id, ArrayList<Button> buttons) {
        commands.put(id, buttons);
    }
}
