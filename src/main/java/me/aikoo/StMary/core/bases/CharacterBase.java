package me.aikoo.StMary.core.bases;

import lombok.Getter;
import me.aikoo.StMary.core.abstracts.ButtonAbstract;
import me.aikoo.StMary.core.bot.StMaryClient;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class CharacterBase {

    private final HashMap<String, Information> informations;

    public CharacterBase(HashMap<String, Information> informations) {
        this.informations = informations;
    }

    public Information getCharacterInformation(String language) {
        return this.informations.get(language);
    }

    public static class Information {

        @Getter
        private final String name;
        @Getter
        private final String description;
        @Getter
        private final HashMap<String, Dialog> dialogs;

        public Information(String name, String description, HashMap<String, Dialog> dialogs) {
            this.name = name;
            this.description = description;
            this.dialogs = dialogs;
        }

        public Dialog getDialog(String dialogId) {
            return this.dialogs.get(dialogId);
        }
    }

    public static class Dialog {
        @Getter
        private final String text;
        @Getter
        private final boolean isQuestion;
        @Getter
        private final String question;
        @Getter
        private final HashMap<String, Option> options;

        public Dialog(String text, boolean isQuestion, String question, HashMap<String, Option> options) {
            this.text = text;
            this.isQuestion = isQuestion;
            this.question = question;
            this.options = options;
        }
    }

    public static class Option {
        @Getter
        private final String name;
        @Getter
        private final String icon;
        @Getter
        private final ButtonStyle style;
        @Getter
        private final Dialog nextDialog;

        public Option(String name, String icon, ButtonStyle style, Dialog nextDialog) {
            this.name = name;
            this.icon = icon;
            this.style = style;
            this.nextDialog = nextDialog;
        }
    }

    public static class OptionBtn extends ButtonAbstract {
        @Getter
        private final String id;
        @Getter
        private final String name;
        @Getter
        private final String icon;
        @Getter
        private final ButtonStyle style;
        private final Method method;
        private final Object classMethod;

        public OptionBtn(String id, String name, String icon, ButtonStyle style, StMaryClient stMaryClient, Object classMethod, Method method) {
            super(id, name, style, Emoji.fromUnicode(icon), stMaryClient);
            this.id = id;
            this.name = name;
            this.icon = icon;
            this.style = style;
            this.classMethod = classMethod;
            this.method = method;
        }

        @Override
        public void onClick(ButtonInteractionEvent event, String language) {
            try {
                Object[] args = {event, language};
                method.invoke(classMethod, args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
