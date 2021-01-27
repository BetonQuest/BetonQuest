package org.betonquest.betonquest.utils.logger;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.config.ConfigPackage;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the BetonQuest log decorator for usage with Lombok's {@link lombok.CustomLog} annotation.
 * <p>
 * This decorator uses the {@link PluginLogger} from the {@link JavaPlugin#getLogger()} method
 * to log messages with an optional topic to the log.
 * <p>
 * Therefore, all of BetonQuest's logging needs to be done with the methods of this class.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class BetonQuestLogger {
    /**
     * The original logger.
     */
    private final Logger logger;
    /**
     * The topic of the logger.
     */
    private final String topic;

    /**
     * Create a decorator for the {@link JavaPlugin#getLogger()}.
     *
     * @param plugin The instance of a {@link JavaPlugin}.
     * @param topic  The topic of the logger.
     */
    public BetonQuestLogger(final JavaPlugin plugin, final String topic) {
        this.logger = plugin.getLogger();
        this.topic = topic == null ? "" : "(" + topic + ") ";
    }

    /**
     * Creates a logger.<p>
     * This method should only be called by lombok with the @{@link lombok.CustomLog} annotation.
     *
     * @param clazz The clazz that is passed by lombok.
     * @return The decorated Logger.
     */
    public static BetonQuestLogger create(final Class<?> clazz) {
        return create(clazz, null);
    }

    /**
     * Creates a logger.<p>
     * This method should only be called by lombok with the @{@link lombok.CustomLog} annotation.
     *
     * @param clazz The clazz that is passed by lombok.
     * @param topic The optional topic of the logger passed by lombok.
     * @return The decorated Logger.
     */
    public static BetonQuestLogger create(final Class<?> clazz, final String topic) {
        return new BetonQuestLogger(BetonQuest.getInstance(), topic);
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
        final BetonQuestLogRecord record = new BetonQuestLogRecord(resolvePack(pack), Level.INFO, topic + msg);
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
        final BetonQuestLogRecord record = new BetonQuestLogRecord(resolvePack(pack), Level.WARNING, topic + msg);
        logger.log(record);
    }

    /**
     * Logs a warning message with the {@link Level#WARNING} level to the log.
     * The {@link Throwable} is logged with the {@link Level#FINER} level to the log.
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
        final BetonQuestLogRecord record = new BetonQuestLogRecord(resolvePack(pack), Level.WARNING, topic + msg);
        logger.log(record);

        final BetonQuestLogRecord recordThrowable = new BetonQuestLogRecord(resolvePack(pack), Level.FINE,
                topic + "Additional stacktrace:");
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
        final BetonQuestLogRecord record = new BetonQuestLogRecord(resolvePack(pack), Level.SEVERE, topic + msg);
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
        final BetonQuestLogRecord record = new BetonQuestLogRecord(resolvePack(pack), Level.SEVERE, topic + msg);
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
        final BetonQuestLogRecord record = new BetonQuestLogRecord(resolvePack(pack), Level.FINE, topic + msg);
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
        final BetonQuestLogRecord record = new BetonQuestLogRecord(resolvePack(pack), Level.FINE, topic + msg);
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
        final BetonQuestLogRecord record = new BetonQuestLogRecord(resolvePack(pack), Level.SEVERE, topic + msg);
        record.setThrown(thrown);
        logger.log(record);
    }

    private String resolvePack(final ConfigPackage pack) {
        return pack == null ? null : pack.getName();
    }
}
