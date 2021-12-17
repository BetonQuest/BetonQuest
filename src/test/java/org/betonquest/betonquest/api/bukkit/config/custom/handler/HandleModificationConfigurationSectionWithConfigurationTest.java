package org.betonquest.betonquest.api.bukkit.config.custom.handler;

import org.betonquest.betonquest.api.bukkit.config.custom.handler.util.HandleModificationToConfiguration;
import org.bukkit.configuration.ConfigurationSection;

/**
 * This is a test for {@link HandleModificationConfigurationSection} as a {@link ConfigurationSection}.
 */
public class HandleModificationConfigurationSectionWithConfigurationTest extends HandleModificationConfigurationSectionTest {
    /**
     * Empty constructor
     */
    public HandleModificationConfigurationSectionWithConfigurationTest() {
        super();
    }

    @Override
    public ConfigurationSection getConfig() {
        setter = new HandleModificationToConfiguration();
        return new HandleModificationConfiguration(super.getDefaultConfig(), setter);
    }
}
