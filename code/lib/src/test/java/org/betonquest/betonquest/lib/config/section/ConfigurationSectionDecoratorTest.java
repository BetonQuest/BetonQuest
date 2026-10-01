package org.betonquest.betonquest.lib.config.section;

import org.betonquest.betonquest.lib.config.util.ConfigurationSectionBaseTest;
import org.junit.jupiter.api.Tag;

/**
 * This is a test for the {@link ConfigurationSectionDecorator}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings("PMD.JUnitJupiterTestShouldBePackagePrivate")
public class ConfigurationSectionDecoratorTest extends ConfigurationSectionBaseTest {

    public ConfigurationSectionDecoratorTest() {
        super(new ConfigurationSectionDecorator(ConfigurationSectionBaseTest.getDefaultConfig()));
    }
}
