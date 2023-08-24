package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.bot.StMaryClient;
import me.aikoo.StMary.core.abstracts.CommandAbstract;
import me.aikoo.StMary.core.constants.BotConfigConstant;
import me.aikoo.StMary.core.database.AdministratorEntity;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Objects;

public class AddAdminCommand extends CommandAbstract {
    public AddAdminCommand(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "adminaddadmin";
        this.description = "Add an admin";
        this.cooldown = 10000L;
        this.setAdminCommand(true);

        this.options.add(new OptionData(OptionType.USER, "user", "The new administrator")
                .setRequired(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!event.getUser().getId().equals(BotConfigConstant.getOwnerId())) {
            String errMsg = stMaryClient.getTextManager().generateError("Ajout d'un administrateur", "Seul le propriétaire du bot peut exécuter cette commande !");
            event.reply(errMsg).queue();
        }

        AdministratorEntity administrators = stMaryClient.getDatabaseManager().getAdministrator(Objects.requireNonNull(event.getOption("user")).getAsUser().getIdLong());
        if (administrators != null) {
            String errMsg = stMaryClient.getTextManager().generateError("Ajout d'un administrateur", "Cet utilisateur est déjà administrateur !");
            event.reply(errMsg).queue();
            return;
        }

        AdministratorEntity newAdmin = new AdministratorEntity();
        newAdmin.setDiscordId(Objects.requireNonNull(event.getOption("user")).getAsUser().getIdLong());

        stMaryClient.getDatabaseManager().save(newAdmin);

        event.reply("L'utilisateur a été ajouté en tant qu'administrateur !").queue();
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event) {

    }
}
