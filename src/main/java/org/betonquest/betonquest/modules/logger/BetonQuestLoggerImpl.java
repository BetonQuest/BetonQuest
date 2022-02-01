package org.betonquest.betonquest.modules.logger;

import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.api.config.QuestPackage;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the implementation of the interface {@link BetonQuestLogger}.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class BetonQuestLoggerImpl implements BetonQuestLogger {

    /**
     * The {@link Plugin} this logger belongs to.
     */
    private final Plugin plugin;

    /**
     * The original logger.
     */
    private final Logger logger;

    /**
     * Creates a decorator for the {@link TopicLogger}.
     *
     * @param plugin The {@link Plugin} this logger belongs to.
     * @param clazz  The calling class.
     * @param topic  The topic of the logger.
     */
    public BetonQuestLoggerImpl(final Plugin plugin, final Logger parentLogger, final Class<?> clazz, final String topic) {
        this.plugin = plugin;
        this.logger = new TopicLogger(parentLogger, clazz, topic);
    }

    @Override
    public void info(final String msg) {
        info(null, msg);
    }

    @Override
    public void info(final QuestPackage pack, final String msg) {
        final BetonQuestLogRecord record = new BetonQuestLogRecord(plugin, pack, Level.INFO, msg);
        logger.log(record);
    }

    @Override
    public void warn(final String msg) {
        warn(null, msg);
    }

    @Override
    public void warn(final QuestPackage pack, final String msg) {
        final BetonQuestLogRecord record = new BetonQuestLogRecord(plugin, pack, Level.WARNING, msg);
        logger.log(record);
    }

    @Override
    public void warn(final String msg, final Throwable thrown) {
        warn(null, msg, thrown);
    }

    @Override
    public void warn(final QuestPackage pack, final String msg, final Throwable thrown) {
        final BetonQuestLogRecord record = new BetonQuestLogRecord(plugin, pack, Level.WARNING, msg);
        logger.log(record);

        final BetonQuestLogRecord recordThrowable = new BetonQuestLogRecord(plugin, pack, Level.FINE, "Additional stacktrace:");
        recordThrowable.setThrown(thrown);
        logger.log(recordThrowable);
    }

    @Override
    public void error(final String msg) {
        error(null, msg);
    }

    @Override
    public void error(final QuestPackage pack, final String msg) {
        final BetonQuestLogRecord record = new BetonQuestLogRecord(plugin, pack, Level.SEVERE, msg);
        logger.log(record);
    }

    @Override
    public void error(final String msg, final Throwable thrown) {
        error(null, msg, thrown);
    }

    @Override
    public void error(final QuestPackage pack, final String msg, final Throwable thrown) {
        final BetonQuestLogRecord record = new BetonQuestLogRecord(plugin, pack, Level.SEVERE, msg);
        record.setThrown(thrown);
        logger.log(record);
    }

    @Override
    public void debug(final String msg) {
        debug(null, msg);
    }

    @Override
    public void debug(final QuestPackage pack, final String msg) {
        final BetonQuestLogRecord record = new BetonQuestLogRecord(plugin, pack, Level.FINE, msg);
        logger.log(record);
    }

    @Override
    public void debug(final String msg, final Throwable thrown) {
        debug(null, msg, thrown);
    }

    @Override
    public void debug(final QuestPackage pack, final String msg, final Throwable thrown) {
        final BetonQuestLogRecord record = new BetonQuestLogRecord(plugin, pack, Level.FINE, msg);
        record.setThrown(thrown);
        logger.log(record);
    }

    @Override
    public void reportException(final Throwable thrown) {
        reportException(null, thrown);
    }

    @Override
    public void reportException(final QuestPackage pack, final Throwable thrown) {
        final String msg = "This is an exception that should never occur. "
                + "If you don't know why this occurs please report it to the author.";
        final BetonQuestLogRecord record = new BetonQuestLogRecord(plugin, pack, Level.SEVERE, msg);
        record.setThrown(thrown);
        logger.log(record);
    }
}
