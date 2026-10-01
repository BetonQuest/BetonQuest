package org.betonquest.betonquest.lib.config.section.multi.fallback;

import org.betonquest.betonquest.lib.config.section.fallback.FallbackConfigurationSectionNonFallbackTest;
import org.betonquest.betonquest.lib.config.section.multi.InvalidSubConfigurationException;
import org.betonquest.betonquest.lib.config.section.multi.KeyConflictException;
import org.betonquest.betonquest.lib.config.section.multi.MultiSectionConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.Tag;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test for the {@link MultiFallbackConfiguration}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings("PMD.JUnitJupiterTestShouldBePackagePrivate")
public class FallbackConfigurationSectionNonFallbackWithMultiFallbackTest extends FallbackConfigurationSectionNonFallbackTest {

    public FallbackConfigurationSectionNonFallbackWithMultiFallbackTest() {
        super(createConfig());
    }

    private static ConfigurationSection createConfig() {
        try {
            return new MultiFallbackConfiguration(new MultiSectionConfiguration(List.of(getDefaultConfig())), null);
        } catch (final KeyConflictException | InvalidSubConfigurationException e) {
            fail(e);
        }
        return null;
    }
}
