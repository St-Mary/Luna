package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.abstracts.CommandAbstract;
import me.aikoo.StMary.core.bot.StMaryClient;
import me.aikoo.StMary.core.constants.BotConfigConstant;
import me.aikoo.StMary.core.database.AdministratorEntity;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class RemoveAdminCommand extends CommandAbstract {
    public RemoveAdminCommand(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "removeadmin";
        this.description = "Remove an admin";
        this.cooldown = 10000L;
        this.setAdminCommand(true);

        this.options.add(new OptionData(OptionType.STRING, "userid", "The old administrator")
                .setRequired(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, String language) {
        if (!event.getUser().getId().equals(BotConfigConstant.getOwnerId())) {
            String errMsg = "Seul le propriétaire du bot peut exécuter cette commande !";
            event.reply(errMsg).queue();
        }

        String userId = event.getOption("userid").getAsString();

        if (!userId.matches("[0-9]+")) {
            event.reply("This user doesn't exist").queue();
            return;
        }

        AdministratorEntity administrator = stMaryClient.getDatabaseManager().getAdministrator(Long.parseLong(userId));
        if (administrator == null) {
            String errMsg = "Cet utilisateur n'est pas administrateur !";
            event.reply(errMsg).queue();
            return;
        }

        stMaryClient.getDatabaseManager().delete(administrator);

        event.reply("L'utilisateur a été déstitué de son rôle d'administrateur !").queue();
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event, String language) {

    }
}
