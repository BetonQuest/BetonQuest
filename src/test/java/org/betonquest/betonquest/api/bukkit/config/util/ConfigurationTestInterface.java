package org.betonquest.betonquest.api.bukkit.config.util;

public interface ConfigurationTestInterface extends ConfigurationSectionTestInterface {
    @Override
    void testAddDefault();

    @Override
    void testAddDefaultOnExistingConfigPath();

    void testAddDefaultsAsMap();

    void testAddDefaultsAsMapOnExistingConfigPath();

    void testAddDefaultsAsConfiguration();

    void testAddDefaultsAsConfigurationOnExistingConfigPath();

    void testGetDefaults();

    void testGetDefaultsOnInvalidConfigPath();

    void testSetDefaults();

    void testSetDefaultsOnExistingConfigPath();

    void testOptions();
}
