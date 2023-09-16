package me.aikoo.stmary.core.managers;

import lombok.Getter;

/** The effect manager manages all effects applied to the player */
public class EffectsManager {

  /**
   * Apply an effect to a player (TEST FUNCTION)
   *
   * @param effectStr the effect to apply
   */
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

  /** Get the Effects enum */
  @Getter
  public enum Effects {
    OBJECTGAIN("objectGain"),
    LIFECHANGE("lifeChange");

    private final String effect;

    /**
     * Add a new effect
     *
     * @param effect the new effect to add
     */
    Effects(String effect) {
      this.effect = effect;
    }
  }
}
