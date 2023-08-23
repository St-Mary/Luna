package me.aikoo.StMary.core.managers;

import me.aikoo.StMary.core.abstracts.AbstractCommand;
import me.aikoo.StMary.core.StMaryClient;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Set;

/**
 * Manages commands for the application.
 */
public class CommandManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);
    private final HashMap<String, AbstractCommand> commands = new HashMap<>();
    private final HashMap<String, AbstractCommand> adminCommands = new HashMap<>();

    /**
     * Get a command by its name.
     *
     * @param name The name of the command to retrieve.
     * @return The command with the specified name, or null if not found.
     */
    public AbstractCommand getCommand(String name) {
        return this.commands.get(name);
    }

    /**
     * Get an admin command by its name.
     *
     * @param name The name of the admin command to retrieve.
     * @return The admin command with the specified name, or null if not found.
     */
    public AbstractCommand getAdminCommand(String name) {
        return this.adminCommands.get(name);
    }

    /**
     * Get all registered commands.
     *
     * @return A map of all registered commands.
     */
    public HashMap<String, AbstractCommand> getCommands() {
        return commands;
    }

    /**
     * Get all registered admin commands.
     *
     * @return A map of all registered admin commands.
     */
    public HashMap<String, AbstractCommand> getAdminCommands() {
        return adminCommands;
    }

    /**
     * Load commands into the manager.
     *
     * @param stMaryClient The StMaryClient instance for command initialization.
     */
    public void loadCommands(StMaryClient stMaryClient) {
        // Use Reflections library to scan for classes in the "me.aikoo.StMary.commands" package
        Reflections reflections = new Reflections("me.aikoo.StMary.commands", new org.reflections.scanners.Scanner[0]);

        // Get all classes that are subclasses of AbstractCommand
        Set<Class<? extends AbstractCommand>> classes = reflections.getSubTypesOf(AbstractCommand.class);

        // Iterate through each found command class
        for (Class<? extends AbstractCommand> s : classes) {
            try {
                // Skip abstract command classes
                if (Modifier.isAbstract(s.getModifiers()))
                    continue;

                // Create an instance of the command class using its constructor
                AbstractCommand c = s.getConstructor(StMaryClient.class).newInstance(stMaryClient);

                // Check if the command is not already loaded
                if (!commands.containsKey(c.getName())) {
                    // Log that the command is loaded
                    LOGGER.info("Loaded command '" + c.getName());

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
