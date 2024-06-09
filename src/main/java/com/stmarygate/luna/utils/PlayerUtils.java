package com.stmarygate.luna.utils;

import com.stmarygate.coral.constants.PlayerConstants;
import com.stmarygate.coral.entities.Player;

public class PlayerUtils {
  public static Player generateNewPlayer() {
    Player player = new Player();
    player.setExp(PlayerConstants.INITIAL_EXP);
    player.setLevel(PlayerConstants.INITIAL_LEVEL);
    player.setMana(PlayerConstants.INITIAL_MANA);
    player.setAura(PlayerConstants.INITIAL_AURA);
    player.setStrength(PlayerConstants.INITIAL_STRENGTH);
    player.setDefense(PlayerConstants.INITIAL_DEFENSE);
    player.setSpeed(PlayerConstants.INITIAL_SPEED);
    player.setHealth(PlayerConstants.INITIAL_HEALTH);
    player.setMaxHealth(PlayerConstants.INITIAL_MAX_HEALTH);
    player.setExpToNextLevel(PlayerConstants.INITIAL_MAX_EXP);
    player.setStamina(PlayerConstants.INITIAL_STAMINA);

    return player;
  }
}
