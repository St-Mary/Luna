package me.aikoo.StMary.commands;

import me.aikoo.StMary.BotConfig;
import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.database.entities.AdministratorEntity;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class RemoveAdminCommand extends AbstractCommand {
    public RemoveAdminCommand(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "adminremoveadmin";
        this.description = "Remove an admin";
        this.cooldown = 10000L;
        this.setAdminCommand(true);

        this.options.add(new OptionData(OptionType.STRING, "userid", "The old administrator")
                .setRequired(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!event.getUser().getId().equals(BotConfig.getOwnerId())) {
            String errMsg = stMaryClient.getTextManager().generateError("Retrait d'un administrateur", "Seul le propriétaire du bot peut exécuter cette commande !");
            event.reply(errMsg).queue();
        }

        String userId = event.getOption("userid").getAsString();

        if (!userId.matches("[0-9]+")) {
            event.reply("This user doesn't exist").queue();
            return;
        }

        AdministratorEntity administrator = stMaryClient.getDatabaseManager().getAdministrator(Long.parseLong(userId));
        if (administrator == null) {
            String errMsg = stMaryClient.getTextManager().generateError("Retrait d'un administrateur", "Cet utilisateur n'est pas administrateur !");
            event.reply(errMsg).queue();
            return;
        }

        stMaryClient.getDatabaseManager().delete(administrator);

        event.reply("L'utilisateur a été déstitué de son rôle d'administrateur !").queue();
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event) {

    }
}
