package org.betonquest.betonquest.api.bukkit.config.custom;

import org.betonquest.betonquest.api.bukkit.config.util.AbstractConfigurationSectionTest;
import org.betonquest.betonquest.api.bukkit.config.util.DelegateSetToConfigurationSection;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.AfterEach;

import static org.junit.jupiter.api.Assertions.*;

public class DelegateSetConfigurationSectionTest extends AbstractConfigurationSectionTest {
    private DelegateSetToConfigurationSection setter;

    /**
     * Empty constructor
     */
    public DelegateSetConfigurationSectionTest() {
        super();
    }

    @Override
    public ConfigurationSection getConfig() {
        setter = new DelegateSetToConfigurationSection();
        return new DelegateSetConfigurationSection(super.getDefaultConfig(), setter);
    }

    @AfterEach
    public void afterEach() {
        assertTrue(setter.getSection().getKeys(true).isEmpty());
    }
}
