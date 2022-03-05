package org.betonquest.betonquest.api.bukkit.config.custom.multi;

import org.betonquest.betonquest.api.bukkit.config.util.ConfigurationBaseTest;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test for the {@link MultiConfiguration}.
 */
@SuppressWarnings({"PMD.JUnit5TestShouldBePackagePrivate", "PMD.JUnitAssertionsShouldIncludeMessage"})
public class MultiConfigurationTest extends ConfigurationBaseTest {
    /**
     * Empty constructor
     */
    public MultiConfigurationTest() {
        super();
    }

    @Override
    public Configuration getConfig() {
        final Configuration defaultConfig = super.getDefaultConfig();
        final Map<ConfigurationSection, String> configs = new HashMap<>();
        configs.put(defaultConfig, "config.yml");
        try {
            final MultiConfiguration multiConfiguration = new MultiConfiguration(new ArrayList<>(configs.keySet()));
            final Configuration defaults = defaultConfig.getDefaults();
            assertNotNull(defaults);
            multiConfiguration.setMultiDefaults(List.of(defaults));
            return multiConfiguration;
        } catch (final KeyConflictException e) {
            fail(e.resolvedMessage(configs), e);
        } catch (final InvalidConfigurationException e) {
            fail(e);
        }
        return null;
    }

    private void assertThrowsUnmodifiableException(final Executable executable) {
        final Exception exception = assertThrows(UnsupportedOperationException.class, executable);
        assertEquals(MultiConfiguration.UNMODIFIABLE_MESSAGE, exception.getMessage());
    }

    @Test
    @Override
    public void testAddDefaultOnRootSection() {
        assertThrowsUnmodifiableException(super::testAddDefaultOnRootSection);
    }

    @Test
    @Override
    public void testAddDefaultOnRootSectionOnExistingConfigPath() {
        assertThrowsUnmodifiableException(super::testAddDefaultOnRootSectionOnExistingConfigPath);
    }

    @Test
    @Override
    public void testAddDefaultsAsConfiguration() {
        assertThrowsUnmodifiableException(super::testAddDefaultsAsConfiguration);
    }

    @Test
    @Override
    public void testAddDefaultsAsConfigurationOnExistingConfigPath() {
        assertThrowsUnmodifiableException(super::testAddDefaultsAsConfigurationOnExistingConfigPath);
    }

    @Test
    @Override
    public void testAddDefaultsAsMap() {
        assertThrowsUnmodifiableException(super::testAddDefaultsAsMap);
    }

    @Test
    @Override
    public void testAddDefaultsAsMapOnExistingConfigPath() {
        assertThrowsUnmodifiableException(super::testAddDefaultsAsMapOnExistingConfigPath);
    }

    @Test
    @Override
    public void testSetDefaults() {
        assertThrowsUnmodifiableException(super::testSetDefaults);
    }

    @Test
    @Override
    public void testSetDefaultsOnExistingConfigPath() {
        assertThrowsUnmodifiableException(super::testSetDefaultsOnExistingConfigPath);
    }

    @Test
    @Override
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    public void testOptions() {
        assertTrue(config.options().copyDefaults());
        config.options().copyDefaults(true);
        assertTrue(config.options().copyDefaults());

        assertEquals('.', config.options().pathSeparator());
        config.options().pathSeparator('-');
        assertEquals('-', config.options().pathSeparator());
    }
}
