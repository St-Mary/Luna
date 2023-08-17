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

public class Adminaddadmin extends AbstractCommand {
    public Adminaddadmin(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "adminaddadmin";
        this.description = "Add an admin";
        this.cooldown = 10000L;
        this.setAdminCommand(true);

        this.options.add(new OptionData(OptionType.USER, "user", "The new administrator")
                .setRequired(true));
    }

    @Override
    public void execute(StMaryClient client, SlashCommandInteractionEvent event) {
        if (!event.getUser().getId().equals(BotConfig.getOwnerId())) {
            EmbedBuilder error = client.getTextManager().generateErrorEmbed("Ajout d'un administrateur", "Seul le propriétaire du bot peut exécuter cette commande !");
            event.replyEmbeds(error.build()).queue();
        }

        Administrators administrators = client.getDatabaseManager().getAdministrator(Objects.requireNonNull(event.getOption("user")).getAsUser().getIdLong());
        if (administrators != null) {
            EmbedBuilder error = client.getTextManager().generateErrorEmbed("Ajout d'un administrateur", "Cet utilisateur est déjà administrateur !");
            event.replyEmbeds(error.build()).queue();
            return;
        }

        Administrators newAdmin = new Administrators();
        newAdmin.setDiscordId(Objects.requireNonNull(event.getOption("user")).getAsUser().getIdLong());

        client.getDatabaseManager().createOrUpdate(newAdmin);

        event.reply("L'utilisateur a été ajouté en tant qu'administrateur !").queue();
    }

    @Override
    public void autoComplete(StMaryClient client, CommandAutoCompleteInteractionEvent event) {

    }
}
