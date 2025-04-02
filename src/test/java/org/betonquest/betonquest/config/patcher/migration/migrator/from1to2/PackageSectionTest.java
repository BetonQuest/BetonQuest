package org.betonquest.betonquest.config.patcher.migration.migrator.from1to2;

import org.betonquest.betonquest.config.quest.Quest;
import org.betonquest.betonquest.config.quest.QuestFixture;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for Package Section migration.
 */
class PackageSectionTest extends QuestFixture {

    @Test
    void test_notify_no_language() throws IOException, InvalidConfigurationException {
        final YamlConfiguration original = new YamlConfiguration();
        original.set("enabled", true);
        final Quest quest = setupQuest(original);
        new PackageSection().migrate(quest);
        assertEquals("""
                        package:
                          enabled: true
                        """, loadPackageFile().saveToString(),
                "The new package should only contain the new enabled section");
    }
}
