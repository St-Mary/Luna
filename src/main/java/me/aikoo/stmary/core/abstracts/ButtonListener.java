package me.aikoo.stmary.core.abstracts;

import java.util.*;
import lombok.Setter;
import me.aikoo.stmary.core.bases.CharacterBase;
import me.aikoo.stmary.core.bot.StMaryClient;
import me.aikoo.stmary.core.managers.CharacterManager;
import me.aikoo.stmary.core.managers.TextManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

/** The ButtonListener class represents a listener for button interactions. */
public abstract class ButtonListener extends ListenerAdapter implements EventListener {
  protected final StMaryClient stMaryClient;
  protected final String authorId;
  protected final String language;
  protected final ArrayList<Button> buttons;
  protected final Long menuDuration;
  protected final boolean isDialog;
  @Setter protected String messageId;
  protected Message message;
  protected Timer timer;

  /**
   * Creates a new ButtonListener.
   *
   * @param stMaryClient The StMaryClient instance.
   * @param authorId The id of the author of the button menu.
   * @param language The language of the player.
   * @param buttons The buttons to display.
   * @param menuDuration The duration of the button menu.
   * @param isDialog Whether the button menu is a dialog or not.
   */
  public ButtonListener(
      StMaryClient stMaryClient,
      String authorId,
      String language,
      ArrayList<Button> buttons,
      Long menuDuration,
      boolean isDialog) {
    this.stMaryClient = stMaryClient;
    this.authorId = authorId;
    this.language = language;
    this.buttons = buttons;
    this.menuDuration = menuDuration;
    this.isDialog = isDialog;
  }

  /**
   * Sends a button menu.
   *
   * @param e The SlashCommandInteractionEvent.
   * @param text The text to display.
   */
  public void sendButtonMenu(SlashCommandInteractionEvent e, String text) {
    e.reply(text)
        .addActionRow(buttons)
        .queue(
            q ->
                q.retrieveOriginal()
                    .queue(
                        m -> {
                          this.messageId = m.getId();
                          this.message = m;

                          createTimer();
                        }));
  }

  /** Creates a timer to close the button menu. */
  public void createTimer() {
    timer = new Timer();
    timer.schedule(
        new TimerTask() {
          @Override
          public void run() {
            message.editMessage(message.getContentRaw()).setComponents().queue();
            stMaryClient.getJda().removeEventListener(this);
          }
        },
        menuDuration);
  }

  /**
   * Executes when a button is clicked.
   *
   * @param event The ButtonInteractionEvent triggered when the button is clicked.
   */
  @Override
  public void onButtonInteraction(ButtonInteractionEvent event) {
    if (event.getGuild() == null || event.getUser().isBot()) return;
    if (!event.getMessageId().equals(messageId)) return;
    if (!event.getUser().getId().equals(authorId) && authorId.isEmpty()) return;
    this.message = event.getMessage();

    if (isDialog) {
      executeDialog(event);
    } else {
      buttonClick(event);
    }
  }

  /**
   * Executes when a button is clicked in a dialog.
   *
   * @param event The ButtonInteractionEvent triggered when the button is clicked.
   */
  private void executeDialog(ButtonInteractionEvent event) {
    CharacterBase.Choice choice = CharacterManager.getChoice(event.getComponentId());

    if (choice == null) {
      handleErrorResponse(event);
      return;
    }

    CharacterBase.Dialog dialog = choice.getNextDialog();

    if (!dialog.getChoices().isEmpty()) {
      updateMessage(event, dialog);
    } else {
      closeBtnMenu(event, dialog.getText(language));
    }
  }

  /**
   * Updates the message.
   *
   * @param event The ButtonInteractionEvent triggered when the button is clicked.
   * @param dialog The dialog to update the message with.
   */
  private void updateMessage(ButtonInteractionEvent event, CharacterBase.Dialog dialog) {
    buttons.clear();
    timer.cancel();

    for (CharacterBase.Choice choice : dialog.getChoices()) {
      buttons.add(choice.getButton(language));
    }

    createTimer();
    event.editMessage(dialog.getText(language)).setComponents().setActionRow(buttons).queue();
  }

  /**
   * Closes the button menu.
   *
   * @param event The ButtonInteractionEvent triggered when the button is clicked.
   * @param text The text to display.
   */
  protected void closeBtnMenu(ButtonInteractionEvent event, String text) {
    timer.cancel();
    stMaryClient.getJda().removeEventListener(this);
    event.editMessage(text).setComponents().queue();
  }

  /**
   * Handles the error response.
   *
   * @param event The ButtonInteractionEvent triggered when the button is clicked.
   */
  private void handleErrorResponse(ButtonInteractionEvent event) {
    String errorText = TextManager.createText("command_error", language).buildError();
    event.reply(errorText).setEphemeral(true).queue();
    stMaryClient.getJda().removeEventListener(this);
  }

  /**
   * Executes when a button is clicked.
   *
   * @param event The ButtonInteractionEvent triggered when the button is clicked.
   */
  public abstract void buttonClick(ButtonInteractionEvent event);
}
