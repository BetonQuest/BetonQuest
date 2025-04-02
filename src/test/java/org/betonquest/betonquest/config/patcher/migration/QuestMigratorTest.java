package org.betonquest.betonquest.config.patcher.migration;

import org.betonquest.betonquest.config.quest.Quest;
import org.betonquest.betonquest.config.quest.QuestFixture;
import org.betonquest.betonquest.versioning.Version;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for Quest Migrations.
 */
class QuestMigratorTest extends QuestFixture {

    @Test
    void test_version_no_set() throws IOException, InvalidConfigurationException {
        final Quest quest = setupQuest();
        final Version currentVersion = new Version("3.2.1-QUEST-5");
        final QuestMigrator migrator = new QuestMigrator(logger, Collections.emptyList(), Collections.emptyMap(), currentVersion);
        migrator.migrate(quest);
        assertEquals(currentVersion.getVersion(), quest.getQuestConfig().getString("package.version"),
                "The version should be set in the quest if none is present.");
        assertEquals(currentVersion.getVersion(), loadPackageFile().getString("package.version"),
                "The version should be set in the file if none is present.");
    }

    @Test
    void test_version_older_set() throws IOException, InvalidConfigurationException {
        final Version qustVersion = new Version("2.2.1-QUEST-0");
        original.set("package.version", qustVersion.getVersion());
        final Quest quest = setupQuest();
        final Version currentVersion = new Version("3.0.0-QUEST-0");
        final QuestMigrator migrator = new QuestMigrator(logger, Collections.emptyList(), Collections.emptyMap(), currentVersion);
        migrator.migrate(quest);
        quest.saveAll();
        assertEquals(currentVersion.getVersion(), quest.getQuestConfig().getString("package.version"),
                "Older versions should be updated in the quest.");
        assertEquals(currentVersion.getVersion(), loadPackageFile().getString("package.version"),
                "Older versions should be updated in the file.");
    }

    @Test
    void test_version_newer_set() throws IOException, InvalidConfigurationException {
        final Version qustVersion = new Version("3.0.1-QUEST-0");
        original.set("package.version", qustVersion.getVersion());
        final Quest quest = setupQuest();
        final Version currentVersion = new Version("3.0.0-QUEST-0");
        final QuestMigrator migrator = new QuestMigrator(logger, Collections.emptyList(), Collections.emptyMap(), currentVersion);
        migrator.migrate(quest);
        assertEquals(qustVersion.getVersion(), quest.getQuestConfig().getString("package.version"),
                "Newer versions should not be changed in the quest.");
        assertEquals(qustVersion.getVersion(), loadPackageFile().getString("package.version"),
                "Newer versions should not be changed in the file.");
    }

    @Test
    void test_no_legacy_migration() throws IOException, InvalidConfigurationException {
        final Version qustVersion = new Version("3.0.0-QUEST-0");
        original.set("package.version", qustVersion.getVersion());
        final Quest quest = setupQuest();
        final Version currentVersion = new Version("3.0.0-QUEST-1");
        final QuestMigration mock = mock(QuestMigration.class);
        final QuestMigrator migrator = new QuestMigrator(logger, Collections.singletonList(mock), Collections.emptyMap(), currentVersion);
        migrator.migrate(quest);
        verifyNoInteractions(mock);
    }

    @Test
    void test_legacy_migration() throws IOException, InvalidConfigurationException {
        final Quest quest = setupQuest();
        final Version currentVersion = new Version("3.0.0-QUEST-1");
        final QuestMigration mock = mock(QuestMigration.class);
        final QuestMigrator migrator = new QuestMigrator(logger, Collections.singletonList(mock), Collections.emptyMap(), currentVersion);
        migrator.migrate(quest);
        verify(mock, times(1)).migrate(any());
    }

    @Test
    void test_select_migration() throws IOException, InvalidConfigurationException {
        final Version qustVersion = new Version("3.0.0-QUEST-0");
        original.set("package.version", qustVersion.getVersion());
        final Quest quest = setupQuest();
        final QuestMigration older = mock(QuestMigration.class);
        final QuestMigration newer = mock(QuestMigration.class);
        final Map<Version, QuestMigration> migrationMap = Map.of(
                new Version("2.3.4-QUEST-1"), older,
                new Version("3.0.1-QUEST-2"), newer
        );
        final Version currentVersion = new Version("3.2.0-QUEST-1");
        final QuestMigrator migrator = new QuestMigrator(logger, Collections.emptyList(), migrationMap, currentVersion);
        migrator.migrate(quest);
        verifyNoInteractions(older);
        verify(newer, times(1)).migrate(any());
    }
}
