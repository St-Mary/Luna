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
        CharacterBase.Dialog dialog = character.getDialog("1");

        CharacterBase.Option optionOne = dialog.getOptions().get("1");

        try {
            Method yesMethod = TestCommand.class.getMethod("onClickYesBtn", ButtonInteractionEvent.class, String.class);

            CharacterBase.OptionBtn optionBtn1 = new CharacterBase.OptionBtn("1", optionOne.getName(), optionOne.getIcon(), optionOne.getStyle(), stMaryClient, this, yesMethod);

            this.buttons.put("1", optionBtn1);
            event.reply(stMaryClient.getCharacterManager().formatCharacterDialog(character, dialog)).addActionRow(optionBtn1.getButton()).queue(msg -> msg.retrieveOriginal().queue(res -> {
                stMaryClient.getButtonManager().addButtons(res.getId(), this.getArrayListButtons());
            }));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event, String language) {
    }

    public void onClickYesBtn(ButtonInteractionEvent event, String language) {
        event.reply("Yes").queue();
    }
}
