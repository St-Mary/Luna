package me.aikoo.stmary.discord.core.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import me.aikoo.stmary.discord.core.abstracts.ButtonAbstract;
import me.aikoo.stmary.discord.core.database.PlayerEntity;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import org.jetbrains.annotations.NotNull;

/** Manages interactive buttons in the bot. */
public class ButtonManager extends ListenerAdapter {

  private static final HashMap<String, ArrayList<ButtonAbstract>> commands = new HashMap<>();

  /**
   * Adds a list of buttons associated with a message ID.
   *
   * @param id The ID of the message the buttons are associated with.
   * @param buttons The list of Button objects to add.
   */
  public static void addButtons(String id, List<ButtonAbstract> buttons) {
    commands.put(id, new ArrayList<>(buttons));
  }

  /**
   * Removes a list of buttons associated with a message ID.
   *
   * @param id The ID of the message the buttons are associated with.
   */
  public static void removeButtons(String id) {
    commands.remove(id);
  }

  /**
   * Checks if a message ID is associated with any registered buttons.
   *
   * @param id The ID of the message to check.
   * @return Whether the message ID is associated with any registered buttons.
   */
  public static boolean isButtons(String id) {
    return commands.containsKey(id);
  }

  /**
   * Handles button interactions.
   *
   * @param event The ButtonInteractionEvent triggered by a button click.
   */
  @Override
  public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
    // Check if the message ID is associated with any registered buttons
    if (!commands.containsKey(event.getMessageId())) {
      return;
    }
    String authorId = event.getUser().getId();
    String language = (event.getGuild().getLocale() == DiscordLocale.FRENCH) ? "fr" : "en";
    PlayerEntity player = DatabaseManager.getPlayer(Long.parseLong(authorId));
    language = (player != null) ? player.getLanguage() : language;

    // Find the button that was clicked based on the component ID
    ButtonAbstract button =
        commands.get(event.getMessageId()).stream()
            .filter(btn -> btn.getId().equals(event.getComponentId()))
            .findFirst()
            .orElse(null);

    // If the button is not found, return
    if (button == null) {
      return;
    }

    // Execute the onClick action of the button
    button.onClick(event, language);

    // Remove the executed button from the commands
    commands.remove(event.getId());
  }
}
