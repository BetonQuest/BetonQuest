package org.betonquest.betonquest.lib.config.section.unmodifiable;

import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.Tag;

/**
 * This is a test for {@link UnmodifiableConfiguration} as a {@link ConfigurationSection}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class UnmodifiableConfigurationSectionWithConfigurationTest extends UnmodifiableConfigurationSectionTest {

    @Override
    public ConfigurationSection getConfig() {
        return new UnmodifiableConfiguration(getDefaultConfig());
    }
}
