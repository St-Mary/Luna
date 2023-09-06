package me.aikoo.stmary.core.bases;

import lombok.Getter;
import me.aikoo.stmary.core.abstracts.ButtonAbstract;
import me.aikoo.stmary.core.bot.StMaryClient;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Represents a character in the game.
 */
public class CharacterBase {

    /**
     * Get the informations of the character.
     */
    private final HashMap<String, Information> informations;

    /**
     * Creates a new Character instance with the specified informations.
     *
     * @param informations The informations of the character.
     */
    public CharacterBase(HashMap<String, Information> informations) {
        this.informations = informations;
    }

    /**
     * Get the information of the character in the specified language.
     *
     * @param language The language to get the information in.
     * @return The information of the character in the specified language.
     */
    public Information getCharacterInformation(String language) {
        return this.informations.get(language);
    }

    /**
     * Get the dialog of the character in the specified language.
     */
    public static class Information {

        /**
         * Get the id of the character.
         */
        @Getter
        private final String id;

        /**
         * Get the name of the character.
         */
        @Getter
        private final String name;

        /**
         * Get the description of the character.
         */
        @Getter
        private final String description;

        /**
         * Get the dialogs of the character.
         */
        @Getter
        private final HashMap<String, Dialog> dialogs;

        /**
         * Creates a new CharacterInformation instance with the specified id, name, description, and dialogs.
         *
         * @param id          The id of the character.
         * @param name        The name of the character.
         * @param description The description of the character.
         * @param dialogs     The dialogs of the character.
         */
        public Information(String id, String name, String description, HashMap<String, Dialog> dialogs) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.dialogs = dialogs;
        }

        /**
         * Get the dialog of the character in the specified language.
         *
         * @param dialogId The id of the dialog to get.
         * @return The dialog of the character in the specified language.
         */
        public Dialog getDialog(String dialogId) {
            if (this.dialogs.get(dialogId) == null) {
                for (Dialog dialog : this.dialogs.values()) {
                    if (dialog.getOptions() != null && dialog.getOptions().get(dialogId) != null) {
                        return dialog.getOptions().get(dialogId).getDialog();
                    }
                }
            }

            return this.dialogs.get(dialogId);
        }
    }

    /**
     * Get the dialog of the character in the specified language.
     */
    public static class Dialog {

        /**
         * Get the id of the dialog.
         */
        @Getter
        private final String id;

        /**
         * Get the text of the dialog.
         */
        @Getter
        private final String text;

        /**
         * Get if the dialog is a question.
         */
        @Getter
        private final boolean isQuestion;

        /**
         * Get the question of the dialog.
         */
        @Getter
        private final String question;

        /**
         * Get the options of the dialog.
         */
        @Getter
        private final HashMap<String, Option> options;

        /**
         * Creates a new Dialog instance with the specified id, text, isQuestion, question, and options.
         *
         * @param id         The id of the dialog.
         * @param text       The text of the dialog.
         * @param isQuestion If the dialog is a question.
         * @param question   The question of the dialog.
         * @param options    The options of the dialog.
         */
        public Dialog(String id, String text, boolean isQuestion, String question, HashMap<String, Option> options) {
            this.id = id;
            this.text = text;
            this.isQuestion = isQuestion;
            this.question = question;
            this.options = options;
        }
    }

    /**
     * Get the option of the dialog in the specified language.
     */
    public static class Option {

        /**
         * Get the id of the option.
         */
        @Getter
        private final String id;

        /**
         * Get the name of the option.
         */
        @Getter
        private final String name;

        /**
         * Get the icon of the option.
         */
        @Getter
        private final String icon;

        /**
         * Get the style of the option.
         */
        @Getter
        private final ButtonStyle style;

        /**
         * Get the dialog of the option.
         */
        @Getter
        private final Dialog dialog;

        /**
         * Creates a new Option instance with the specified id, name, icon, style, and dialog.
         *
         * @param id     The id of the option.
         * @param name   The name of the option.
         * @param icon   The icon of the option.
         * @param style  The style of the option.
         * @param dialog The dialog of the option.
         */
        public Option(String id, String name, String icon, ButtonStyle style, Dialog dialog) {
            this.id = id;
            this.name = name;
            this.icon = icon;
            this.style = style;
            this.dialog = dialog;
        }
    }

    /**
     * Get the option of the dialog in the specified language.
     */
    public static class OptionBtn extends ButtonAbstract {

        /**
         * Get the id of the option.
         */
        @Getter
        private final String id;

        /**
         * Get the name of the option.
         */
        @Getter
        private final String name;

        /**
         * Get the icon of the option.
         */
        @Getter
        private final String icon;

        /**
         * Get the style of the option.
         */
        @Getter
        private final ButtonStyle style;

        private final Method method;
        private final Object classMethod;

        /**
         * Creates a new Option instance with the specified id, name, icon, style, and dialog.
         *
         * @param id           The id of the option.
         * @param name         The name of the option.
         * @param icon         The icon of the option.
         * @param style        The style of the option.
         * @param stMaryClient The StMaryClient instance.
         * @param classMethod  The class method to be executed when the button is clicked.
         * @param method       The method to be executed when the button is clicked.
         */
        public OptionBtn(String id, String name, String icon, ButtonStyle style, StMaryClient stMaryClient, Object classMethod, Method method) {
            super(id, name, style, Emoji.fromFormatted(icon), stMaryClient, null, null);
            this.id = id;
            this.name = name;
            this.icon = icon;
            this.style = style;
            this.classMethod = classMethod;
            this.method = method;
        }

        /**
         * Executes the method when the button is clicked.
         *
         * @param event    The ButtonInteractionEvent triggered when the button is clicked.
         * @param language The language of the player.
         */
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
