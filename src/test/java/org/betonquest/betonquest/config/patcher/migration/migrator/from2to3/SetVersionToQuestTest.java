package org.betonquest.betonquest.config.patcher.migration.migrator.from2to3;

import org.betonquest.betonquest.config.quest.Quest;
import org.betonquest.betonquest.config.quest.QuestFixture;
import org.betonquest.betonquest.versioning.Version;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the package version setting.
 */
class SetVersionToQuestTest extends QuestFixture {

    @Test
    void test_no_version_present() throws IOException, InvalidConfigurationException {
        final Quest quest = setupQuest(new YamlConfiguration());
        final Version version = new Version("1.2.3-QUEST-4");
        new SetVersionToQuest(version).migrate(quest);
        assertEquals(version.getVersion(), loadPackageFile().getString("package.version"),
                "Version should be set");
    }

    @Test
    void test_version_present() throws IOException, InvalidConfigurationException {
        final Version presentVersion = new Version("2.3.4-QUEST-5");
        final YamlConfiguration originalConfig = new YamlConfiguration();
        originalConfig.set("package.version", presentVersion.getVersion());
        final Quest quest = setupQuest(originalConfig);
        final Version version = new Version("1.2.3-QUEST-4");
        new SetVersionToQuest(version).migrate(quest);
        assertEquals(version.getVersion(), loadPackageFile().getString("package.version"),
                "The migrator should override the version");
    }
}
