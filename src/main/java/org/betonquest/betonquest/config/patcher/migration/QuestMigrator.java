package org.betonquest.betonquest.config.patcher.migration;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.config.patcher.migration.migrator.from2to3.SetVersionToQuestTemplate;
import org.betonquest.betonquest.config.quest.Quest;
import org.betonquest.betonquest.versioning.UpdateStrategy;
import org.betonquest.betonquest.versioning.Version;
import org.betonquest.betonquest.versioning.VersionComparator;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;
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
     * The migrations by their version.
     */
    private final NavigableMap<Version, QuestMigration> migrations;

    /**
     * The newest package version.
     */
    private final Version currentVersion;

    /**
     * To set the current package version.
     */
    private final SetVersionToQuestTemplate versionSet;

    /**
     * Create a new Quest Migrator.
     *
     * @param log the custom logger for the class
     */
    public QuestMigrator(final BetonQuestLogger log) {
        this.log = log;
        this.migrations = new TreeMap<>(VERSION_COMPARATOR);
        final String threeZeroZero = "3.0.0-QUEST-";
        final int threeZeroMigrations = 0;
        this.currentVersion = new Version(threeZeroZero + threeZeroMigrations);
        this.versionSet = new SetVersionToQuestTemplate(currentVersion);
    }

    /**
     * Updates the Quest to the newest version.
     *
     * @param quest the Quest to update
     * @throws InvalidConfigurationException when an error occurs
     */
    public void migrate(final Quest quest) throws InvalidConfigurationException {
        log.debug("Attempting to migrate package '" + quest.getQuestPath() + "'");
        final String versionString = quest.getQuestConfig().getString(QUEST_VERSION_PATH);
        if (versionString == null) {
            log.debug("  No version present, setting to '" + currentVersion.getVersion() + "'");
            versionSet.migrate(quest);
            try {
                quest.saveAll();
            } catch (final IOException e) {
                throw new InvalidConfigurationException(e.getMessage(), e);
            }
            return;
        }

        final Version otherVersion = new Version(versionString);
        if (VERSION_COMPARATOR.isOtherNewerOrEqualThanCurrent(currentVersion, otherVersion)) {
            log.debug("  Version '" + otherVersion + "' is up to date");
            return;
        }

        log.debug("  Migrating from version '" + otherVersion.getVersion() + "' to '" + currentVersion.getVersion() + "'");
        for (final Map.Entry<Version, QuestMigration> entry : migrations.tailMap(otherVersion, false).entrySet()) {
            entry.getValue().migrate(quest);
        }

        versionSet.migrate(quest);
        try {
            quest.saveAll();
        } catch (final IOException e) {
            throw new InvalidConfigurationException(e.getMessage(), e);
        }
    }
}
