package me.aikoo.StMary.system.places.places;

import me.aikoo.StMary.system.places.Places;
import me.aikoo.StMary.system.places.oraland.Talon;

public class PlaceGriffonMarin extends Places {
    public PlaceGriffonMarin(Talon talon) {
        super("Place du Griffon Marin", "Cette place animée est le pouls de Talon, où les étals colorés débordent de coquillages luisants, de perles rares et de trésors exotiques ramenés des contrées lointaines. Les senteurs émanent des stands d'épices et d'herbes, tandis que les voix des marchands se mêlent aux cris des enchères. Des voyageurs venus des horizons les plus lointains se rassemblent ici pour échanger des nouvelles et des histoires de contrées éloignées, transformant cette place en un carrefour vivant des cultures de tout horizon.", talon.getRegion());

        this.setTownPlace(talon);
    }
}
