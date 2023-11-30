package com.stmarygate.discord.core.managers;

import com.stmarygate.discord.core.abstracts.CommandAbstract;
import com.stmarygate.discord.core.bot.StMaryClient;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Set;
import lombok.Getter;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Manages commands for the application. */
public class CommandManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);
  @Getter private static final HashMap<String, CommandAbstract> commands = new HashMap<>();
  @Getter private static final HashMap<String, CommandAbstract> adminCommands = new HashMap<>();

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
   * Load commands into the manager.
   *
   * @param stMaryClient The StMaryClient instance for command initialization.
   */
  public static void loadCommands(StMaryClient stMaryClient) {
    Reflections reflections = new Reflections("com.stmarygate.discord.commands");

    Set<Class<? extends CommandAbstract>> commandClasses =
        reflections.getSubTypesOf(CommandAbstract.class);

    for (Class<? extends CommandAbstract> commandClass : commandClasses) {
      if (isAbstractClass(commandClass)) {
        continue;
      }

      try {
        CommandAbstract command = instantiateCommand(stMaryClient, commandClass);

        if (!isCommandLoaded(command)) {
          logLoadedCommand(command);
          addCommandToAppropriateMap(command);
        }
      } catch (Exception e) {
        logErrorLoadingCommand(e);
      }
    }
  }

  /**
   * Check if a class is abstract.
   *
   * @param commandClass The class to check.
   * @return True if the class is abstract, false otherwise.
   */
  private static boolean isAbstractClass(Class<?> commandClass) {
    return Modifier.isAbstract(commandClass.getModifiers());
  }

  /**
   * Instantiate a command.
   *
   * @param stMaryClient The StMaryClient instance for command initialization.
   * @param commandClass The class of the command to instantiate.
   * @return The instantiated command.
   * @throws InstantiationException If the command cannot be instantiated.
   * @throws IllegalAccessException If the command cannot be accessed.
   * @throws NoSuchMethodException If the command does not have a constructor.
   * @throws InvocationTargetException If the command cannot be invoked.
   */
  private static CommandAbstract instantiateCommand(
      StMaryClient stMaryClient, Class<? extends CommandAbstract> commandClass)
      throws InstantiationException,
          IllegalAccessException,
          NoSuchMethodException,
          InvocationTargetException {
    return commandClass.getConstructor(StMaryClient.class).newInstance(stMaryClient);
  }

  /**
   * Check if a command is loaded.
   *
   * @param command The command to check.
   * @return True if the command is loaded, false otherwise.
   */
  private static boolean isCommandLoaded(CommandAbstract command) {
    return commands.containsKey(command.getName());
  }

  /**
   * Log that a command has been loaded.
   *
   * @param command The command that has been loaded.
   */
  private static void logLoadedCommand(CommandAbstract command) {
    LOGGER.info("Loaded command: " + command.getName());
  }

  /**
   * Add a command to the appropriate map.
   *
   * @param command The command to add.
   */
  private static void addCommandToAppropriateMap(CommandAbstract command) {
    if (command.isAdminCommand()) {
      adminCommands.put(command.getName(), command);
    } else {
      commands.put(command.getName(), command);
    }
  }

  /**
   * Log an error that occurred while loading a command.
   *
   * @param e The exception that occurred.
   */
  private static void logErrorLoadingCommand(Exception e) {
    LOGGER.error("An error occurred while loading commands: ", e);
  }
}
