package org.betonquest.betonquest.api.bukkit.config.util;

@SuppressWarnings({"unused", "PMD.CommentRequired", "PMD.JUnit4TestShouldUseTestAnnotation", "PMD.TooManyMethods"})
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
