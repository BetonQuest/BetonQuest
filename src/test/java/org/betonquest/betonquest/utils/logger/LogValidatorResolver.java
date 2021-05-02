package org.betonquest.betonquest.utils.logger;

import org.bukkit.plugin.java.JavaPlugin;
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
import static org.mockito.Mockito.*;

/**
 * Resolves a {@link LogValidator} for JUnit 5 tests.
 */
public class LogValidatorResolver implements ParameterResolver, BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {
    /**
     * The {@link LogValidator} instance.
     */
    private static LogValidator logValidator;
    /**
     * The MockedStatic instance of {@link BetonQuestLogger} class.
     */
    private static MockedStatic<BetonQuestLogger> betonQuestLogger;

    /**
     * Default {@link LogValidatorResolver} Constructor.
     */
    public LogValidatorResolver() {
    }

    @Override
    public void beforeAll(final ExtensionContext context) {
        final JavaPlugin javaPlugin = mock(JavaPlugin.class);
        when(javaPlugin.getLogger()).thenReturn(Logger.getGlobal());

        betonQuestLogger = mockStatic(BetonQuestLogger.class);
        betonQuestLogger.when(() -> BetonQuestLogger.create(any(), any())).thenAnswer(invocation ->
                new BetonQuestLogger(javaPlugin, invocation.getArgument(0), invocation.getArgument(1)));

        logValidator = new LogValidator();
        Logger.getGlobal().addHandler(logValidator);
    }

    @Override
    public void beforeEach(final ExtensionContext context) {
        logValidator.flush();
    }

    @Override
    public void afterEach(final ExtensionContext context) {
        logValidator.assertEmpty();
    }

    @Override
    public void afterAll(final ExtensionContext context) {
        betonQuestLogger.close();
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
