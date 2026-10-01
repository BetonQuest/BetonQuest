package org.betonquest.betonquest.lib.config.section.handle;

import org.betonquest.betonquest.lib.config.section.handle.util.HandleModificationToConfigurationFixture;
import org.junit.jupiter.api.Tag;

/**
 * This is a test for the {@link HandleModificationConfigurationSection} as a {@link org.bukkit.configuration.ConfigurationSection}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings("PMD.JUnitJupiterTestShouldBePackagePrivate")
public class HandleModificationConfigurationSectionWithConfigurationTest extends AbstractHandleModificationConfigurationSectionTest {

    public HandleModificationConfigurationSectionWithConfigurationTest() {
        super(new HandleModificationConfiguration(getDefaultConfig(), new HandleModificationToConfigurationFixture()));
    }
}
