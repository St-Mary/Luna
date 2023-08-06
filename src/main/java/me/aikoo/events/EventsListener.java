package me.aikoo.events;

import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class EventsListener extends ListenerAdapter {
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        System.out.println("I am ready!");
    }
}
