package org.betonquest.betonquest.api.bukkit.config.custom.fallback;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link FallbackConfigurationSection} class.
 */
@SuppressWarnings({"PMD.JUnitAssertionsShouldIncludeMessage", "PMD.JUnit5TestShouldBePackagePrivate"})
public class FallbackConfigurationSectionEmptyOriginalTest extends FallbackConfigurationSectionTest {
    @Override
    public ConfigurationSection getConfig() {
        fallback = getDefaultConfig();
        return new FallbackConfiguration(new MemoryConfiguration(), fallback);
    }

    @Test
    @Override
    public void testSetComments() {
        final List<String> comments = new ArrayList<>();
        comments.add("Test Comment");
        config.setComments("existingSet", comments);
        assertTrue(config.getComments("existingSet").isEmpty());
    }


    @Test
    @Override
    public void testSetInlineComments() {
        final List<String> comments = new ArrayList<>();
        comments.add("Test Inline Comment");
        config.setInlineComments("existingSet", comments);
        assertTrue(config.getInlineComments("existingSet").isEmpty());
    }
}
