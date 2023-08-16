package me.aikoo.StMary.commands;

import me.aikoo.StMary.core.StMaryClient;
import me.aikoo.StMary.database.entities.Player;
import me.aikoo.StMary.system.Button;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.hibernate.collection.spi.PersistentSet;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class Start extends AbstractCommand {

    public Start(StMaryClient stMaryClient) {
        super(stMaryClient);

            this.name = "start";
            this.description = ":evergreen_tree: Démarrer l'aventure !";
            this.cooldown = 10000L;
    }

    @Override
    public void execute(StMaryClient client, SlashCommandInteractionEvent event) {
        Player player = client.getDatabaseManager().getPlayer(event.getUser().getIdLong());

        if (player == null) {
            player = new Player();
            player.setLevel(1);
            player.setExperience(BigInteger.valueOf(0));
            player.setMoney(BigInteger.valueOf(0));
            player.setDiscordId(event.getUser().getIdLong());
            player.setTitles(List.of(new String[]{"Néophyte"}));
            player.setCreationTimestamp(new Date());
            player.setCurrentLocationRegion("Oraland");
            player.setCurrentLocationTown("Talon");
            player.setCurrentLocationPlace("Place du Griffon Marin");
            player.setCurrentTitle("Néophyte");

            client.getDatabaseManager().createOrUpdate(player);

            String text = client.getTextManager().getText("start_adventure");
            String title = client.getTextManager().getTitle("start_adventure");
            event.reply(client.getTextManager().generateScene(title, text)).queue();
        } else {
            EmbedBuilder embedBuilder = client.getTextManager().generateErrorEmbed("Démarrage de l'aventure", "Vous ne pouvez pas recommencer l'aventure !");
            event.replyEmbeds(embedBuilder.build()).queue();
        }
    }
}
