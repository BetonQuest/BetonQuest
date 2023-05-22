package org.betonquest.betonquest.modules.logger.util;

import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.modules.logger.BetonQuestLoggerImpl;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
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
public class BetonQuestLoggerService implements ParameterResolver, BeforeAllCallback, AfterAllCallback {
    /**
     * The topic of generated {@link BetonQuestLogger} in the {@link ParameterResolver}.
     */
    public static final String LOGGER_TOPIC = "GeneratedTopic";

    /**
     * The mocked plugin instance.
     */
    private final Plugin plugin;

    /**
     * The instance of the parent logger.
     */
    private final Logger parentLogger;

    /**
     * The MockedStatic instance of {@link BetonQuestLogger} class.
     */
    private MockedStatic<BetonQuestLogger> betonQuestLogger;

    /**
     * Default {@link BetonQuestLoggerService} Constructor.
     */
    public BetonQuestLoggerService() {
        plugin = mock(Plugin.class);
        when(plugin.getName()).thenReturn("GeneratedPlugin");
        parentLogger = LogValidator.getSilentLogger();
    }

    @Override
    public void beforeAll(final ExtensionContext context) {
        betonQuestLogger = mockStatic(BetonQuestLogger.class);
        betonQuestLogger.when(BetonQuestLogger::create).thenAnswer(invocation ->
                new BetonQuestLoggerImpl(plugin, parentLogger, getCallingClass(), null));
        betonQuestLogger.when(() -> BetonQuestLogger.create(anyString())).thenAnswer(invocation ->
                new BetonQuestLoggerImpl(plugin, parentLogger, getCallingClass(), invocation.getArgument(0)));
        betonQuestLogger.when(() -> BetonQuestLogger.create(any(Class.class))).thenAnswer(invocation ->
                new BetonQuestLoggerImpl(plugin, parentLogger, invocation.getArgument(0), null));
        betonQuestLogger.when(() -> BetonQuestLogger.create(any(Class.class), anyString())).thenAnswer(invocation ->
                new BetonQuestLoggerImpl(plugin, parentLogger, invocation.getArgument(0), invocation.getArgument(1)));
        betonQuestLogger.when(() -> BetonQuestLogger.create(any(Plugin.class))).thenAnswer(invocation ->
                new BetonQuestLoggerImpl(plugin, parentLogger, invocation.getArgument(0).getClass(), null));
        betonQuestLogger.when(() -> BetonQuestLogger.create(any(Plugin.class), anyString())).thenAnswer(invocation ->
                new BetonQuestLoggerImpl(plugin, parentLogger, invocation.getArgument(0).getClass(), invocation.getArgument(1)));
    }

    @NotNull
    private Class<?> getCallingClass() {
        try {
            final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            boolean found = false;
            for (final StackTraceElement stackTraceElement : stackTrace) {
                final String className = stackTraceElement.getClassName();
                if (className.equals(BetonQuestLogger.class.getName())) {
                    found = true;
                    continue;
                }
                if (found) {
                    return Class.forName(className);
                }
            }
        } catch (final ClassNotFoundException e) {
            throw new IllegalStateException("It was not possible to create a logger for the current class!", e);
        }
        throw new IllegalStateException("It was not possible to create a logger for the current class!");
    }

    @Override
    public void afterAll(final ExtensionContext context) {
        betonQuestLogger.close();
        betonQuestLogger = null;
    }

    @Override
    public boolean supportsParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType() == LogValidator.class
                || parameterContext.getParameter().getType() == Logger.class
                || parameterContext.getParameter().getType() == BetonQuestLogger.class;
    }

    @Override
    public Object resolveParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) {
        if (parameterContext.getParameter().getType() == LogValidator.class) {
            return LogValidator.getForLogger(parentLogger);
        }
        if (parameterContext.getParameter().getType() == Logger.class) {
            return parentLogger;
        }
        if (parameterContext.getParameter().getType() == BetonQuestLogger.class) {
            return new BetonQuestLoggerImpl(plugin, parentLogger, parameterContext.getClass(), LOGGER_TOPIC);
        }
        return null;
    }
}
