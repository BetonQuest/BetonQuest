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
import org.betonquest.betonquest.config.patcher.migration.migrator.from2to3.AddSimpleTypeToQuestItem;
import org.betonquest.betonquest.config.patcher.migration.migrator.from2to3.FolderTimeUnit;
import org.betonquest.betonquest.config.patcher.migration.migrator.from2to3.HeadOwnerMigrator;
import org.betonquest.betonquest.config.patcher.migration.migrator.from2to3.LanguageRename;
import org.betonquest.betonquest.config.patcher.migration.migrator.from2to3.ListNamesRenameToPlural;
import org.betonquest.betonquest.config.patcher.migration.migrator.from2to3.MoonPhaseRename;
import org.betonquest.betonquest.config.patcher.migration.migrator.from2to3.MoveMenuItems;
import org.betonquest.betonquest.config.patcher.migration.migrator.from2to3.NpcEventsRename;
import org.betonquest.betonquest.config.patcher.migration.migrator.from2to3.NpcRename;
import org.betonquest.betonquest.config.patcher.migration.migrator.from2to3.PackageSeparator;
import org.betonquest.betonquest.config.patcher.migration.migrator.from2to3.PickRandomPercentage;
import org.betonquest.betonquest.config.patcher.migration.migrator.from2to3.RemoveLegacyPointModification;
import org.betonquest.betonquest.config.patcher.migration.migrator.from2to3.RemoveStringList;
import org.betonquest.betonquest.config.patcher.migration.migrator.from2to3.VariablesRename;
import org.betonquest.betonquest.config.quest.Quest;
import org.betonquest.betonquest.versioning.UpdateStrategy;
import org.betonquest.betonquest.versioning.Version;
import org.betonquest.betonquest.versioning.VersionComparator;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.PluginDescriptionFile;
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
            this.migrations.put(new SettableVersion(entry.getKey().getVersion()), entry.getValue());
        }
        this.fallbackVersion = new SettableVersion(fallbackVersion.getVersion());
    }

    /**
     * Create a new Quest Migrator with BQ Migrations.
     *
     * @param log               the custom logger for the class
     * @param pluginDescription the PluginDescriptionFile containing a semantic version,
     *                          used as fallback when no migrator is applied
     */
    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    public QuestMigrator(final BetonQuestLogger log, final PluginDescriptionFile pluginDescription) {
        this.log = log;
        this.legacyMigrations = getLegacy();
        this.migrations = new TreeMap<>(VERSION_COMPARATOR);
        migrations.put(questVersion("3.0.0", 1), new LanguageRename());
        migrations.put(questVersion("3.0.0", 2), new NpcRename());
        migrations.put(questVersion("3.0.0", 3), new AddSimpleTypeToQuestItem());
        migrations.put(questVersion("3.0.0", 4), new ListNamesRenameToPlural());
        migrations.put(questVersion("3.0.0", 5), new PickRandomPercentage());
        migrations.put(questVersion("3.0.0", 6), new MoveMenuItems());
        migrations.put(questVersion("3.0.0", 7), new MoonPhaseRename());
        migrations.put(questVersion("3.0.0", 8), new RemoveStringList());
        migrations.put(questVersion("3.0.0", 9), new VariablesRename());
        migrations.put(questVersion("3.0.0", 10), new HeadOwnerMigrator());
        migrations.put(questVersion("3.0.0", 11), new NpcEventsRename());
        migrations.put(questVersion("3.0.0", 12), new FolderTimeUnit());
        migrations.put(questVersion("3.0.0", 13), new PackageSeparator());
        migrations.put(questVersion("3.0.0", 14), new RemoveLegacyPointModification());
        this.fallbackVersion = questVersion(pluginDescription.getVersion(), 0);
    }

    private SettableVersion questVersion(final String semanticVersion, final int number) {
        return new SettableVersion(semanticVersion + "-QUEST-" + number);
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
     * @throws VersionMissmatchException     when the Quest version is newer than the max settable version
     */
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.AvoidCatchingGenericException", "PMD.CognitiveComplexity"})
    public void migrate(final Quest quest) throws IOException, InvalidConfigurationException, VersionMissmatchException {
        log.debug("Attempting to migrate package '" + quest.getQuestPath() + "'");
        final String versionString = quest.getQuestConfig().getString(QUEST_VERSION_PATH);
        final SettableVersion lastVersionToSet = migrations.isEmpty() ? fallbackVersion : migrations.lastKey();
        if (versionString == null) {
            log.debug("  No version present, just setting to '" + lastVersionToSet + "'");
            lastVersionToSet.setVersion(quest, QUEST_VERSION_PATH);
            quest.saveAll();
            return;
        }

        final Map<SettableVersion, QuestMigration> actualMigrations;
        if (LEGACY.equalsIgnoreCase(versionString)) {
            log.debug("  Legacy identifier set, applying legacy and versioned migrations");
            for (final QuestMigration legacyMigration : legacyMigrations) {
                try {
                    legacyMigration.migrate(quest);
                } catch (final Exception e) {
                    throw new InvalidConfigurationException("Unexpected error while applying legacy migration: " + e.getMessage(), e);
                }
                quest.saveAll();
            }
            actualMigrations = migrations;
        } else {
            final SettableVersion otherVersion = new SettableVersion(versionString);
            if (lastVersionToSet.equals(otherVersion)) {
                log.debug("  Version '" + otherVersion + "' is up to date");
                return;
            }
            if (VERSION_COMPARATOR.isOtherNewerThanCurrent(lastVersionToSet, otherVersion)) {
                throw new VersionMissmatchException("The version '" + otherVersion
                        + "' is newer than the latest known version '" + lastVersionToSet + "'!\n"
                        + "Quests with newer versions will probably cause issues. If you know that won't be the case"
                        + " you can change the quest version to the latest known.");
            }
            log.debug("  Migrating from version '" + otherVersion + "' to '" + lastVersionToSet + "'");
            actualMigrations = migrations.tailMap(otherVersion, false);
        }

        if (actualMigrations.isEmpty()) {
            log.debug("  No newer migrations found, just setting version to '" + lastVersionToSet + "'");
            lastVersionToSet.setVersion(quest, QUEST_VERSION_PATH);
            quest.saveAll();
            return;
        }

        for (final Map.Entry<SettableVersion, QuestMigration> entry : actualMigrations.entrySet()) {
            try {
                entry.getValue().migrate(quest);
            } catch (final Exception e) {
                throw new InvalidConfigurationException("Unexpected error while applying migration '" + entry.getKey() + "': " + e.getMessage(), e);
            }
            entry.getKey().setVersion(quest, QUEST_VERSION_PATH);
            quest.saveAll();
        }
    }
}
