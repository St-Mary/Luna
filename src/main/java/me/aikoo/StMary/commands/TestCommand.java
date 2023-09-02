package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.abstracts.ButtonAbstract;
import me.aikoo.StMary.core.abstracts.CommandAbstract;
import me.aikoo.StMary.core.bases.CharacterBase;
import me.aikoo.StMary.core.bot.StMaryClient;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.lang.reflect.Method;

public class TestCommand extends CommandAbstract {
    public TestCommand(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "test";
        this.description = "Test command";
        this.cooldown = 10000L;

        this.setAdminCommand(true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, String language) {
        CharacterBase.Information character = this.stMaryClient.getCharacterManager().getCharacterByName("Maréchal Thorne", language).getCharacterInformation(language);
        CharacterBase.Dialog dialog = character.getDialog("1.1");

        try {
            CharacterBase.Option optionOne = dialog.getOptions().get("1.1.1");
            CharacterBase.Option optionTwo = dialog.getOptions().get("1.1.2");
            Method yesMethod = TestCommand.class.getMethod("onClickYesBtn", ButtonInteractionEvent.class, String.class);
            Method noMethod = TestCommand.class.getMethod("onClickNoBtn", ButtonInteractionEvent.class, String.class);

            CharacterBase.OptionBtn optionBtn1 = new CharacterBase.OptionBtn(optionOne.getId(), optionOne.getName(), optionOne.getIcon(), optionOne.getStyle(), stMaryClient, this, yesMethod);
            CharacterBase.OptionBtn optionBtn2 = new CharacterBase.OptionBtn(optionTwo.getId(), optionTwo.getName(), optionTwo.getIcon(), optionTwo.getStyle(), stMaryClient, this, noMethod);

            this.buttons.put(optionBtn1.getId(), optionBtn1);
            this.buttons.put(optionBtn2.getId(), optionBtn2);

            event.reply(stMaryClient.getCharacterManager().formatCharacterDialog(character, dialog)).addActionRow(optionBtn1.getButton(), optionBtn2.getButton()).queue(msg -> msg.retrieveOriginal().queue(res -> {
                stMaryClient.getButtonManager().addButtons(res.getId(), this.getArrayListButtons());

                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                if (stMaryClient.getButtonManager().isButtons(res.getId())) {
                                    stMaryClient.getButtonManager().removeButtons(res.getId());

                                    CharacterBase.Dialog noDialog = character.getDialog("1.1.2");
                                    String text = stMaryClient.getCharacterManager().formatCharacterDialog(character, noDialog);
                                    res.editMessage(text).setComponents().queue();
                                }
                            }
                        },
                        10000
                );
            }));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event, String language) {
    }

    public void onClickYesBtn(ButtonInteractionEvent event, String language) {
        CharacterBase.Information character = this.stMaryClient.getCharacterManager().getCharacterByName("Maréchal Thorne", language).getCharacterInformation(language);
        String dialog = stMaryClient.getCharacterManager().formatCharacterDialog(character, character.getDialog("1.1.1"));

        event.editMessage(dialog).setComponents().queue();
        stMaryClient.getButtonManager().removeButtons(event.getMessageId());
    }

    public void onClickNoBtn(ButtonInteractionEvent event, String language) {
        CharacterBase.Information character = this.stMaryClient.getCharacterManager().getCharacterByName("Maréchal Thorne", language).getCharacterInformation(language);
        String dialog = stMaryClient.getCharacterManager().formatCharacterDialog(character, character.getDialog("1.1.2"));

        event.editMessage(dialog).setComponents().queue();
        stMaryClient.getButtonManager().removeButtons(event.getMessageId());
    }
}
