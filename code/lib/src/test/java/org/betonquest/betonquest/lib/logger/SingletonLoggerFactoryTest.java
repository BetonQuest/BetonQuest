package org.betonquest.betonquest.lib.logger;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SingletonLoggerFactoryTest {

    @Mock
    private BetonQuestLogger logger;

    private SingletonLoggerFactory loggerFactory;

    @BeforeEach
    void setLogger() {
        loggerFactory = new SingletonLoggerFactory(logger);
    }

    @Test
    void same_instance_on_create_with_class() {
        final BetonQuestLogger betonQuestLogger = loggerFactory.create(SingletonLoggerFactoryTest.class);
        assertSame(logger, betonQuestLogger, "Logger should be the same instance");
    }

    @Test
    void same_instance_on_create_with_class_and_topic() {
        final BetonQuestLogger betonQuestLogger = loggerFactory.create(SingletonLoggerFactoryTest.class, "topic");
        assertSame(logger, betonQuestLogger, "Logger should be the same instance");
    }

    @Test
    void same_instance_on_create_with_plugin() {
        final BetonQuestLogger betonQuestLogger = loggerFactory.create(mock(org.bukkit.plugin.Plugin.class));
        assertSame(logger, betonQuestLogger, "Logger should be the same instance");
    }

    @Test
    void same_instance_on_create_with_plugin_and_topic() {
        final BetonQuestLogger betonQuestLogger = loggerFactory.create(mock(org.bukkit.plugin.Plugin.class), "topic");
        assertSame(logger, betonQuestLogger, "Logger should be the same instance");
    }
}
