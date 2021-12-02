package org.betonquest.betonquest.api.bukkit.config.custom;

import org.betonquest.betonquest.api.bukkit.config.util.AbstractConfigurationTest;
import org.bukkit.configuration.Configuration;
import org.junit.jupiter.api.Disabled;

@Disabled
public class UnmodifiableConfigurationTest extends AbstractConfigurationTest {
    /**
     * Empty constructor
     */
    public UnmodifiableConfigurationTest() {
        super();
    }

    @Override
    public Configuration getConfig() {
        return new UnmodifiableConfiguration((Configuration) super.getConfig());
    }
}
