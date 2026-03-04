package org.betonquest.betonquest.lib.config.section.handle;

import org.betonquest.betonquest.lib.config.section.handle.util.HandleModificationToConfigurationFixture;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.Tag;

/**
 * This is a test for the {@link HandleModificationConfigurationSection} as a {@link ConfigurationSection}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class HandleModificationConfigurationSectionWithConfigurationTest extends HandleModificationConfigurationSectionTest {

    @Override
    public ConfigurationSection getConfig() {
        setter = new HandleModificationToConfigurationFixture();
        return new HandleModificationConfiguration(super.getDefaultConfig(), setter);
    }
}
