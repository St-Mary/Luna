package me.aikoo.StMary.core;

import java.util.HashMap;

public class CooldownManager {
    private final HashMap<String, HashMap<String, HashMap<Long, Long>>> cooldowns = new HashMap<>();

    public void addCooldown(String user, String command, Long time) {
        HashMap<String, HashMap<Long, Long>> cooldown = new HashMap<>();
        HashMap<Long, Long> times = new HashMap<>();
        times.put(System.currentTimeMillis(), time);
        cooldown.put(command, times);
        cooldowns.put(user, cooldown);
    }

    public boolean hasCooldown(String user, String command) {
        return cooldowns.containsKey(user) && cooldowns.get(user).containsKey(command);
    }

    public HashMap<Long, Long> getUserCommandCooldown(String userId, String command) {
        return cooldowns.get(userId).get(command);
    }

    public Long getRemainingCooldown(String userId, String command) {
        return getUserCommandCooldown(userId, command).entrySet().iterator().next().getValue() - (System.currentTimeMillis() - getUserCommandCooldown(userId, command).entrySet().iterator().next().getKey());
    }
}
