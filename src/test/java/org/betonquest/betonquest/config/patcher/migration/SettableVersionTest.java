package org.betonquest.betonquest.config.patcher.migration;

import org.betonquest.betonquest.config.quest.Quest;
import org.betonquest.betonquest.config.quest.QuestFixture;
import org.betonquest.betonquest.versioning.Version;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the package version setting.
 */
class SettableVersionTest extends QuestFixture {

    /**
     * Version path.
     */
    public static final String PACKAGE_VERSION = "package.version";

    @Test
    void test_no_version_present() throws IOException, InvalidConfigurationException {
        final Quest quest = setupQuest(new YamlConfiguration());
        final SettableVersion version = new SettableVersion("1.2.3-QUEST-4", PACKAGE_VERSION);
        version.setVersion(quest);
        quest.saveAll();
        assertEquals(quest.getQuestConfig().getKeys(true), Set.of("package", PACKAGE_VERSION),
                "Expected \n" + PACKAGE_VERSION + " but found \n" + quest.getQuestConfig().getKeys(true));
        assertEquals(version.getVersion(), loadPackageFile().getString(PACKAGE_VERSION),
                "Version should be set");
    }

    @Test
    void test_version_present() throws IOException, InvalidConfigurationException {
        final Version presentVersion = new Version("2.3.4-QUEST-5");
        final YamlConfiguration originalConfig = new YamlConfiguration();
        originalConfig.set(PACKAGE_VERSION, presentVersion.getVersion());
        final Quest quest = setupQuest(originalConfig);
        final SettableVersion version = new SettableVersion("1.2.3-QUEST-4", PACKAGE_VERSION);
        version.setVersion(quest);
        quest.saveAll();
        assertEquals(quest.getQuestConfig().getKeys(true), Set.of("package", PACKAGE_VERSION),
                "Expected " + PACKAGE_VERSION + " but found \n" + quest.getQuestConfig().getKeys(true));
        assertEquals(version.getVersion(), loadPackageFile().getString(PACKAGE_VERSION),
                "The migrator should override the version");
    }
}
