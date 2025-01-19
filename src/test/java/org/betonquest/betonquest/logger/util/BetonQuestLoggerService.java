package org.betonquest.betonquest.logger.util;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.logger.SingletonLoggerFactory;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.mockito.MockedStatic;

import java.lang.reflect.ParameterizedType;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

/**
 * This class provides a {@link BetonQuestLogger} and {@link BetonQuestLoggerFactory} for testing.
 * It can also expose the parent {@link Logger} used for the created BetonQuestLogger
 * and a {@link java.util.logging.Handler} that is registered for the parent logger.
 */
public class BetonQuestLoggerService implements BeforeEachCallback, ParameterResolver, AfterEachCallback {
    /**
     * The instance of a handler that is registered for the parent logger.
     */
    private BetonQuestLogger logger;

    /**
     * The instance of the BetonQuestLoggerFactory.
     */
    private BetonQuestLoggerFactory loggerFactory;

    /**
     * The MockedStatic instance of {@link BetonQuestLogger} class.
     */
    private MockedStatic<BetonQuest> staticBetonQuest;

    /**
     * Default {@link BetonQuestLoggerService} Constructor.
     */
    public BetonQuestLoggerService() {
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
        final BetonQuest betonQuest = mock(BetonQuest.class);
        lenient().when(betonQuest.getLoggerFactory()).thenReturn(loggerFactory);
        staticBetonQuest = mockStatic(BetonQuest.class);
        staticBetonQuest.when(BetonQuest::getInstance).thenReturn(betonQuest);
    }

    @Override
    public void afterEach(final ExtensionContext context) {
        staticBetonQuest.close();
        staticBetonQuest = null;
    }

    @Override
    public boolean supportsParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) {
        return isStaticBetonQuest(parameterContext)
                || parameterContext.getParameter().getType() == BetonQuestLogger.class
                || parameterContext.getParameter().getType() == BetonQuestLoggerFactory.class;
    }

    @Override
    public Object resolveParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) {
        if (isStaticBetonQuest(parameterContext)) {
            return staticBetonQuest;
        }
        if (parameterContext.getParameter().getType() == BetonQuestLogger.class) {
            return logger;
        }
        if (parameterContext.getParameter().getType() == BetonQuestLoggerFactory.class) {
            return loggerFactory;
        }
        return null;
    }

    private boolean isStaticBetonQuest(final ParameterContext parameterContext) {
        return parameterContext.getParameter().getType() == MockedStatic.class
                && parameterContext.getParameter().getParameterizedType() instanceof final ParameterizedType parameterizedType
                && parameterizedType.getActualTypeArguments()[0].equals(BetonQuest.class);
    }
}
