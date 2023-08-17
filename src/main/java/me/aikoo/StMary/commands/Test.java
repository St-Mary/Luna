package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.system.Button;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

public class Test extends AbstractCommand {
    public Test(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "test";
        this.description = "Test command";
        this.cooldown = 10000L;

        this.buttons.put("test", new testBtn());
        this.options.add(new OptionData(OptionType.STRING, "test", "Test option")
                        .setAutoComplete(true)
                .setRequired(true));
    }

    @Override
    public void execute(StMaryClient client, SlashCommandInteractionEvent event) {
        String text = client.getTextManager().getText("start_adventure");
        String title = client.getTextManager().getTitle("start_adventure");
        event.reply(client.getTextManager().generateScene(title, text)).addActionRow(this.buttons.get("test").getButton()).queue(msg -> {
            msg.retrieveOriginal().queue(res -> {
                stMaryClient.getButtonManager().addButtons(res.getId(), this.getArrayListButtons());
            });
        });
    }

    @Override
    public void autoComplete(StMaryClient client, CommandAutoCompleteInteractionEvent event) {
        event.replyChoice("Un", "deux").queue();
    }

    private class testBtn extends Button {
        public testBtn() {
            super("test_btn", "Test", ButtonStyle.PRIMARY, Emoji.fromUnicode("\uD83C\uDF32"), stMaryClient);
        }

        @Override
        public void onClick(ButtonInteractionEvent event) {
            event.reply("Test success").queue();
            // event.editButton(getButton().asDisabled()).queue();
        }
    }
}
