package org.betonquest.betonquest.config.patcher.migration;

import org.betonquest.betonquest.api.version.Version;
import org.betonquest.betonquest.config.quest.QuestFixture;
import org.betonquest.betonquest.lib.config.patcher.migration.BetonQuestMigratorVersion;
import org.betonquest.betonquest.lib.config.quest.Quest;
import org.betonquest.betonquest.lib.version.VersionParser;
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
        final BetonQuestMigratorVersion version = new BetonQuestMigratorVersion("1.2.3-QUEST-4");
        version.setVersion(quest, PACKAGE_VERSION);
        quest.saveAll();
        expected.set(PACKAGE_VERSION, version.wrappedVersion().toString());
        expected.setInlineComments(PACKAGE_VERSION,
                List.of("Don't change this! The plugin's automatic quest updater handles it."));
        checkAssertion(quest, "package.yml");
    }

    @Test
    void test_version_present() throws IOException, InvalidConfigurationException {
        final Version presentVersion = VersionParser.parse(BetonQuestMigratorVersion.QUEST_PACKAGE_VERSION_TYPE, "2.3.4-QUEST-5");
        original.set(PACKAGE_VERSION, presentVersion.toString());
        final Quest quest = setupQuest();
        final BetonQuestMigratorVersion version = new BetonQuestMigratorVersion("1.2.3-QUEST-4");
        version.setVersion(quest, PACKAGE_VERSION);
        quest.saveAll();
        expected.set(PACKAGE_VERSION, version.wrappedVersion().toString());
        expected.setInlineComments(PACKAGE_VERSION,
                List.of("Don't change this! The plugin's automatic quest updater handles it."));
        checkAssertion(quest, "package.yml");
    }
}
