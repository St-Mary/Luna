package com.stmarygate.discord.core.managers;

import java.util.HashMap;
import java.util.Map;

/** Manages cooldowns for user commands. */
public class CooldownManager {
  // Stores user-specific command cooldowns
  private static final HashMap<String, HashMap<String, HashMap<Long, Long>>> cooldowns =
      new HashMap<>();

  /**
   * Adds a cooldown for a user and command.
   *
   * @param user The user for whom to add the cooldown.
   * @param command The command for which to add the cooldown.
   * @param time The time (in milliseconds) for the cooldown.
   */
  public static void addCooldown(String user, String command, Long time) {
    HashMap<String, HashMap<Long, Long>> cooldown = new HashMap<>();
    HashMap<Long, Long> times = new HashMap<>();
    times.put(System.currentTimeMillis(), time);
    cooldown.put(command, times);
    cooldowns.put(user, cooldown);
  }

  /**
   * Checks if a user has a cooldown for a specific command.
   *
   * @param user The user to check for cooldown.
   * @param command The command to check for cooldown.
   * @return True if the user has a cooldown for the command, otherwise False.
   */
  public static boolean hasCooldown(String user, String command) {
    return cooldowns.containsKey(user) && cooldowns.get(user).containsKey(command);
  }

  /**
   * Gets the cooldown details for a user's specific command.
   *
   * @param userId The user for whom to get the cooldown details.
   * @param command The command for which to get the cooldown details.
   * @return A map of cooldown details (timestamps and duration) for the user's command.
   */
  public static Map<Long, Long> getUserCommandCooldown(String userId, String command) {
    return cooldowns.get(userId).get(command);
  }

  /**
   * Gets the remaining cooldown time for a user's specific command.
   *
   * @param userId The user for whom to get the remaining cooldown time.
   * @param command The command for which to get the remaining cooldown time.
   * @return The remaining cooldown time in milliseconds.
   */
  public static Long getRemainingCooldown(String userId, String command) {
    return getUserCommandCooldown(userId, command).entrySet().iterator().next().getValue()
        - (System.currentTimeMillis()
            - getUserCommandCooldown(userId, command).entrySet().iterator().next().getKey());
  }
}
