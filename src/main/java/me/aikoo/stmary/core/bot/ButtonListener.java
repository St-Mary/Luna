package me.aikoo.stmary.core.bot;

import me.aikoo.stmary.core.bases.CharacterBase;
import me.aikoo.stmary.core.managers.CharacterManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.Date;

public abstract class ButtonListener extends ListenerAdapter {
    private final StMaryClient stMaryClient;
    private String messageId;
    private Date startMenuTimestamp;
    private final Long menuDuration;
    private final String authorId;
    private final String language;
    private final boolean isBaseBtnMenu;
    private final boolean isDialog;
    private final ArrayList<Button> buttons;

    public ButtonListener(StMaryClient stMaryClient, String authorId, String language, ArrayList<Button> buttons, Long menuDuration, boolean isBaseBtnMenu, boolean isDialog) {
        this.stMaryClient = stMaryClient;
        this.authorId = authorId;
        this.language = language;
        this.buttons = buttons;
        this.menuDuration = menuDuration;
        this.isBaseBtnMenu = isBaseBtnMenu;
        this.isDialog = isDialog;
    }

    public void sendButtonMenu(SlashCommandInteractionEvent e, String text) {
        e.reply(text).addActionRow(buttons).queue(q -> {
            q.retrieveOriginal().queue(m -> {
                this.messageId = m.getId();
                this.startMenuTimestamp = new Date();
            });
        });
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getGuild() == null) return;
        if (!event.getMessageId().equals(messageId)) return;
        if (!event.getUser().getId().equals(authorId) && authorId.isEmpty()) return;

        if (new Date().getTime() - startMenuTimestamp.getTime() > menuDuration) {
            Message message = event.getMessage();
            message.editMessage(message.getContentRaw()).setComponents().queue();
            return;
        }

        if (isBaseBtnMenu) {
            executeBaseMenu(event);
        } else {
            buttonClick(event);
        }
    };

    public void executeBaseMenu(ButtonInteractionEvent event) {
        if (isDialog) {
            CharacterBase.Choice choice = CharacterManager.getChoice(event.getComponentId());

            if (choice == null) {
                event.reply("Error").queue();
                return;
            }

            CharacterBase.Dialog dialog = choice.getNextDialog();
            event.reply(dialog.getText(language)).queue();
        } else {
            event.reply("Menu").queue();
        }
    }

    public abstract void buttonClick(ButtonInteractionEvent event);
}
