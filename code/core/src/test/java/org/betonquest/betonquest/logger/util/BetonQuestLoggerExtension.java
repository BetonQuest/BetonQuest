package org.betonquest.betonquest.logger.util;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.lib.logger.SingletonLoggerFactory;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.logging.Logger;

import static org.mockito.Mockito.*;

/**
 * This class provides a {@link BetonQuestLogger} and {@link BetonQuestLoggerFactory} for testing.
 * It can also expose the parent {@link Logger} used for the created BetonQuestLogger
 * and a {@link java.util.logging.Handler} that is registered for the parent logger.
 */
public class BetonQuestLoggerExtension implements BeforeEachCallback, ParameterResolver {

    /**
     * The instance of a handler that is registered for the parent logger.
     */
    private BetonQuestLogger logger;

    /**
     * The instance of the BetonQuestLoggerFactory.
     */
    private BetonQuestLoggerFactory loggerFactory;

    /**
     * The instance of the API.
     */
    private BetonQuestApi betonQuestApi;

    /**
     * Default {@link BetonQuestLoggerExtension} Constructor.
     */
    public BetonQuestLoggerExtension() {
    }

    /**
     * Creates an anonymous and silent logger. This is an optimal way to obtain a logger for testing.
     *
     * @return a silent logger
     */
    public static Logger getSilentLogger() {
        final Logger logger = Logger.getAnonymousLogger();
        logger.setUseParentHandlers(false);
        return logger;
    }

    @Override
    public void beforeEach(final ExtensionContext context) {
        this.logger = mock(BetonQuestLogger.class);
        this.loggerFactory = new SingletonLoggerFactory(logger);
        this.betonQuestApi = mock(BetonQuestApi.class);
        lenient().when(betonQuestApi.loggerFactory()).thenReturn(loggerFactory);
    }

    @Override
    public boolean supportsParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType() == BetonQuestApi.class
                || parameterContext.getParameter().getType() == BetonQuestLogger.class
                || parameterContext.getParameter().getType() == BetonQuestLoggerFactory.class;
    }

    @Override
    @Nullable
    public Object resolveParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) {
        if (parameterContext.getParameter().getType() == BetonQuestApi.class) {
            return betonQuestApi;
        }
        if (parameterContext.getParameter().getType() == BetonQuestLogger.class) {
            return logger;
        }
        if (parameterContext.getParameter().getType() == BetonQuestLoggerFactory.class) {
            return loggerFactory;
        }
        return null;
    }
}
