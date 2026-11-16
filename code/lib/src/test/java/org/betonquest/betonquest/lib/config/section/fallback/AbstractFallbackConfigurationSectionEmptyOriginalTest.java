package org.betonquest.betonquest.lib.config.section.fallback;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Base abstract test class for {@link FallbackConfigurationSection} with empty original.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.UnitTestAssertionsShouldIncludeMessage", "PMD.JUnitJupiterTestShouldBePackagePrivate"})
public abstract class AbstractFallbackConfigurationSectionEmptyOriginalTest extends AbstractFallbackConfigurationSectionTest {

    public AbstractFallbackConfigurationSectionEmptyOriginalTest(final ConfigurationSection config, final Configuration fallback) {
        super(config, fallback);
    }

    @Test
    @Override
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    public void set_section_overwriting_existing() {
        assertTrue(config.isSet("childSection"));
        final MemoryConfiguration configuration = new MemoryConfiguration();
        configuration.set("test", "test");
        config.set("childSection", configuration);
        final ConfigurationSection childSection = config.getConfigurationSection("childSection");
        assertNotNull(childSection);
        assertEquals("[nestedChildSection, nestedChildSection.key, test]", childSection.getKeys(true).toString());
    }

    @Test
    @Override
    public void set_existing_section_to_null() {
        assertTrue(config.isSet("childSection"));
        config.set("childSection", null);
        assertTrue(config.isSet("childSection"));
    }

    @Test
    void original_is_not_modified_by_read() {
        config.getValues(true);
        final Set<String> keys = getOriginal().getKeys(true);
        assertTrue(keys.isEmpty(), "Original was modified by read operation: " + keys);
    }

    @Test
    @Override
    public void set_comments() {
        final List<String> comments = new ArrayList<>();
        comments.add("Test Comment");
        config.setComments("existingSet", comments);
        assertTrue(config.getComments("existingSet").isEmpty());
    }

    @Test
    @Override
    public void set_inline_comments() {
        final List<String> comments = new ArrayList<>();
        comments.add("Test Inline Comment");
        config.setInlineComments("existingSet", comments);
        assertTrue(config.getInlineComments("existingSet").isEmpty());
    }
}
