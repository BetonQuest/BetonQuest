package org.betonquest.betonquest.lib.config.section.multi.fallback;

import org.betonquest.betonquest.lib.config.section.fallback.AbstractFallbackConfigurationEmptyOriginalTest;
import org.betonquest.betonquest.lib.config.section.multi.InvalidSubConfigurationException;
import org.betonquest.betonquest.lib.config.section.multi.KeyConflictException;
import org.betonquest.betonquest.lib.config.section.multi.MultiSectionConfiguration;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Tag;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test for the {@link MultiFallbackConfiguration}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings("PMD.JUnitJupiterTestShouldBePackagePrivate")
public class FallbackConfigurationEmptyOriginalWithMultiFallbackTest extends AbstractFallbackConfigurationEmptyOriginalTest {

    public FallbackConfigurationEmptyOriginalWithMultiFallbackTest() {
        super(createConfig(getDefaultConfig()), getDefaultConfig());
    }

    private static Configuration createConfig(final Configuration fallback) {
        try {
            return new MultiFallbackConfiguration(new MultiSectionConfiguration(List.of(new MemoryConfiguration())), fallback);
        } catch (final KeyConflictException | InvalidSubConfigurationException e) {
            fail(e);
        }
        return null;
    }
}
