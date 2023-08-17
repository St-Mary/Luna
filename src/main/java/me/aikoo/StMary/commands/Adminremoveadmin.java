package me.aikoo.StMary.commands;

import me.aikoo.StMary.BotConfig;
import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.database.entities.Administrators;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Objects;

public class Adminremoveadmin extends AbstractCommand {
    public Adminremoveadmin(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "adminremoveadmin";
        this.description = "Remove an admin";
        this.cooldown = 10000L;
        this.setAdminCommand(true);

        this.options.add(new OptionData(OptionType.USER, "user", "The old administrator")
                .setRequired(true));
    }

    @Override
    public void execute(StMaryClient client, SlashCommandInteractionEvent event) {
        if (!event.getUser().getId().equals(BotConfig.getOwnerId())) {
            EmbedBuilder error = client.getTextManager().generateErrorEmbed("Retrait d'un administrateur", "Seul le propriétaire du bot peut exécuter cette commande !");
            event.replyEmbeds(error.build()).queue();
        }

        Administrators administrator = client.getDatabaseManager().getAdministrator(Objects.requireNonNull(event.getOption("user")).getAsUser().getIdLong());
        if (administrator == null) {
            EmbedBuilder error = client.getTextManager().generateErrorEmbed("Retrait d'un administrateur", "Cet utilisateur n'est pas administrateur !");
            event.replyEmbeds(error.build()).queue();
            return;
        }

        client.getDatabaseManager().delete(administrator);

        event.reply("L'utilisateur a été déstitué de son rôle d'administrateur !").queue();
    }

    @Override
    public void autoComplete(StMaryClient client, CommandAutoCompleteInteractionEvent event) {

    }
}
