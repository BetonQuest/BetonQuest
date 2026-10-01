package org.betonquest.betonquest.lib.config.section.multi.fallback;

import org.betonquest.betonquest.lib.config.section.fallback.FallbackConfigurationNonFallbackTest;
import org.betonquest.betonquest.lib.config.section.multi.InvalidSubConfigurationException;
import org.betonquest.betonquest.lib.config.section.multi.KeyConflictException;
import org.betonquest.betonquest.lib.config.section.multi.MultiSectionConfiguration;
import org.bukkit.configuration.Configuration;
import org.junit.jupiter.api.Tag;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test for the {@link MultiFallbackConfiguration}.
 */
@Tag("ConfigurationSection")
class FallbackConfigurationNonFallbackWithMultiFallbackTest extends FallbackConfigurationNonFallbackTest {

    public FallbackConfigurationNonFallbackWithMultiFallbackTest() {
        super(createConfig());
    }

    private static Configuration createConfig() {
        try {
            return new MultiFallbackConfiguration(new MultiSectionConfiguration(List.of(getDefaultConfig())), null);
        } catch (final KeyConflictException | InvalidSubConfigurationException e) {
            fail(e);
        }
        return null;
    }
}
