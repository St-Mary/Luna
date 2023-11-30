package com.stmarygate.discord.commands;

import com.stmarygate.discord.core.abstracts.ButtonListener;
import com.stmarygate.discord.core.abstracts.CommandAbstract;
import com.stmarygate.discord.core.bases.CharacterBase;
import com.stmarygate.discord.core.bot.StMaryClient;
import com.stmarygate.discord.core.managers.CharacterManager;
import java.util.ArrayList;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

/** The Test command. */
public class TestCommand extends CommandAbstract {

  /**
   * Constructs a Test command.
   *
   * @param stMaryClient The StMaryClient instance.
   */
  public TestCommand(StMaryClient stMaryClient) {
    super(stMaryClient);

    this.name = "test";
    this.description = "Test command";
    this.cooldown = 3000L;

    this.setAdminCommand(true);
  }

  /**
   * Executes the Test command.
   *
   * @param event The SlashCommandInteractionEvent triggered when the button is clicked.
   * @param language The language of the player
   */
  @Override
  public void execute(SlashCommandInteractionEvent event, String language) {
    CharacterBase character = CharacterManager.getCharacter("1");
    ArrayList<Button> buttons = new ArrayList<>();

    for (CharacterBase.Choice choice : character.getDialog("1.1").getChoices()) {
      buttons.add(choice.getButton(language));
    }

    ButtonListener buttonListener =
        new ButtonListener(stMaryClient, event.getUser().getId(), language, buttons, 25000L, true) {
          @Override
          public void buttonClick(ButtonInteractionEvent event) {
            // Unused method for this command
          }
        };

    stMaryClient.getJda().addEventListener(buttonListener);
    buttonListener.sendButtonMenu(event, character.getDialog("1.1").printDialog(language));
  }

  /**
   * Auto complete method for the Test command.
   *
   * @param event The CommandAutoCompleteInteractionEvent triggered when the button is clicked.
   * @param language The language of the player
   */
  @Override
  public void autoComplete(CommandAutoCompleteInteractionEvent event, String language) {
    // Unused method for this command
  }
}
