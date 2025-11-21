package org.betonquest.betonquest.api.bukkit.config.custom.lazy;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("ConfigurationSection")
@SuppressWarnings("PMD.UnitTestAssertionsShouldIncludeMessage")
class LazyConfigurationSectionLazynessTest {
    private ConfigurationSection config;

    private ConfigurationSection lazy;

    private ConfigurationSection getConfig() {
        final MemoryConfiguration config = new MemoryConfiguration();
        final LazyConfigurationSection lazy = new LazyConfigurationSection(config, "lazy");

        final MemoryConfiguration spy = spy(config);
        when(spy.getConfigurationSection("lazy")).thenReturn(lazy);

        return spy;
    }

    @BeforeEach
    void setUp() {
        config = getConfig();
        lazy = Objects.requireNonNull(config.getConfigurationSection("lazy"));
    }

    @Test
    void testLazySet() {
        lazy.set("key", "value");
        assertEquals("value", config.getString("lazy.key"));
    }

    @Test
    void testLazyAddDefault() {
        lazy.addDefault("key", "value");
        assertTrue(config.isSet("lazy"));
        assertEquals("value", config.getString("lazy.key"));
    }

    @Test
    void testLazyCreateSection() {
        lazy.createSection("key");
        assertNotNull(config.getConfigurationSection("lazy.key"));
    }

    @Test
    void testLazyCreateSectionWithMap() {
        final Map<String, String> map = Map.of("key", "value");
        lazy.createSection("section", map);
        assertEquals("value", config.getString("lazy.section.key"));
    }

    @Test
    void testLazySetComments() {
        lazy.setComments("key", null);
        assertTrue(config.isSet("lazy"));
    }

    @Test
    void testLazySetInlineComments() {
        lazy.setInlineComments("key", null);
        assertTrue(config.isSet("lazy"));
    }
}
