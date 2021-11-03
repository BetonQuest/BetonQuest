package org.betonquest.betonquest.api;

import lombok.CustomLog;
import org.betonquest.betonquest.config.ConfigPackage;
import org.betonquest.betonquest.modules.logger.BetonQuestLoggerImpl;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;

import java.util.logging.Level;

/**
 * This is the BetonQuest log facade for manual usage or Lombok's {@link CustomLog} annotation.
 * <p>
 * This facade uses the {@link PluginLogger} from the {@link Plugin#getLogger()} method.
 * It registers a new child logger for each class it's used in.
 * <p>
 * Therefore, all of BetonQuest's logging needs to be done with the methods of this class.
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface BetonQuestLogger {
    /**
     * Creates a logger.
     * <p>
     * Use this method to create a logger without a topic.
     * <p>
     * This method is also used by Lombok.
     *
     * @param clazz The class to create a logger for.
     * @return A {@link BetonQuestLogger} implementation.
     */
    static BetonQuestLogger create(final Class<?> clazz) {
        return create(clazz, null);
    }

    /**
     * Creates a logger.
     * <p>
     * Use this method to create a logger with a topic.
     * <p>
     * This method is used by Lombok.
     *
     * @param clazz The class to create a logger for.
     * @param topic The optional topic of the logger.
     * @return A {@link BetonQuestLogger} implementation.
     */
    @SuppressWarnings("PMD.UseProperClassLoader")
    static BetonQuestLogger create(final Class<?> clazz, final String topic) {
        for (final Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin.getClass().getClassLoader().equals(clazz.getClassLoader())) {
                return new BetonQuestLoggerImpl(plugin, plugin.getLogger(), clazz, topic);
            }
        }
        return new BetonQuestLoggerImpl(null, Bukkit.getLogger(), clazz, topic);
    }

    /**
     * Creates a logger.
     * <p>
     * Use this method to create a logger for the {@link Plugin} class without a topic.
     * For other classes use the {@link BetonQuestLogger#create(Class)}
     * or {@link BetonQuestLogger#create(Class, String)} method.
     *
     * @param plugin The plugin which is used for logging.
     * @return A {@link BetonQuestLogger} implementation.
     */
    static BetonQuestLogger create(final Plugin plugin) {
        return create(plugin, null);
    }

    /**
     * Creates a logger.
     * <p>
     * Use this method to create a logger for the {@link Plugin} class without a topic.
     * For other classes use the {@link BetonQuestLogger#create(Class)}
     * or {@link BetonQuestLogger#create(Class, String)} method.
     *
     * @param plugin The plugin which is used for logging.
     * @param topic  The optional topic of the logger.
     * @return A {@link BetonQuestLogger} implementation.
     */
    static BetonQuestLogger create(final Plugin plugin, final String topic) {
        return new BetonQuestLoggerImpl(plugin, plugin.getLogger(), plugin.getClass(), topic);
    }

    /**
     * Logs a normal message with the {@link Level#INFO} level to the log.
     * <p>
     * Use this for normal log information.
     * <p>
     * Don't use this method, if you can provide a {@link ConfigPackage}.
     * Use {@link BetonQuestLogger#info(ConfigPackage, String)} instead.
     *
     * @param msg The message to log.
     */
    void info(String msg);

    /**
     * Logs a normal message with the {@link Level#INFO} level to the log.
     * <p>
     * Use this for normal log information.
     *
     * @param pack The related {@link ConfigPackage} or null.
     * @param msg  The message to log.
     */
    void info(ConfigPackage pack, String msg);

    /**
     * Logs a warning message with the {@link Level#WARNING} level to the log.
     * <p>
     * Use this if you can provide useful information how to fix the underlying problem.
     * <p>
     * If you can provide an exception use {@link BetonQuestLogger#warning(String, Throwable)} instead.
     * <p>
     * Don't use this method, if you can provide a {@link ConfigPackage}.
     * Use {@link BetonQuestLogger#warning(ConfigPackage, String)} instead.
     *
     * @param msg The message to log.
     */
    void warning(String msg);

    /**
     * Logs a warning message with the {@link Level#WARNING} level to the log.
     * <p>
     * Use this if you can provide useful information how to fix the underlying problem.
     * <p>
     * If you can provide an exception use {@link BetonQuestLogger#warning(ConfigPackage, String, Throwable)} instead.
     *
     * @param pack The related {@link ConfigPackage} or null.
     * @param msg  The message to log.
     */
    void warning(ConfigPackage pack, String msg);

    /**
     * Logs a warning message with the {@link Level#WARNING} level to the log.
     * The {@link Throwable} is logged with the {@link Level#FINE} level to the log.
     * <p>
     * Use this if you can provide useful information how to fix the underlying problem.
     * <p>
     * If you cannot provide an exception use {@link BetonQuestLogger#warning(String)} instead.
     * <p>
     * Don't use this method, if you can provide a {@link ConfigPackage}.
     * Use {@link BetonQuestLogger#warning(ConfigPackage, String, Throwable)} instead.
     *
     * @param msg    The message to log.
     * @param thrown The throwable to log.
     */
    void warning(String msg, Throwable thrown);

    /**
     * Logs a warning message with the {@link Level#WARNING} level to the log.
     * The {@link Throwable} is logged with the {@link Level#FINE} level to the log.
     * <p>
     * Use this if you can provide useful information how to fix the underlying problem.
     * <p>
     * If you cannot provide an exception use {@link BetonQuestLogger#warning(ConfigPackage, String)} instead.
     *
     * @param pack   The related {@link ConfigPackage} or null.
     * @param msg    The message to log.
     * @param thrown The throwable to log.
     */
    void warning(ConfigPackage pack, String msg, Throwable thrown);

    /**
     * Logs an error message with the {@link Level#SEVERE} level to the log.
     * <p>
     * Use this if the underlying problem affects the servers security or functionality.
     * Usage is also allowed if you don't know how the user can fix the underlying problem.
     * <p>
     * If you can provide an exception use {@link BetonQuestLogger#error(String, Throwable)} instead.
     * <p>
     * Don't use this method, if you can provide a {@link ConfigPackage}.
     * Use {@link BetonQuestLogger#error(ConfigPackage, String)} instead.
     *
     * @param msg The message to log.
     */
    void error(String msg);

    /**
     * Logs an error message with the {@link Level#SEVERE} level to the log.
     * <p>
     * Use this if the underlying problem affects the servers security or functionality.
     * Usage is also allowed if you don't know how the user can fix the underlying problem.
     * <p>
     * If you can provide an exception use {@link BetonQuestLogger#error(ConfigPackage, String, Throwable)} instead.
     *
     * @param pack The related {@link ConfigPackage} or null.
     * @param msg  The message to log.
     */
    void error(ConfigPackage pack, String msg);

    /**
     * Logs an error message with the {@link Level#SEVERE} level to the log.
     * The {@link Throwable} is logged together with the message.
     * <p>
     * Use this if the underlying problem affects the servers security or functionality.
     * Usage is also allowed if you don't know how the user can fix the underlying problem.
     * <p>
     * If you cannot provide an exception use {@link BetonQuestLogger#error(String)} instead.
     * <p>
     * Don't use this method, if you can provide a {@link ConfigPackage}.
     * Use {@link BetonQuestLogger#error(ConfigPackage, String, Throwable)} instead.
     *
     * @param msg    The message to log.
     * @param thrown The throwable to log.
     */
    void error(String msg, Throwable thrown);

    /**
     * Logs an error message with the {@link Level#SEVERE} level to the log.
     * The {@link Throwable} is logged together with the message.
     * <p>
     * Use this if the underlying problem affects the servers security or functionality.
     * Usage is also allowed if you don't know how the user can fix the underlying problem.
     * <p>
     * If you cannot provide an exception use {@link BetonQuestLogger#error(ConfigPackage, String)} instead.
     *
     * @param pack   The related {@link ConfigPackage} or null.
     * @param msg    The message to log.
     * @param thrown The throwable to log.
     */
    void error(ConfigPackage pack, String msg, Throwable thrown);

    /**
     * Logs a debug message with the {@link Level#FINE} level to the log.
     * <p>
     * Use this for additional debug log information.
     * <p>
     * If you can provide an exception use {@link BetonQuestLogger#debug(String, Throwable)} instead.
     * <p>
     * Don't use this method, if you can provide a {@link ConfigPackage}.
     * Use {@link BetonQuestLogger#debug(ConfigPackage, String)} instead.
     *
     * @param msg The message to log.
     */
    void debug(String msg);

    /**
     * Logs a debug message with the {@link Level#FINE} level to the log.
     * <p>
     * Use this for additional debug log information.
     * <p>
     * If you can provide an exception use {@link BetonQuestLogger#debug(ConfigPackage, String, Throwable)} instead.
     *
     * @param pack The related {@link ConfigPackage} or null.
     * @param msg  The message to log.
     */
    void debug(ConfigPackage pack, String msg);

    /**
     * Logs a debug message with the {@link Level#FINE} level to the log.
     * The {@link Throwable} is logged together with the message.
     * <p>
     * Use this for additional debug log information.
     * <p>
     * If you cannot provide an exception use {@link BetonQuestLogger#debug(String)} instead.
     * <p>
     * Don't use this method, if you can provide a {@link ConfigPackage}.
     * Use {@link BetonQuestLogger#debug(ConfigPackage, String, Throwable)} instead.
     *
     * @param msg    The message to log.
     * @param thrown The throwable to log.
     */
    void debug(String msg, Throwable thrown);

    /**
     * Logs a debug message with the {@link Level#FINE} level to the log.
     * The {@link Throwable} is logged together with the message.
     * <p>
     * Use this for additional debug log information.
     * <p>
     * If you cannot provide an exception use {@link BetonQuestLogger#debug(ConfigPackage, String)} instead.
     *
     * @param pack   The related {@link ConfigPackage} or null.
     * @param msg    The message to log.
     * @param thrown The throwable to log.
     */
    void debug(ConfigPackage pack, String msg, Throwable thrown);

    /**
     * Logs a {@link Throwable} with the {@link Level#SEVERE} level to the log.
     * The Throwable is logged together with a message that informs the user that the error
     * needs to be reported to the issue tracker.
     * <p>
     * Only use this in cases that should never occur and indicate an error that must be reported.
     * <p>
     * Don't use this method, if you can provide a {@link ConfigPackage}.
     * Use {@link BetonQuestLogger#reportException(ConfigPackage, Throwable)} instead.
     *
     * @param thrown The throwable to log.
     */
    void reportException(Throwable thrown);

    /**
     * Logs a {@link Throwable} with the {@link Level#SEVERE} level to the log.
     * The Throwable is logged together with a message that informs the user that the error
     * needs to be reported to the issue tracker.
     * <p>
     * Only use this in cases that should never occur and indicate an error that must be reported.
     *
     * @param pack   The related {@link ConfigPackage} or null.
     * @param thrown The throwable to log.
     */
    void reportException(ConfigPackage pack, Throwable thrown);
}
