package org.betonquest.betonquest.config.patcher.migration;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.config.patcher.migration.migrator.from1to2.AuraSkillsRename;
import org.betonquest.betonquest.config.patcher.migration.migrator.from1to2.EffectLib;
import org.betonquest.betonquest.config.patcher.migration.migrator.from1to2.EventScheduling;
import org.betonquest.betonquest.config.patcher.migration.migrator.from1to2.FabledRename;
import org.betonquest.betonquest.config.patcher.migration.migrator.from1to2.MmoUpdates;
import org.betonquest.betonquest.config.patcher.migration.migrator.from1to2.NpcHolograms;
import org.betonquest.betonquest.config.patcher.migration.migrator.from1to2.PackageSection;
import org.betonquest.betonquest.config.patcher.migration.migrator.from1to2.RemoveEntity;
import org.betonquest.betonquest.config.patcher.migration.migrator.from1to2.RideUpdates;
import org.betonquest.betonquest.config.patcher.migration.migrator.from2to3.LanguageRename;
import org.betonquest.betonquest.config.patcher.migration.migrator.from2to3.NpcRename;
import org.betonquest.betonquest.config.quest.Quest;
import org.betonquest.betonquest.versioning.UpdateStrategy;
import org.betonquest.betonquest.versioning.Version;
import org.betonquest.betonquest.versioning.VersionComparator;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.VisibleForTesting;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Migrates {@link Quest}s by Versions.
 */
public class QuestMigrator {
    /**
     * Comparator for {@link Version} with the qualifier QUEST.
     */
    private static final VersionComparator VERSION_COMPARATOR = new VersionComparator(UpdateStrategy.MAJOR, "QUEST-");

    /**
     * The path to the quest's version in the package.
     */
    private static final String QUEST_VERSION_PATH = "package.version";

    /**
     * "Version" String indicating that all migrations should be applied.
     */
    private static final String LEGACY = "legacy";

    /**
     * Custom logger for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The legacy migrations.
     */
    private final List<QuestMigration> legacyMigrations;

    /**
     * The migrations by their version.
     */
    private final NavigableMap<SettableVersion, QuestMigration> migrations;

    /**
     * The newest package version.
     */
    private final SettableVersion fallbackVersion;

    /**
     * Create a new Quest Migrator with custom migrations.
     *
     * @param log              the custom logger for this class
     * @param legacyMigrations the legacy migrations to apply if no version is set
     * @param migrations       the migrations by their version
     * @param fallbackVersion  the version to set if no migrator is applied
     */
    @VisibleForTesting
    QuestMigrator(final BetonQuestLogger log, final List<QuestMigration> legacyMigrations,
                  final Map<Version, QuestMigration> migrations, final Version fallbackVersion) {
        this.log = log;
        this.legacyMigrations = legacyMigrations;
        this.migrations = new TreeMap<>(VERSION_COMPARATOR);
        for (final Map.Entry<Version, QuestMigration> entry : migrations.entrySet()) {
            this.migrations.put(questVersion(entry.getKey().getVersion()), entry.getValue());
        }
        this.fallbackVersion = questVersion(fallbackVersion.getVersion());
    }

    /**
     * Create a new Quest Migrator with BQ Migrations.
     *
     * @param log     the custom logger for the class
     * @param version the plugin version, used as fallback when no migrator is applied
     */
    public QuestMigrator(final BetonQuestLogger log, final String version) {
        this.log = log;
        this.legacyMigrations = getLegacy();
        this.migrations = new TreeMap<>(VERSION_COMPARATOR);
        migrations.put(questVersion("3.0.0-QUEST-1"), new LanguageRename());
        migrations.put(questVersion("3.0.0-QUEST-2"), new NpcRename());
        this.fallbackVersion = questVersion(version + "QUEST-0");
    }

    private SettableVersion questVersion(final String version) {
        return new SettableVersion(version, QUEST_VERSION_PATH);
    }

    private List<QuestMigration> getLegacy() {
        return List.of(
                new EventScheduling(),
                new PackageSection(),
                new NpcHolograms(),
                new EffectLib(),
                new MmoUpdates(),
                new RemoveEntity(),
                new RideUpdates(),
                new AuraSkillsRename(),
                new FabledRename()
        );
    }

    /**
     * Updates the Quest to the newest version.
     *
     * @param quest the Quest to update
     * @throws InvalidConfigurationException when an error occurs
     * @throws IOException                   when an error occurs
     */
    public void migrate(final Quest quest) throws IOException, InvalidConfigurationException {
        log.debug("Attempting to migrate package '" + quest.getQuestPath() + "'");
        final String versionString = quest.getQuestConfig().getString(QUEST_VERSION_PATH);
        final SettableVersion lastVersionToSet = migrations.isEmpty() ? fallbackVersion : migrations.lastKey();
        if (versionString == null) {
            log.debug("  No version present, just setting to '" + lastVersionToSet.getVersion() + "'");
            lastVersionToSet.setVersion(quest);
            quest.saveAll();
            return;
        }

        final Map<SettableVersion, QuestMigration> actualMigrations;
        if (LEGACY.equalsIgnoreCase(versionString)) {
            log.debug("  Legacy identifier set, applying legacy and versioned migrations");
            for (final QuestMigration legacyMigration : legacyMigrations) {
                legacyMigration.migrate(quest);
                quest.saveAll();
            }
            actualMigrations = migrations;
        } else {
            final SettableVersion otherVersion = questVersion(versionString);
            if (VERSION_COMPARATOR.isOtherNewerOrEqualThanCurrent(lastVersionToSet, otherVersion)) {
                log.debug("  Version '" + otherVersion + "' is up to date");
                return;
            }
            log.debug("  Migrating from version '" + otherVersion.getVersion() + "' to '" + lastVersionToSet.getVersion() + "'");
            actualMigrations = migrations.tailMap(otherVersion, false);
        }

        if (actualMigrations.isEmpty()) {
            lastVersionToSet.setVersion(quest);
            quest.saveAll();
            return;
        }

        for (final Map.Entry<SettableVersion, QuestMigration> entry : actualMigrations.entrySet()) {
            entry.getValue().migrate(quest);
            entry.getKey().setVersion(quest);
            quest.saveAll();
        }
    }
}
