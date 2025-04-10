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

import java.io.IOException;
import java.util.ArrayList;
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
    private final SettableVersion currentVersion;

    /**
     * Create a new Quest Migrator with custom migrations.
     *
     * @param log              the custom logger for this class
     * @param legacyMigrations the legacy migrations to apply if no version is set
     * @param migrations       the migrations by their version
     * @param currentVersion   the current version to set
     */
    protected QuestMigrator(final BetonQuestLogger log, final List<QuestMigration> legacyMigrations,
                            final Map<Version, QuestMigration> migrations, final Version currentVersion) {
        this.log = log;
        this.legacyMigrations = legacyMigrations;
        this.migrations = new TreeMap<>(VERSION_COMPARATOR);
        for (final Map.Entry<Version, QuestMigration> entry : migrations.entrySet()) {
            this.migrations.put(new SettableVersion(entry.getKey().getVersion(), QUEST_VERSION_PATH), entry.getValue());
        }
        this.currentVersion = new SettableVersion(currentVersion.getVersion(), QUEST_VERSION_PATH);
    }

    /**
     * Create a new Quest Migrator with BQ Migrations.
     *
     * @param log the custom logger for the class
     */
    public QuestMigrator(final BetonQuestLogger log) {
        this.log = log;
        this.legacyMigrations = getLegacy();
        this.migrations = new TreeMap<>(VERSION_COMPARATOR);
        final SettableVersion threeZeroZeroQuestOne = new SettableVersion("3.0.0-QUEST-1", QUEST_VERSION_PATH);
        migrations.put(threeZeroZeroQuestOne, new LanguageRename());
        final SettableVersion threeZeroZeroQuestTwo = new SettableVersion("3.0.0-QUEST-2", QUEST_VERSION_PATH);
        migrations.put(threeZeroZeroQuestTwo, new NpcRename());
        this.currentVersion = threeZeroZeroQuestTwo;
    }

    private List<QuestMigration> getLegacy() {
        final List<QuestMigration> legacy = new ArrayList<>();
        legacy.add(new EventScheduling());
        legacy.add(new PackageSection());
        legacy.add(new NpcHolograms());
        legacy.add(new EffectLib());
        legacy.add(new MmoUpdates());
        legacy.add(new RemoveEntity());
        legacy.add(new RideUpdates());
        legacy.add(new AuraSkillsRename());
        legacy.add(new FabledRename());
        return legacy;
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
        if (versionString == null) {
            log.debug("  No version present, applying legacy migrations and setting to '" + currentVersion.getVersion() + "'");
            for (final QuestMigration legacyMigration : legacyMigrations) {
                legacyMigration.migrate(quest);
            }
            currentVersion.setVersion(quest);
            quest.saveAll();
            return;
        }

        final SettableVersion otherVersion = new SettableVersion(versionString, QUEST_VERSION_PATH);
        if (VERSION_COMPARATOR.isOtherNewerOrEqualThanCurrent(currentVersion, otherVersion)) {
            log.debug("  Version '" + otherVersion + "' is up to date");
            return;
        }

        log.debug("  Migrating from version '" + otherVersion.getVersion() + "' to '" + currentVersion.getVersion() + "'");
        for (final Map.Entry<SettableVersion, QuestMigration> entry : migrations.tailMap(otherVersion, false).entrySet()) {
            entry.getValue().migrate(quest);
            entry.getKey().setVersion(quest);
            quest.saveAll();
        }

        currentVersion.setVersion(quest);
        quest.saveAll();
    }
}
