package me.aikoo.StMary.core.managers;

import me.aikoo.StMary.core.abstracts.CommandAbstract;
import me.aikoo.StMary.core.bot.StMaryClient;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Manages commands for the application.
 */
public class CommandManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);
    private static final HashMap<String, CommandAbstract> commands = new HashMap<>();
    private static final HashMap<String, CommandAbstract> adminCommands = new HashMap<>();

    /**
     * Get a command by its name.
     *
     * @param name The name of the command to retrieve.
     * @return The command with the specified name, or null if not found.
     */
    public static CommandAbstract getCommand(String name) {
        return commands.get(name);
    }

    /**
     * Get an admin command by its name.
     *
     * @param name The name of the admin command to retrieve.
     * @return The admin command with the specified name, or null if not found.
     */
    public static CommandAbstract getAdminCommand(String name) {
        return adminCommands.get(name);
    }

    /**
     * Get all registered commands.
     *
     * @return A map of all registered commands.
     */
    public static Map<String, CommandAbstract> getCommands() {
        return commands;
    }

    /**
     * Get all registered admin commands.
     *
     * @return A map of all registered admin commands.
     */
    public static Map<String, CommandAbstract> getAdminCommands() {
        return adminCommands;
    }

    /**
     * Load commands into the manager.
     *
     * @param stMaryClient The StMaryClient instance for command initialization.
     */
    public static void loadCommands(StMaryClient stMaryClient) {
        // Use Reflections library to scan for classes in the "me.aikoo.StMary.commands" package
        Reflections reflections = new Reflections("me.aikoo.StMary.commands");

        // Get all classes that are subclasses of AbstractCommand
        Set<Class<? extends CommandAbstract>> classes = reflections.getSubTypesOf(CommandAbstract.class);

        // Iterate through each found command class
        for (Class<? extends CommandAbstract> s : classes) {
            try {
                // Skip abstract command classes
                if (Modifier.isAbstract(s.getModifiers()))
                    continue;

                // Create an instance of the command class using its constructor
                CommandAbstract c = s.getConstructor(StMaryClient.class).newInstance(stMaryClient);

                // Check if the command is not already loaded
                if (!commands.containsKey(c.getName())) {
                    // Log that the command is loaded
                    LOGGER.info("Loaded command: " + c.getName());

                    // Check if the command is an admin command and add it to the appropriate map
                    if (c.isAdminCommand()) {
                        adminCommands.put(c.getName(), c);
                    } else {
                        commands.put(c.getName(), c);
                    }
                }
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                     NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }
}
