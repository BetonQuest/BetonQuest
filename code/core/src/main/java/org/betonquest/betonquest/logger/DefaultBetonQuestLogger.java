package org.betonquest.betonquest.logger;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the implementation of the interface {@link BetonQuestLogger}.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class DefaultBetonQuestLogger implements BetonQuestLogger {

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
     * @param plugin       The {@link Plugin} this logger belongs to.
     * @param parentLogger The parent logger for this logger.
     * @param clazz        The calling class.
     * @param topic        The topic of the logger.
     */
    public DefaultBetonQuestLogger(final Plugin plugin, final Logger parentLogger, final Class<?> clazz, @Nullable final String topic) {
        this.plugin = plugin;
        this.logger = new TopicLogger(parentLogger, clazz, topic);
    }

    @Override
    public void debug(@Nullable final String msg) {
        debug(null, msg);
    }

    @Override
    public void debug(@Nullable final QuestPackage pack, @Nullable final String msg) {
        final BetonQuestLogRecord record = new BetonQuestLogRecord(Level.FINE, msg, plugin, pack);
        logger.log(record);
    }

    @Override
    public void debug(@Nullable final String msg, final Throwable thrown) {
        debug(null, msg, thrown);
    }

    @Override
    public void debug(@Nullable final QuestPackage pack, @Nullable final String msg, final Throwable thrown) {
        final BetonQuestLogRecord record = new BetonQuestLogRecord(Level.FINE, msg, plugin, pack);
        record.setThrown(thrown);
        logger.log(record);
    }

    @Override
    public void info(final String msg) {
        info(null, msg);
    }

    @Override
    public void info(@Nullable final QuestPackage pack, @Nullable final String msg) {
        final BetonQuestLogRecord record = new BetonQuestLogRecord(Level.INFO, msg, plugin, pack);
        logger.log(record);
    }

    @Override
    public void warn(@Nullable final String msg) {
        warn(null, msg);
    }

    @Override
    public void warn(@Nullable final QuestPackage pack, @Nullable final String msg) {
        final BetonQuestLogRecord record = new BetonQuestLogRecord(Level.WARNING, msg, plugin, pack);
        logger.log(record);
    }

    @Override
    public void warn(@Nullable final String msg, final Throwable thrown) {
        warn(null, msg, thrown);
    }

    @Override
    public void warn(@Nullable final QuestPackage pack, @Nullable final String msg, final Throwable thrown) {
        final BetonQuestLogRecord record = new BetonQuestLogRecord(Level.WARNING, msg, plugin, pack);
        logger.log(record);

        final BetonQuestLogRecord recordThrowable = new BetonQuestLogRecord(Level.FINE, "Additional stacktrace:", plugin, pack);
        recordThrowable.setThrown(thrown);
        logger.log(recordThrowable);
    }

    @Override
    public void error(@Nullable final String msg) {
        error(null, msg);
    }

    @Override
    public void error(@Nullable final QuestPackage pack, @Nullable final String msg) {
        final BetonQuestLogRecord record = new BetonQuestLogRecord(Level.SEVERE, msg, plugin, pack);
        logger.log(record);
    }

    @Override
    public void error(@Nullable final String msg, final Throwable thrown) {
        error(null, msg, thrown);
    }

    @Override
    public void error(@Nullable final QuestPackage pack, @Nullable final String msg, final Throwable thrown) {
        final BetonQuestLogRecord record = new BetonQuestLogRecord(Level.SEVERE, msg, plugin, pack);
        record.setThrown(thrown);
        logger.log(record);
    }

    @Override
    public void reportException(final Throwable thrown) {
        reportException(null, thrown);
    }

    @Override
    public void reportException(@Nullable final QuestPackage pack, final Throwable thrown) {
        final String msg = "This is an exception that should never occur. "
                + "If you don't know why this occurs please report it to the author.";
        final BetonQuestLogRecord record = new BetonQuestLogRecord(Level.SEVERE, msg, plugin, pack);
        record.setThrown(thrown);
        logger.log(record);
    }
}
