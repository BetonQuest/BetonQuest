package org.betonquest.betonquest.lib.config.section.lazy;

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
class LazyConfigurationSectionLazinessTest {

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
    void lazy_set() {
        lazy.set("key", "value");
        assertEquals("value", config.getString("lazy.key"));
    }

    @Test
    void lazy_add_default() {
        lazy.addDefault("key", "value");
        assertTrue(config.isSet("lazy"));
        assertEquals("value", config.getString("lazy.key"));
    }

    @Test
    void lazy_create_section() {
        lazy.createSection("key");
        assertNotNull(config.getConfigurationSection("lazy.key"));
    }

    @Test
    void lazy_create_section_with_map() {
        final Map<String, String> map = Map.of("key", "value");
        lazy.createSection("section", map);
        assertEquals("value", config.getString("lazy.section.key"));
    }

    @Test
    void lazy_set_comments() {
        lazy.setComments("key", null);
        assertTrue(config.isSet("lazy"));
    }

    @Test
    void lazy_set_inline_comments() {
        lazy.setInlineComments("key", null);
        assertTrue(config.isSet("lazy"));
    }
}
