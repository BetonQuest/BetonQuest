package org.betonquest.betonquest.api.bukkit.config.custom.fallback;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link FallbackConfigurationSection} class.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.UnitTestAssertionsShouldIncludeMessage", "PMD.JUnit5TestShouldBePackagePrivate"})
public class FallbackConfigurationSectionEmptyOriginalTest extends FallbackConfigurationSectionTest {
    protected Configuration original;

    @Override
    public ConfigurationSection getConfig() throws InvalidConfigurationException {
        fallback = getDefaultConfig();
        original = new MemoryConfiguration();
        return new FallbackConfiguration(original, fallback);
    }

    @Test
    @Override
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    public void testSetSectionOverwritingExisting() {
        assertTrue(config.isSet("childSection"));
        final MemoryConfiguration configuration = new MemoryConfiguration();
        configuration.set("test", "test");
        config.set("childSection", configuration);
        final ConfigurationSection childSection = config.getConfigurationSection("childSection");
        assertNotNull(childSection);
        assertEquals("[test, nestedChildSection, nestedChildSection.key]", childSection.getKeys(true).toString());
    }

    @Test
    @Override
    public void testSetExistingSectionToNull() {
        assertTrue(config.isSet("childSection"));
        config.set("childSection", null);
        assertTrue(config.isSet("childSection"));
    }

    @Test
    void testOriginalIsNotModifiedByRead() {
        config.getValues(true);
        final Set<String> keys = original.getKeys(true);
        assertTrue(keys.isEmpty(), "Original was modified by read operation: " + keys);
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
