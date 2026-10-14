package org.betonquest.betonquest.lib.config.section.unmodifiable;

import org.junit.jupiter.api.Tag;

/**
 * This is a test for the {@link UnmodifiableConfigurationSection}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings("PMD.JUnitJupiterTestShouldBePackagePrivate")
public class UnmodifiableConfigurationSectionTest extends AbstractUnmodifiableConfigurationSectionTest {

    public UnmodifiableConfigurationSectionTest() {
        super(new UnmodifiableConfigurationSection(getDefaultConfig()));
    }
}
