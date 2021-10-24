package org.betonquest.betonquest.api;

import lombok.CustomLog;
import org.betonquest.betonquest.config.ConfigPackage;
import org.betonquest.betonquest.utils.logger.BetonQuestLoggerImpl;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * This is the BetonQuest log facade for usage with Lombok {@link CustomLog} annotation.
 * <p>
 * This decorator uses the {@link PluginLogger} from the {@link JavaPlugin#getLogger()} method.
 * It registers a new child logger for each class it's used in.
 * <p>
 * Therefore, all of BetonQuest's logging needs to be done with the methods of this class.
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface BetonQuestLogger {
    /**
     * Creates a logger.<p>
     * This method should only be called with lombok using the @{@link CustomLog} annotation.
     *
     * @param clazz The class that is passed by lombok.
     * @return The decorated Logger.
     */
    static BetonQuestLoggerImpl create(final Class<?> clazz) {
        return create(clazz, null);
    }

    /**
     * Creates a logger.<p>
     * This method should only be called with lombok using the @{@link CustomLog} annotation.
     *
     * @param clazz The class that is passed by lombok.
     * @param topic The optional topic of the logger passed by lombok.
     * @return The decorated Logger.
     */
    @SuppressWarnings("PMD.UseProperClassLoader")
    static BetonQuestLoggerImpl create(final Class<?> clazz, final String topic) {
        for (final Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin.getClass().getClassLoader().equals(clazz.getClassLoader())) {
                return new BetonQuestLoggerImpl(plugin, plugin.getLogger(), clazz, topic);
            }
        }
        return new BetonQuestLoggerImpl(null, Bukkit.getLogger(), clazz, topic);
    }

    /**
     * Logs a normal message with the {@link Level#INFO} level to the log.
     * <p>
     * Use this for normal log information.
     * <p>
     * Don't use this method, if you can provide a {@link ConfigPackage}.
     * Use {@link BetonQuestLoggerImpl#info(ConfigPackage, String)} instead.
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
     * If you can provide an exception use {@link BetonQuestLoggerImpl#warning(String, Throwable)} instead.
     * <p>
     * Don't use this method, if you can provide a {@link ConfigPackage}.
     * Use {@link BetonQuestLoggerImpl#warning(ConfigPackage, String)} instead.
     *
     * @param msg The message to log.
     */
    void warning(String msg);

    /**
     * Logs a warning message with the {@link Level#WARNING} level to the log.
     * <p>
     * Use this if you can provide useful information how to fix the underlying problem.
     * <p>
     * If you can provide an exception use {@link BetonQuestLoggerImpl#warning(ConfigPackage, String, Throwable)} instead.
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
     * If you cannot provide an exception use {@link BetonQuestLoggerImpl#warning(String)} instead.
     * <p>
     * Don't use this method, if you can provide a {@link ConfigPackage}.
     * Use {@link BetonQuestLoggerImpl#warning(ConfigPackage, String, Throwable)} instead.
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
     * If you cannot provide an exception use {@link BetonQuestLoggerImpl#warning(ConfigPackage, String)} instead.
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
     * If you can provide an exception use {@link BetonQuestLoggerImpl#error(String, Throwable)} instead.
     * <p>
     * Don't use this method, if you can provide a {@link ConfigPackage}.
     * Use {@link BetonQuestLoggerImpl#error(ConfigPackage, String)} instead.
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
     * If you can provide an exception use {@link BetonQuestLoggerImpl#error(ConfigPackage, String, Throwable)} instead.
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
     * If you cannot provide an exception use {@link BetonQuestLoggerImpl#error(String)} instead.
     * <p>
     * Don't use this method, if you can provide a {@link ConfigPackage}.
     * Use {@link BetonQuestLoggerImpl#error(ConfigPackage, String, Throwable)} instead.
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
     * If you cannot provide an exception use {@link BetonQuestLoggerImpl#error(ConfigPackage, String)} instead.
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
     * If you can provide an exception use {@link BetonQuestLoggerImpl#debug(String, Throwable)} instead.
     * <p>
     * Don't use this method, if you can provide a {@link ConfigPackage}.
     * Use {@link BetonQuestLoggerImpl#debug(ConfigPackage, String)} instead.
     *
     * @param msg The message to log.
     */
    void debug(String msg);

    /**
     * Logs a debug message with the {@link Level#FINE} level to the log.
     * <p>
     * Use this for additional debug log information.
     * <p>
     * If you can provide an exception use {@link BetonQuestLoggerImpl#debug(ConfigPackage, String, Throwable)} instead.
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
     * If you cannot provide an exception use {@link BetonQuestLoggerImpl#debug(String)} instead.
     * <p>
     * Don't use this method, if you can provide a {@link ConfigPackage}.
     * Use {@link BetonQuestLoggerImpl#debug(ConfigPackage, String, Throwable)} instead.
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
     * If you cannot provide an exception use {@link BetonQuestLoggerImpl#debug(ConfigPackage, String)} instead.
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
     * Use {@link BetonQuestLoggerImpl#reportException(ConfigPackage, Throwable)} instead.
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
