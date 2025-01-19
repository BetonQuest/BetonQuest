package org.betonquest.betonquest.logger;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.logger.util.BetonQuestLoggerService;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;

import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

/**
 * This class test the {@link BetonQuestLogger}.
 */
class DefaultBetonQuestLoggerTest {
    /**
     * The {@link QuestPackage} name.
     */
    public static final String PACKAGE_NAME = "CustomTestPackage";

    /**
     * The additional stacktrace message.
     */
    public static final String STACKTRACE_MESSAGE = "Additional stacktrace:";

    /**
     *
     */
    public static final String REPORT_MESSAGE = "This is an exception that should never occur. "
            + "If you don't know why this occurs please report it to the author.";

    /**
     * The {@link IOException} for testing.
     */
    public static final IOException IO_EXCEPTION = new IOException("Test Exception");

    /**
     * The log message.
     */
    private static final String LOG_MESSAGE = "Test Message";

    /**
     * The {@link Handler} for testing.
     */
    private Handler handler;

    /**
     * The {@link BetonQuestLogger} to test.
     */
    private BetonQuestLogger logger;

    /**
     * The {@link QuestPackage} for testing.
     */
    private QuestPackage questPackage;

    @BeforeEach
    void setUp() {
        this.handler = mock(Handler.class);
        final Logger parentLogger = BetonQuestLoggerService.getSilentLogger();
        parentLogger.addHandler(handler);
        final Plugin plugin = mock(Plugin.class);
        when(plugin.getName()).thenReturn("TestPlugin");
        this.logger = new DefaultBetonQuestLogger(plugin, parentLogger, getClass(), null);
        this.questPackage = mock(QuestPackage.class);
        when(this.questPackage.getQuestPath()).thenReturn(PACKAGE_NAME);
    }

    @AfterEach
    void checkNoMoreInteractions() {
        verifyNoMoreInteractions(handler);
    }

    @Test
    void debug() {
        logger.debug(LOG_MESSAGE);
        verify(handler, times(1)).publish(argThat(new RecordMatcher(Level.FINE, LOG_MESSAGE)));
    }

    @Test
    void debugWithPackage() {
        logger.debug(questPackage, LOG_MESSAGE);
        verify(handler, times(1)).publish(argThat(new RecordMatcher(PACKAGE_NAME, Level.FINE, LOG_MESSAGE)));
    }

    @Test
    void debugException() {
        logger.debug(LOG_MESSAGE, IO_EXCEPTION);
        verify(handler, times(1)).publish(argThat(new RecordMatcher(Level.FINE, LOG_MESSAGE, IO_EXCEPTION)));
    }

    @Test
    void debugExceptionWithPackage() {
        logger.debug(questPackage, LOG_MESSAGE, IO_EXCEPTION);
        verify(handler, times(1)).publish(argThat(new RecordMatcher(PACKAGE_NAME, Level.FINE, LOG_MESSAGE, IO_EXCEPTION)));
    }

    @Test
    void info() {
        logger.info(LOG_MESSAGE);
        verify(handler, times(1)).publish(argThat(new RecordMatcher(Level.INFO, LOG_MESSAGE)));
    }

    @Test
    void infoWithPackage() {
        logger.info(questPackage, LOG_MESSAGE);
        verify(handler, times(1)).publish(argThat(new RecordMatcher(PACKAGE_NAME, Level.INFO, LOG_MESSAGE)));
    }

    @Test
    void warn() {
        logger.warn(LOG_MESSAGE);
        verify(handler, times(1)).publish(argThat(new RecordMatcher(Level.WARNING, LOG_MESSAGE)));
    }

    @Test
    void warnWithPackage() {
        logger.warn(questPackage, LOG_MESSAGE);
        verify(handler, times(1)).publish(argThat(new RecordMatcher(PACKAGE_NAME, Level.WARNING, LOG_MESSAGE)));
    }

    @Test
    void warnException() {
        logger.warn(LOG_MESSAGE, IO_EXCEPTION);
        verify(handler, times(1)).publish(argThat(new RecordMatcher(Level.WARNING, LOG_MESSAGE)));
        verify(handler, times(1)).publish(argThat(new RecordMatcher(Level.FINE, STACKTRACE_MESSAGE, IO_EXCEPTION)));
    }

