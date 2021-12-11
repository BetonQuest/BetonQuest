package org.betonquest.betonquest.api.bukkit.config.custom;

import org.betonquest.betonquest.api.bukkit.config.util.DelegateModificationToConfiguration;
import org.bukkit.configuration.ConfigurationSection;

/**
 * This is a test for {@link DelegateConfigurationSection} as a {@link ConfigurationSection}.
 */
public class DelegateConfigurationSectionToConfigurationWithConfigurationTest extends DelegateConfigurationSectionToConfigurationTest {
    /**
     * Empty constructor
     */
    public DelegateConfigurationSectionToConfigurationWithConfigurationTest() {
        super();
    }

    @Override
    public ConfigurationSection getConfig() {
        setter = new DelegateModificationToConfiguration();
        return new DelegateConfiguration(super.getDefaultConfig(), setter);
    }
}
