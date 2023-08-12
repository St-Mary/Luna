package me.aikoo.StMary.system.places.oraland;

import me.aikoo.StMary.system.places.Region;
import me.aikoo.StMary.system.places.Town;
import me.aikoo.StMary.system.places.places.PlaceGriffonMarin;

public class Talon extends Town {

    public Talon(Region region) {
        super("Talon", "Tel un joyau en bordure de l'horizon maritime, Talon, capitale de la région d'Oraland, s'étend fièrement à l'embouchure majestueuse de la rivière Lucca, où ses rues pavées rencontrent le sable doré des rivages de la mer Naia. Baignée dans la richesse des eaux, la ville respire au rythme des marées, son identité gravée par ses pêcheurs et marins intrépides.\n" +
                "\n" +
                "Les terres environnantes abritent des champs verdoyants et des fermes fertiles qui fournissent la cité en vivres et ressources. La brise marine, mélange subtil d'embruns et de fleurs des champs, danse dans les ruelles animées tandis que les bateaux colorés amarrés au port dévoilent les voyages tumultueux qui ont forgé le caractère des habitants.\n" +
                "\n" +
                "Le seigneur Gyr Talon, maître de la ville et chef de la prestigieuse famille Talon, veille sur Talon avec autorité et bienveillance. La renommée de la famille réside dans sa longue histoire de marins et de commerçants prospères, une histoire qui s'entremêle avec celle de la ville.\n" +
                "\n" +
                "Talon, aux confins des terres et des mers, est une cité où les liens entre la mer et la terre sont tissés dans chaque pierre et chaque vague. Les joueurs seront invités à explorer son histoire complexe et à naviguer entre les courants de richesse et de rivalités qui l'animent.", region);

        this.getPlaces().add(new PlaceGriffonMarin(this));
    }
}
