package org.betonquest.betonquest.config.patcher.migration;

import org.betonquest.betonquest.config.quest.Quest;
import org.betonquest.betonquest.config.quest.QuestFixture;
import org.betonquest.betonquest.versioning.Version;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

/**
 * Tests the package version setting.
 */
class SettableVersionTest extends QuestFixture {

    /**
     * Version path.
     */
    private static final String PACKAGE_VERSION = "package.version";

    @Test
    void test_no_version_present() throws IOException, InvalidConfigurationException {
        final Quest quest = setupQuest();
        final SettableVersion version = new SettableVersion("1.2.3-QUEST-4");
        version.setVersion(quest, PACKAGE_VERSION);
        quest.saveAll();
        expected.set(PACKAGE_VERSION, version.getVersion());
        expected.setInlineComments(PACKAGE_VERSION,
                List.of("Don't change this! The plugin's automatic quest updater handles it."));
        checkAssertion(quest, "package.yml");
    }

    @Test
    void test_version_present() throws IOException, InvalidConfigurationException {
        final Version presentVersion = new Version("2.3.4-QUEST-5");
        original.set(PACKAGE_VERSION, presentVersion.getVersion());
        final Quest quest = setupQuest();
        final SettableVersion version = new SettableVersion("1.2.3-QUEST-4");
        version.setVersion(quest, PACKAGE_VERSION);
        quest.saveAll();
        expected.set(PACKAGE_VERSION, version.getVersion());
        expected.setInlineComments(PACKAGE_VERSION,
                List.of("Don't change this! The plugin's automatic quest updater handles it."));
        checkAssertion(quest, "package.yml");
    }
}
