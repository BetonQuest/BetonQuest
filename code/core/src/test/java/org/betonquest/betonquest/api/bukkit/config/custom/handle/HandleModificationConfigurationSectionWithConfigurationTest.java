package org.betonquest.betonquest.api.bukkit.config.custom.handle;

import org.betonquest.betonquest.api.bukkit.config.custom.handle.util.HandleModificationToConfiguration;
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
        setter = new HandleModificationToConfiguration();
        return new HandleModificationConfiguration(super.getDefaultConfig(), setter);
    }
}
