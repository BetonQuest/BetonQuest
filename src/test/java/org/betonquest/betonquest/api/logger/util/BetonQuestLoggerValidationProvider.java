package org.betonquest.betonquest.api.logger.util;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.mockito.MockedStatic;

import java.util.logging.Logger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

/**
 * Resolves a {@link LogValidator} for JUnit 5 tests.
 */
public class BetonQuestLoggerValidationProvider implements ParameterResolver, BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {
    /**
     * The MockedStatic instance of {@link BetonQuestLogger} class.
     */
    private static MockedStatic<BetonQuestLogger> betonQuestLogger;
    /**
     * The {@link LogValidator} instance.
     */
    private LogValidator logValidator;

    /**
     * Default {@link BetonQuestLoggerValidationProvider} Constructor.
     */
    public BetonQuestLoggerValidationProvider() {
    }

    @Override
    public void beforeAll(final ExtensionContext context) {
        betonQuestLogger = mockStatic(BetonQuestLogger.class);
        betonQuestLogger.when(() -> BetonQuestLogger.create(any(), any())).thenAnswer(invocation ->
                new BetonQuestLogger(Logger.getGlobal(), invocation.getArgument(0), invocation.getArgument(1)));
    }

    @Override
    public void beforeEach(final ExtensionContext context) {
        logValidator = new LogValidator();
        Logger.getGlobal().addHandler(logValidator);
    }

    @Override
    public void afterEach(final ExtensionContext context) {
        logValidator = null;
    }

    @Override
    public void afterAll(final ExtensionContext context) {
        betonQuestLogger.close();
        betonQuestLogger = null;
    }

    @Override
    public boolean supportsParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType() == LogValidator.class;
    }

    @Override
    public Object resolveParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) {
        return logValidator;
    }
}
