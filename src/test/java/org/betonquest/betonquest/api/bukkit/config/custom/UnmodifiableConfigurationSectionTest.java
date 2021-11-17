package org.betonquest.betonquest.api.bukkit.config.custom;

import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.CONCURRENT)
public class UnmodifiableConfigurationSectionTest {

    @Test
    public void testSet() {
        final UnmodifiableConfigurationSection unmodifiableConfigurationSection = new UnmodifiableConfigurationSection(new MemoryConfiguration());

        assertThrows(UnsupportedOperationException.class, () -> unmodifiableConfigurationSection.set("dsaf", "dsafdsf"),
                "UnmodifiableConfiguration allows modification!");
    }

}
