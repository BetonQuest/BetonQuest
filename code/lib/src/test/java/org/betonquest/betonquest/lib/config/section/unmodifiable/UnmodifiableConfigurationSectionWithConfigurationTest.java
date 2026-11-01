package org.betonquest.betonquest.lib.config.section.unmodifiable;

import org.junit.jupiter.api.Tag;

/**
 * This is a test for {@link UnmodifiableConfiguration} as a {@link org.bukkit.configuration.ConfigurationSection}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings("PMD.JUnitJupiterTestShouldBePackagePrivate")
public class UnmodifiableConfigurationSectionWithConfigurationTest extends AbstractUnmodifiableConfigurationSectionTest {

    public UnmodifiableConfigurationSectionWithConfigurationTest() {
        super(new UnmodifiableConfiguration(getDefaultConfig()));
    }
}
