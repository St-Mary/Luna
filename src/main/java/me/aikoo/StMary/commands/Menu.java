package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.database.entities.Player;
import me.aikoo.StMary.system.Title;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Menu extends AbstractCommand {

    public Menu(StMaryClient stMaryClient) {
        super(stMaryClient);

        this.name = "menu";
        this.description = ":evergreen_tree: Menu principal";
        this.cooldown = 10000L;
    }

    @Override
    public void execute(StMaryClient client, SlashCommandInteractionEvent event) {
        Player player = client.getDatabaseManager().getPlayer(event.getUser().getIdLong());

        if (player == null) {
            event.reply("Vous n'avez pas encore commencé l'aventure !").queue();
        } else {
            Title title = player.getCurrentTitle(client);
            System.out.println(title);
            String icon = title.getIcon();
            String town = (player.getCurrentLocationTown() != null) ? player.getCurrentLocationTown() : "Aucune ville";
            String place = player.getCurrentLocationPlace();

            String stringBuilder = String.format("### %s | %s | Menu Joueur\n", icon, event.getUser().getGlobalName()) +
                    "**Niveau :** `" + player.getLevel() + "`\n" +
                    "**Expérience :** `" + player.getExperience() + " EXP`\n" +
                    "▬▬▬▬▬▬▬▬▬▬\n" +
                    "**Localisation :** `%s` - `%s`\n".formatted(town, place) +
                    "**Région :** `%s`\n".formatted(player.getCurrentLocationRegion());

            event.reply(stringBuilder).queue();
        }
    }
}
