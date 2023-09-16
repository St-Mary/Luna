package me.aikoo.stmary.core.bases;

import lombok.Getter;
import me.aikoo.stmary.core.managers.EffectsManager;

/** The Effect cclass, which reprensent an effect appliied to a player */
@Getter
public class Effect {
  private final EffectsManager.Effects effect;
  private final Object value;

  /**
   * Create a new effet
   *
   * @param effect The effect name
   * @param value The effect value
   */
  public Effect(String effect, Object value) {
    this.effect = EffectsManager.Effects.valueOf(effect.toUpperCase());
    this.value = value;
  }
}
