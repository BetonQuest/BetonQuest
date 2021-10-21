package org.betonquest.betonquest.api.logger.util;

import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.utils.logger.BetonQuestLoggerImpl;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.mockito.MockedStatic;

import java.util.logging.Logger;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Resolves a {@link LogValidator} for JUnit 5 tests.
 */
public class BetonQuestLoggerValidationProvider implements ParameterResolver, BeforeAllCallback, AfterAllCallback {
    /**
     * The instance of the parent logger.
     */
    private final Logger parentLogger;
    /**
     * The MockedStatic instance of {@link BetonQuestLogger} class.
     */
    private MockedStatic<BetonQuestLogger> betonQuestLogger;

    /**
     * Default {@link BetonQuestLoggerValidationProvider} Constructor.
     */
    public BetonQuestLoggerValidationProvider() {
        parentLogger = LogValidator.getSilentLogger();
    }

    @Override
    public void beforeAll(final ExtensionContext context) {
        betonQuestLogger = mockStatic(BetonQuestLogger.class);
        betonQuestLogger.when(() -> BetonQuestLogger.create(any(), any())).thenAnswer(invocation ->
                new BetonQuestLoggerImpl(parentLogger, invocation.getArgument(0), invocation.getArgument(1)));
    }

    @Override
    public void afterAll(final ExtensionContext context) {
        betonQuestLogger.close();
        betonQuestLogger = null;
    }

    @Override
    public boolean supportsParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType() == LogValidator.class
                || parameterContext.getParameter().getType() == Logger.class;
    }

    @Override
    public Object resolveParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) {
        if (parameterContext.getParameter().getType() == LogValidator.class) {
            return LogValidator.getForLogger(parentLogger);
        }
        if (parameterContext.getParameter().getType() == Logger.class) {
            return parentLogger;
        }
        return null;
    }
}
