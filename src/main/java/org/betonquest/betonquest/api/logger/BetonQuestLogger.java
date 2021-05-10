package org.betonquest.betonquest.api.logger;

import lombok.CustomLog;
import org.betonquest.betonquest.config.ConfigPackage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the BetonQuest log facade for usage with Lombok {@link CustomLog} annotation.
 * <p>
 * This decorator uses the {@link PluginLogger} from the {@link JavaPlugin#getLogger()} method.
 * It registers a new child {@link TopicLogger} for each class it's used in.
 * <p>
 * Therefore, all of BetonQuest's logging needs to be done with the methods of this class.
 */
public class BetonQuestLogger {

    /**
     * The original logger.
     */
    private final Logger logger;

    /**
     * Creates a decorator for the {@link TopicLogger}.
     *
     * @param parentLogger The instance of the parent {@link Logger}.
     * @param clazz        The calling class.
     * @param topic        The topic of the logger.
     */
    public BetonQuestLogger(final Logger parentLogger, final Class<?> clazz, final String topic) {
        this.logger = new TopicLogger(parentLogger, clazz, topic);
    }

    /**
     * Creates a logger.<p>
     * This method should only be called with lombok using the @{@link CustomLog} annotation.
     *
     * @param clazz The class that is passed by lombok.
     * @return The decorated Logger.
     */
    public static BetonQuestLogger create(final Class<?> clazz) {
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
    public static BetonQuestLogger create(final Class<?> clazz, final String topic) {
        for (final Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin.getClass().getClassLoader().equals(clazz.getClassLoader())) {
                return new BetonQuestLogger(plugin.getLogger(), clazz, topic);
            }
        }
        return new BetonQuestLogger(Bukkit.getLogger(), clazz, topic);
    }

    /**
     * Logs a normal message with the {@link Level#INFO} level to the log.
     * <p>
     * Use this for normal log information.
     *
     * @param pack The related {@link ConfigPackage} or null.
     * @param msg  The message to log.
     */
    public void info(final ConfigPackage pack, final String msg) {
        final QuestPackageLogRecord record = new QuestPackageLogRecord(pack, Level.INFO, msg);
        logger.log(record);
    }

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
    public void warning(final ConfigPackage pack, final String msg) {
        final QuestPackageLogRecord record = new QuestPackageLogRecord(pack, Level.WARNING, msg);
        logger.log(record);
    }

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
    public void warning(final ConfigPackage pack, final String msg, final Throwable thrown) {
        final QuestPackageLogRecord record = new QuestPackageLogRecord(pack, Level.WARNING, msg);
        logger.log(record);

        final QuestPackageLogRecord recordThrowable = new QuestPackageLogRecord(pack, Level.FINE, "Additional stacktrace:");
        recordThrowable.setThrown(thrown);
        logger.log(recordThrowable);
    }

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
    public void error(final ConfigPackage pack, final String msg) {
        final QuestPackageLogRecord record = new QuestPackageLogRecord(pack, Level.SEVERE, msg);
        logger.log(record);
    }

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
    public void error(final ConfigPackage pack, final String msg, final Throwable thrown) {
        final QuestPackageLogRecord record = new QuestPackageLogRecord(pack, Level.SEVERE, msg);
        record.setThrown(thrown);
        logger.log(record);
    }

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
    public void debug(final ConfigPackage pack, final String msg) {
        final QuestPackageLogRecord record = new QuestPackageLogRecord(pack, Level.FINE, msg);
        logger.log(record);
    }

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
    public void debug(final ConfigPackage pack, final String msg, final Throwable thrown) {
        final QuestPackageLogRecord record = new QuestPackageLogRecord(pack, Level.FINE, msg);
        record.setThrown(thrown);
        logger.log(record);
    }

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
    public void reportException(final ConfigPackage pack, final Throwable thrown) {
        final String msg = "This is an exception that should never occur. "
                + "If you don't know why this occurs please report it to the author.";
        final QuestPackageLogRecord record = new QuestPackageLogRecord(pack, Level.SEVERE, msg);
        record.setThrown(thrown);
        logger.log(record);
    }
}
