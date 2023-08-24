package me.aikoo.StMary.core.constants;

import java.math.BigInteger;
import java.util.List;

public class PlayerConstant {
    public static final Integer CREATION_TIME_WEEK_LIMIT = 1; // The number of weeks that a Discord account must be old to be able to create a character.
    public static final Integer LEVEL = 1;
    public static final BigInteger EXPERIENCE = BigInteger.valueOf(0);
    public static final BigInteger MONEY = BigInteger.valueOf(0);
    public static final List<String> TITLES = List.of(new String[]{"Néophyte"});
    public static final String CURRENT_LOCATION_REGION = "oraland";
    public static final String CURRENT_LOCATION_TOWN = "talon";
    public static final String CURRENT_LOCATION_PLACE = "talon_02";
    public static final String CURRENT_TITLE = "Néophyte";
    public static final String MAGICAL_BOOK = "book_I";
}
