package org.betonquest.betonquest.modules.logger;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

/**
 * This class test the {@link BetonQuestLogger}.
 */
class DefaultBetonQuestLoggerTest {
    /**
     * The {@link QuestPackage} name.
     */
    public static final String CUSTOM_CONFIG_PACKAGE = "CustomTestPackage";

    /**
     * The log message.
     */
    private static final String LOG_MESSAGE = "Test Message";

    /**
     * The exception message.
     */
    private static final String EXCEPTION_MESSAGE = "Test Exception";

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
        this.logger = new DefaultBetonQuestLogger(mock(Plugin.class), parentLogger, getClass(), null);
        this.questPackage = mock(QuestPackage.class);
        when(this.questPackage.getQuestPath()).thenReturn(CUSTOM_CONFIG_PACKAGE);
    }

    @AfterEach
    void checkNoMoreInteractions() {
        verifyNoMoreInteractions(handler);
    }

    @Test
    void debug() {
        logger.debug(LOG_MESSAGE);
        verify(handler, times(1)).publish(argThat(record -> record.getLevel() == Level.FINE && LOG_MESSAGE.equals(record.getMessage())));
    }

    @Test
    void debugWithPackage() {
        logger.debug(questPackage, LOG_MESSAGE);
        verify(handler, times(1)).publish(argThat(record -> record.getLevel() == Level.FINE && LOG_MESSAGE.equals(record.getMessage())));
        verifyNoMoreInteractions(handler);
    }

    @Test
    void debugException() {
        logger.debug(LOG_MESSAGE, new IOException(EXCEPTION_MESSAGE));
        verify(handler, times(1)).publish(argThat(record -> record.getLevel() == Level.FINE && LOG_MESSAGE.equals(record.getMessage())));
        verifyNoMoreInteractions(handler);
    }

    @Test
    void debugExceptionWithPackage() {
        logger.debug(questPackage, LOG_MESSAGE, new IOException(EXCEPTION_MESSAGE));
        verify(handler, times(1)).publish(argThat(record -> record.getLevel() == Level.FINE && LOG_MESSAGE.equals(record.getMessage())));
        verifyNoMoreInteractions(handler);
    }

    @Test
    void info() {
        logger.info(LOG_MESSAGE);
        verify(handler, times(1)).publish(argThat(record -> record.getLevel() == Level.INFO && LOG_MESSAGE.equals(record.getMessage())));
        verifyNoMoreInteractions(handler);
    }

    @Test
    void infoWithPackage() {
        logger.info(questPackage, LOG_MESSAGE);
        verify(handler, times(1)).publish(argThat(record -> record.getLevel() == Level.INFO && LOG_MESSAGE.equals(record.getMessage())));
        verifyNoMoreInteractions(handler);
    }

    @Test
    void warn() {
        logger.warn(LOG_MESSAGE);
        verify(handler, times(1)).publish(argThat(record -> record.getLevel() == Level.WARNING && LOG_MESSAGE.equals(record.getMessage())));
        verifyNoMoreInteractions(handler);
    }

    @Test
    void warnWithPackage() {
        logger.warn(questPackage, LOG_MESSAGE);
        verify(handler, times(1)).publish(argThat(record -> record.getLevel() == Level.WARNING && LOG_MESSAGE.equals(record.getMessage())));
        verifyNoMoreInteractions(handler);
    }

    @Test
    void warnException() {
        logger.warn(LOG_MESSAGE, new IOException(EXCEPTION_MESSAGE));
        verify(handler, times(1)).publish(argThat(record -> record.getLevel() == Level.WARNING && LOG_MESSAGE.equals(record.getMessage())));
        verify(handler, times(1)).publish(argThat(record -> record.getLevel() == Level.FINE && "Additional stacktrace:".equals(record.getMessage())));
        verifyNoMoreInteractions(handler);
    }

    @Test
    void warnExceptionWithPackage() {
        logger.warn(questPackage, LOG_MESSAGE, new IOException(EXCEPTION_MESSAGE));
        verify(handler, times(1)).publish(argThat(record -> record.getLevel() == Level.WARNING && LOG_MESSAGE.equals(record.getMessage())));
        verify(handler, times(1)).publish(argThat(record -> record.getLevel() == Level.FINE && "Additional stacktrace:".equals(record.getMessage())));
        verifyNoMoreInteractions(handler);
    }

    @Test
    void error() {
        logger.error(LOG_MESSAGE);
        verify(handler, times(1)).publish(argThat(record -> record.getLevel() == Level.SEVERE && LOG_MESSAGE.equals(record.getMessage())));
        verifyNoMoreInteractions(handler);
    }

    @Test
    void errorWithPackage() {
        logger.error(questPackage, LOG_MESSAGE);
        verify(handler, times(1)).publish(argThat(record -> record.getLevel() == Level.SEVERE && LOG_MESSAGE.equals(record.getMessage())));
        verifyNoMoreInteractions(handler);
    }

    @Test
    void errorException() {
        logger.error(LOG_MESSAGE, new IOException(EXCEPTION_MESSAGE));
        verify(handler, times(1)).publish(argThat(record -> record.getLevel() == Level.SEVERE && LOG_MESSAGE.equals(record.getMessage())));
        verifyNoMoreInteractions(handler);
    }

    @Test
    void errorExceptionWithPackage() {
        logger.error(questPackage, LOG_MESSAGE, new IOException(EXCEPTION_MESSAGE));
        verify(handler, times(1)).publish(argThat(record -> record.getLevel() == Level.SEVERE && LOG_MESSAGE.equals(record.getMessage())));
        verifyNoMoreInteractions(handler);
    }

    @Test
    void reportException() {
        logger.reportException(new IOException(EXCEPTION_MESSAGE));
        verify(handler, times(1)).publish(argThat(record -> record.getLevel() == Level.SEVERE && record.getMessage().equals("This is an exception that should never occur. "
                + "If you don't know why this occurs please report it to the author.")));
        verifyNoMoreInteractions(handler);
    }

    @Test
    void reportExceptionWithPackage() {
        logger.reportException(questPackage, new IOException(EXCEPTION_MESSAGE));
        verify(handler, times(1)).publish(argThat(record -> record.getLevel() == Level.SEVERE && record.getMessage().equals("This is an exception that should never occur. "
                + "If you don't know why this occurs please report it to the author.")));
        verifyNoMoreInteractions(handler);
    }
}
