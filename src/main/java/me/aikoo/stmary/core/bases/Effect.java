package me.aikoo.stmary.core.bases;

import me.aikoo.stmary.core.managers.EffectsManager;

public class Effect {
  private final EffectsManager.Effects effect;
  private final Object value;

  public Effect(String effect, Object value) {
    this.effect = EffectsManager.Effects.valueOf(effect.toUpperCase());
    this.value = value;
  }

  public EffectsManager.Effects getEffect() {
    return effect;
  }

  public Object getValue() {
    return value;
  }
}
