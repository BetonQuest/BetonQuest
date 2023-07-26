package org.betonquest.betonquest.api.logger;

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

    private SingletonLoggerFactory factory;

    @BeforeEach
    void setLogger() {
        factory = new SingletonLoggerFactory(logger);
    }

    @Test
    void same_instance_on_create_with_Class() {
        final BetonQuestLogger betonQuestLogger = factory.create(SingletonLoggerFactoryTest.class);
        assertSame(logger, betonQuestLogger, "Logger should be the same instance");
    }

    @Test
    void same_instance_on_create_with_Class_and_Topic() {
        final BetonQuestLogger betonQuestLogger = factory.create(SingletonLoggerFactoryTest.class, "topic");
        assertSame(logger, betonQuestLogger, "Logger should be the same instance");
    }

    @Test
    void same_instance_on_create_with_Plugin() {
        final BetonQuestLogger betonQuestLogger = factory.create(mock(org.bukkit.plugin.Plugin.class));
        assertSame(logger, betonQuestLogger, "Logger should be the same instance");
    }

    @Test
    void same_instance_on_create_with_Plugin_and_Topic() {
        final BetonQuestLogger betonQuestLogger = factory.create(mock(org.bukkit.plugin.Plugin.class), "topic");
        assertSame(logger, betonQuestLogger, "Logger should be the same instance");
    }
}
