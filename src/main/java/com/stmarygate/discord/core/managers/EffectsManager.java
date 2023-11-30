package com.stmarygate.discord.core.managers;

import lombok.Getter;

public class EffectsManager {

  public static void playEffect(String effectStr) {
    Effects effect = Effects.valueOf(effectStr.toUpperCase());

    switch (effect) {
      case OBJECTGAIN -> {
        System.out.println("Object gain");
      }
      case LIFECHANGE -> {
        System.out.println("Life change");
      }
      default -> {
        break;
      }
    }
  }

  @Getter
  public enum Effects {
    OBJECTGAIN("objectGain"),
    LIFECHANGE("lifeChange");

    private final String effect;

    Effects(String effect) {
      this.effect = effect;
    }
  }
}
