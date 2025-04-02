package org.betonquest.betonquest.config.patcher.migration;

import org.betonquest.betonquest.config.quest.Quest;
import org.betonquest.betonquest.config.quest.QuestFixture;
import org.betonquest.betonquest.versioning.Version;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for Quest Migrations.
 */
class QuestMigratorTest extends QuestFixture {

    @Test
    void test_version_no_set() throws IOException, InvalidConfigurationException {
        final YamlConfiguration originalConfig = new YamlConfiguration();
        final Quest quest = setupQuest(originalConfig);
        final Version currentVersion = new Version("3.2.1-QUEST-5");
        final QuestMigrator migrator = new QuestMigrator(logger, Collections.emptyList(), Collections.emptyMap(), currentVersion);
        migrator.migrate(quest);
        assertEquals(currentVersion.getVersion(), loadPackageFile().getString("package.version"),
                "The version should be set if none is present.");
    }

    @Test
    void test_version_older_set() throws IOException, InvalidConfigurationException {
        final YamlConfiguration originalConfig = new YamlConfiguration();
        final Version qustVersion = new Version("2.2.1-QUEST-0");
        originalConfig.set("package.version", qustVersion.getVersion());
        final Quest quest = setupQuest(originalConfig);
        final Version currentVersion = new Version("3.0.0-QUEST-0");
        final QuestMigrator migrator = new QuestMigrator(logger, Collections.emptyList(), Collections.emptyMap(), currentVersion);
        migrator.migrate(quest);
        assertEquals(currentVersion.getVersion(), loadPackageFile().getString("package.version"),
                "Older versions should be updated.");
    }

    @Test
    void test_version_newer_set() throws IOException, InvalidConfigurationException {
        final YamlConfiguration originalConfig = new YamlConfiguration();
        final Version qustVersion = new Version("3.0.1-QUEST-0");
        originalConfig.set("package.version", qustVersion.getVersion());
        final Quest quest = setupQuest(originalConfig);
        final Version currentVersion = new Version("3.0.0-QUEST-0");
        final QuestMigrator migrator = new QuestMigrator(logger, Collections.emptyList(), Collections.emptyMap(), currentVersion);
        migrator.migrate(quest);
        assertEquals(qustVersion.getVersion(), loadPackageFile().getString("package.version"),
                "Newer versions should not be changed.");
    }

    @Test
    void test_no_legacy_migration() throws IOException, InvalidConfigurationException {
        final YamlConfiguration originalConfig = new YamlConfiguration();
        final Version qustVersion = new Version("3.0.0-QUEST-0");
        originalConfig.set("package.version", qustVersion.getVersion());
        final Quest quest = setupQuest(originalConfig);
        final Version currentVersion = new Version("3.0.0-QUEST-1");
        final QuestMigration mock = mock(QuestMigration.class);
        final QuestMigrator migrator = new QuestMigrator(logger, Collections.singletonList(mock), Collections.emptyMap(), currentVersion);
        migrator.migrate(quest);
        verifyNoInteractions(mock);
    }

    @Test
    void test_legacy_migration() throws IOException, InvalidConfigurationException {
        final YamlConfiguration originalConfig = new YamlConfiguration();
        final Quest quest = setupQuest(originalConfig);
        final Version currentVersion = new Version("3.0.0-QUEST-1");
        final QuestMigration mock = mock(QuestMigration.class);
        final QuestMigrator migrator = new QuestMigrator(logger, Collections.singletonList(mock), Collections.emptyMap(), currentVersion);
        migrator.migrate(quest);
        verify(mock, times(1)).migrate(any());
    }

    @Test
    void test_select_migration() throws IOException, InvalidConfigurationException {
        final YamlConfiguration originalConfig = new YamlConfiguration();
        final Version qustVersion = new Version("3.0.0-QUEST-0");
        originalConfig.set("package.version", qustVersion.getVersion());
        final Quest quest = setupQuest(originalConfig);
        final QuestMigration older = mock(QuestMigration.class);
        final QuestMigration newer = mock(QuestMigration.class);
        final Map<Version, List<QuestMigration>> migrationMap = Map.of(
                new Version("2.3.4-QUEST-1"), Collections.singletonList(older),
                new Version("3.0.1-QUEST-2"), Collections.singletonList(newer)
        );
        final Version currentVersion = new Version("3.2.0-QUEST-1");
        final QuestMigrator migrator = new QuestMigrator(logger, Collections.emptyList(), migrationMap, currentVersion);
        migrator.migrate(quest);
        verifyNoInteractions(older);
        verify(newer, times(1)).migrate(any());
    }
}
