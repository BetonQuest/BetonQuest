package org.betonquest.betonquest.api.bukkit.config.custom.multi;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static com.ibm.icu.impl.Assert.fail;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test for the {@link MultiSectionConfiguration} and it's thrown {@link InvalidSubConfigurationException}s.
 */
@Tag("ConfigurationSection")
@SuppressWarnings("PMD.UnitTestAssertionsShouldIncludeMessage")
class MultiSectionConfigurationInvalidSubConfigurationExceptionTest {

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testInvalidPathSeparator() {
        final Configuration configuration = new MemoryConfiguration();
        configuration.options().pathSeparator('/');
        try {
            new MultiSectionConfiguration(List.of(configuration));
        } catch (final InvalidSubConfigurationException e) {
            assertEquals(configuration, e.getSubConfiguration());
            assertEquals("At least one source config does not have valid path separator!", e.getMessage());
            return;
        } catch (final KeyConflictException e) {
            fail(e);
        }
        fail("Expected an Exception!");
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testNoRoot() {
        try {
            new MultiSectionConfiguration(List.of(Mockito.mock(ConfigurationSection.class)));
        } catch (final InvalidSubConfigurationException e) {
            assertEquals("At least one source config does not have a root!", e.getMessage());
            return;
        } catch (final InvalidConfigurationException e) {
            fail(e);
        }
        fail("Expected an Exception!");
    }
}
