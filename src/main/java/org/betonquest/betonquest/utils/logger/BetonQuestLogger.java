package org.betonquest.betonquest.utils.logger;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.config.ConfigPackage;
import org.betonquest.betonquest.utils.logger.custom.PackageLogRecord;
import org.betonquest.betonquest.utils.logger.custom.TopicLogger;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the BetonQuest log decorator for usage with Lombok's {@link lombok.CustomLog} annotation.
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
     * @param plugin The instance of a {@link JavaPlugin}.
     * @param clazz  The calling class.
     * @param topic  The topic of the logger.
     */
    public BetonQuestLogger(final JavaPlugin plugin, final Class<?> clazz, final String topic) {
        this.logger = new TopicLogger(plugin, clazz, topic);
    }

    /**
     * Creates a logger.<p>
     * This method should only be called with lombok using the @{@link lombok.CustomLog} annotation.
     *
     * @param clazz The class that is passed by lombok.
     * @return The decorated Logger.
     */
    public static BetonQuestLogger create(final Class<?> clazz) {
        return create(clazz, null);
    }

    /**
     * Creates a logger.<p>
     * This method should only be called with lombok using the @{@link lombok.CustomLog} annotation.
     *
     * @param clazz The class that is passed by lombok.
     * @param topic The optional topic of the logger passed by lombok.
     * @return The decorated Logger.
     */
    public static BetonQuestLogger create(final Class<?> clazz, final String topic) {
        return new BetonQuestLogger(BetonQuest.getInstance(), clazz, topic);
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
        final PackageLogRecord record = new PackageLogRecord(pack, Level.INFO, msg);
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
        final PackageLogRecord record = new PackageLogRecord(pack, Level.WARNING, msg);
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
        final PackageLogRecord record = new PackageLogRecord(pack, Level.WARNING, msg);
        logger.log(record);

        final PackageLogRecord recordThrowable = new PackageLogRecord(pack, Level.FINE,
                "Additional stacktrace:");
        final int stackSize = thrown.getStackTrace().length;
        final StackTraceElement element = thrown.getStackTrace()[stackSize - 1];
        recordThrowable.setSourceClassName(element.getClassName());
        recordThrowable.setSourceMethodName(element.getMethodName());
        recordThrowable.setThrown(thrown);
        logger.log(recordThrowable);
    }

    /**
     * Logs a error message with the {@link Level#SEVERE} level to the log.
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
        final PackageLogRecord record = new PackageLogRecord(pack, Level.SEVERE, msg);
        logger.log(record);
    }

    /**
     * Logs a error message with the {@link Level#SEVERE} level to the log.
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
        final PackageLogRecord record = new PackageLogRecord(pack, Level.SEVERE, msg);
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
        final PackageLogRecord record = new PackageLogRecord(pack, Level.FINE, msg);
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
        final PackageLogRecord record = new PackageLogRecord(pack, Level.FINE, msg);
        record.setThrown(thrown);
        logger.log(record);
    }

    /**
     * The {@link Throwable} is logged using the {@link Level#SEVERE} level
     * <p>
     * and a message that informs the user that the error needs to be reported to the issue tracker.
     * Only use this in cases that should never occur and indicate an error that must be reported.
     *
     * @param pack   The related {@link ConfigPackage} or null.
     * @param thrown The throwable to log.
     */
    public void reportException(final ConfigPackage pack, final Throwable thrown) {
        final String msg = "This is an exception that should never occur. "
                + "If you don't know why this occurs please report it to <https://github.com/BetonQuest/BetonQuest/issues>.";
        final PackageLogRecord record = new PackageLogRecord(pack, Level.SEVERE, msg);
        record.setThrown(thrown);
        logger.log(record);
    }
}