    @Test
    void warnExceptionWithPackage() {
        logger.warn(questPackage, LOG_MESSAGE, IO_EXCEPTION);
        verify(handler, times(1)).publish(argThat(new RecordMatcher(PACKAGE_NAME, Level.WARNING, LOG_MESSAGE)));
        verify(handler, times(1)).publish(argThat(new RecordMatcher(PACKAGE_NAME, Level.FINE, STACKTRACE_MESSAGE, IO_EXCEPTION)));
    }

    @Test
    void error() {
        logger.error(LOG_MESSAGE);
        verify(handler, times(1)).publish(argThat(new RecordMatcher(Level.SEVERE, LOG_MESSAGE)));
    }

    @Test
    void errorWithPackage() {
        logger.error(questPackage, LOG_MESSAGE);
        verify(handler, times(1)).publish(argThat(new RecordMatcher(PACKAGE_NAME, Level.SEVERE, LOG_MESSAGE)));
    }

    @Test
    void errorException() {
        logger.error(LOG_MESSAGE, IO_EXCEPTION);
        verify(handler, times(1)).publish(argThat(new RecordMatcher(Level.SEVERE, LOG_MESSAGE, IO_EXCEPTION)));
    }

    @Test
    void errorExceptionWithPackage() {
        logger.error(questPackage, LOG_MESSAGE, IO_EXCEPTION);
        verify(handler, times(1)).publish(argThat(new RecordMatcher(PACKAGE_NAME, Level.SEVERE, LOG_MESSAGE, IO_EXCEPTION)));
    }

    @Test
    void reportException() {
        logger.reportException(IO_EXCEPTION);
        verify(handler, times(1)).publish(argThat(new RecordMatcher(Level.SEVERE, REPORT_MESSAGE, IO_EXCEPTION)));
    }

    @Test
    void reportExceptionWithPackage() {
        logger.reportException(questPackage, IO_EXCEPTION);
        verify(handler, times(1)).publish(argThat(new RecordMatcher(PACKAGE_NAME, Level.SEVERE, REPORT_MESSAGE, IO_EXCEPTION)));
    }

    /**
     * Argument matcher for {@link LogRecord}s.
     */
    private static class RecordMatcher implements ArgumentMatcher<LogRecord> {
        /**
         * The package name to match.
         */
        private final String pack;

        /**
         * The level to match.
         */
        private final Level level;

        /**
         * The message to match.
         */
        private final String message;

        /**
         * The exception to match.
         */
        private final Exception exception;

        /**
         * Creates a new record matcher.
         *
         * @param level   The level
         * @param message The message
         */
        public RecordMatcher(final Level level, final String message) {
            this(null, level, message, null);
        }

        /**
         * Creates a new record matcher.
         *
         * @param level     The level
         * @param message   The message
         * @param exception The exception
         */
        public RecordMatcher(final Level level, final String message, final Exception exception) {
            this(null, level, message, exception);
        }

        /**
         * Creates a new record matcher.
         *
         * @param pack    The package name
         * @param level   The level
         * @param message The message
         */
        public RecordMatcher(final String pack, final Level level, final String message) {
            this(pack, level, message, null);
        }

        /**
         * Creates a new record matcher.
         *
         * @param pack      The package name
         * @param level     The level
         * @param message   The message
         * @param exception The exception
         */
        public RecordMatcher(final String pack, final Level level, final String message, final Exception exception) {
            this.pack = pack;
            this.level = level;
            this.message = message;
            this.exception = exception;
        }

        @Override
        public boolean matches(final LogRecord record) {
            if (!(record instanceof final BetonQuestLogRecord betonQuestLogRecord)) {
                return false;
            }
            final String pack = betonQuestLogRecord.getPack().orElse(null);

            final boolean packMatch = this.pack == null && pack == null || this.pack != null && this.pack.equals(pack);
            final boolean exceptionMatch = exception == null && record.getThrown() == null || exception != null && exception.equals(record.getThrown());
            return packMatch && exceptionMatch && record.getLevel().equals(level) && message.equals(record.getMessage());
        }
    }
}
