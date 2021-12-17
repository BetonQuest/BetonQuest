package org.betonquest.betonquest.api.bukkit.config.util;

@SuppressWarnings({"unused", "PMD.CommentRequired", "PMD.JUnit4TestShouldUseTestAnnotation", "PMD.TooManyMethods"})
public interface ConfigurationTestInterface {

    void testAddDefaultOnRootSection();

    void testAddDefaultOnRootSectionOnExistingConfigPath();

    void testAddDefaultsAsMap();

    void testAddDefaultsAsMapOnExistingConfigPath();

    void testAddDefaultsAsConfiguration();

    void testAddDefaultsAsConfigurationOnExistingConfigPath();

    void testGetDefaults();

    void testSetDefaults();

    void testSetDefaultsOnExistingConfigPath();

    void testOptions();
}
