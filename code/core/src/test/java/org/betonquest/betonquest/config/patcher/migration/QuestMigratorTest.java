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

    /**
     * Version path.
     */
    private static final String VERSION_PATH = "package.version";

    @Test
    void version_no_set() throws IOException, InvalidConfigurationException, VersionMissmatchException {
        final Quest quest = setupQuest();
        final Version currentVersion = new Version("3.2.1-QUEST-5");
        new QuestMigrator(logger, Collections.emptyList(), Collections.emptyMap(), currentVersion).migrate(quest);
        assertEquals(currentVersion.getVersion(), quest.getQuestConfig().getString(VERSION_PATH),
                "The version should be set in the quest if none is present.");
        assertEquals(currentVersion.getVersion(), loadPackageFile().getString(VERSION_PATH),
                "The version should be set in the file if none is present.");
    }

    @Test
    void version_older_set() throws IOException, InvalidConfigurationException, VersionMissmatchException {
        original.set(VERSION_PATH, "2.2.1-QUEST-0");
        final Quest quest = setupQuest();
        final Version fallback = new Version("3.0.0-QUEST-0");
        new QuestMigrator(logger, Collections.emptyList(), Collections.emptyMap(), fallback).migrate(quest);
        assertEquals(fallback.getVersion(), quest.getQuestConfig().getString(VERSION_PATH),
                "Older versions should be updated in the quest.");
        assertEquals(fallback.getVersion(), loadPackageFile().getString(VERSION_PATH),
                "Older versions should be updated in the file.");
    }

    @Test
    void version_newer_set() throws IOException, InvalidConfigurationException {
        final Version qustVersion = new Version("3.0.1-QUEST-0");
        original.set(VERSION_PATH, qustVersion.getVersion());
        final Quest quest = setupQuest();
        final Version fallback = new Version("3.0.0-QUEST-0");
        final QuestMigrator migrator = new QuestMigrator(logger, Collections.emptyList(), Collections.emptyMap(), fallback);
        assertThrows(VersionMissmatchException.class, () -> migrator.migrate(quest),
                "Newer versions in quest than migrator should throw");
    }

    @Test
    void no_legacy_migration() throws IOException, InvalidConfigurationException, VersionMissmatchException {
        original.set(VERSION_PATH, "3.0.0-QUEST-0");
        final Quest quest = setupQuest();
        final Version fallback = new Version("3.0.0-QUEST-1");
        final QuestMigration mock = mock(QuestMigration.class);
        new QuestMigrator(logger, Collections.singletonList(mock), Collections.emptyMap(), fallback).migrate(quest);
        verifyNoInteractions(mock);
    }

    @Test
    void legacy_migration() throws IOException, InvalidConfigurationException, VersionMissmatchException {
        original.set(VERSION_PATH, "legacy");
        final Quest quest = setupQuest();
        final Version fallback = new Version("3.0.0-QUEST-1");
        final QuestMigration legacy = mock(QuestMigration.class);
        final QuestMigration versioned = mock(QuestMigration.class);
        final Map<Version, QuestMigration> migrationMap = Map.of(new Version("2.3.4-QUEST-7"), versioned);
        new QuestMigrator(logger, Collections.singletonList(legacy), migrationMap, fallback).migrate(quest);
        verify(legacy, times(1)).migrate(any());
        verify(versioned, times(1)).migrate(any());
    }

    @Test
    void select_migration() throws IOException, InvalidConfigurationException, VersionMissmatchException {
        original.set(VERSION_PATH, "3.0.0-QUEST-0");
        final Quest quest = setupQuest();
        final QuestMigration older = mock(QuestMigration.class);
        final QuestMigration newer = mock(QuestMigration.class);
        final Version newestMigrationVersion = new Version("3.0.1-QUEST-2");
        final Map<Version, QuestMigration> migrationMap = Map.of(
                new Version("2.3.4-QUEST-1"), older,
                newestMigrationVersion, newer
        );
        final Version fallback = new Version("3.2.0-QUEST-1");
        new QuestMigrator(logger, Collections.emptyList(), migrationMap, fallback).migrate(quest);
        verifyNoInteractions(older);
        verify(newer, times(1)).migrate(any());
    }

    @Test
    void set_migration_version() throws IOException, InvalidConfigurationException, VersionMissmatchException {
        original.set(VERSION_PATH, "3.0.0-QUEST-0");
        final Version migrationVersion = new Version("3.0.1-QUEST-2");
        final Map<Version, QuestMigration> migrationMap = Map.of(migrationVersion, mock(QuestMigration.class));
        final Version fallback = new Version("3.2.0-QUEST-1");
        final Quest quest = setupQuest();
        new QuestMigrator(logger, Collections.emptyList(), migrationMap, fallback).migrate(quest);
        assertEquals(migrationVersion.getVersion(), quest.getQuestConfig().getString(VERSION_PATH),
                "Fallback version should not be set with existing migrations.");
    }
}
