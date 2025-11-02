package org.betonquest.betonquest.config.patcher.migration.migrator.from1to2;

import org.betonquest.betonquest.config.quest.Quest;
import org.betonquest.betonquest.config.quest.QuestFixture;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Test for Package Section migration.
 */
class PackageSectionTest extends QuestFixture {

    @Test
    void migrate() throws IOException, InvalidConfigurationException {
        original.set("enabled", true);
        final Quest quest = setupQuest();
        new PackageSection().migrate(quest);
        quest.saveAll();
        expected.set("package.enabled", true);
        checkAssertion(quest, "package.yml");
    }
}
