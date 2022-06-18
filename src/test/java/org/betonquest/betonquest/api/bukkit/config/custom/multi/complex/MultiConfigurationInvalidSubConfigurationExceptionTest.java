package org.betonquest.betonquest.api.bukkit.config.custom.multi.complex;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.InvalidSubConfigurationException;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.KeyConflictException;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static com.ibm.icu.impl.Assert.fail;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test for the {@link MultiConfiguration} and it's thrown {@link InvalidSubConfigurationException}s.
 */
@SuppressWarnings("PMD.JUnitAssertionsShouldIncludeMessage")
class MultiConfigurationInvalidSubConfigurationExceptionTest {

    @Test
    void testInvalidPathSeparator() {
        final YamlConfiguration configuration = new YamlConfiguration();
        configuration.options().pathSeparator('/');
        try {
            new MultiConfiguration(List.of(configuration));
        } catch (final InvalidSubConfigurationException e) {
            assertEquals("At least one source config does not have valid path separator!", e.getMessage());
            return;
        } catch (final KeyConflictException e) {
            fail(e);
        }
        fail("Expected an Exception!");
    }

    @Test
    void testNoRoot() {
        try {
            new MultiConfiguration(List.of(Mockito.mock(ConfigurationSection.class)));
        } catch (final InvalidSubConfigurationException e) {
            assertEquals("At least one source config does not have a root!", e.getMessage());
            return;
        } catch (final InvalidConfigurationException e) {
            fail(e);
        }
        fail("Expected an Exception!");
    }
}
