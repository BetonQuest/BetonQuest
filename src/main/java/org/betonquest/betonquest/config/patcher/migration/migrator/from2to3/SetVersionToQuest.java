package org.betonquest.betonquest.config.patcher.migration.migrator.from2to3;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.config.quest.Quest;
import org.betonquest.betonquest.versioning.Version;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;
import java.util.List;

/**
 * Migrator that sets the version.
 */
public class SetVersionToQuest implements QuestMigration {
    /**
     * Version to set.
     */
    private final Version version;

    /**
     * Create a Migrator that sets the current version into the package version field.
     *
     * @param version the version to set
     */
    public SetVersionToQuest(final Version version) {
        this.version = version;
    }

    @Override
    public void migrate(final Quest quest) throws IOException {
        final MultiConfiguration config = quest.getQuestConfig();
        final String path = "package.version";
        final boolean isSet = config.isSet(path);
        config.set(path, version.getVersion());
        config.setInlineComments(path, List.of("# Don't change this! The plugin's automatic quest updater handles it."));
        if (!isSet) {
            try {
                final ConfigAccessor packageFile = quest.getOrCreateConfigAccessor("package.yml");
                config.associateWith(path, packageFile.getConfig());
            } catch (final InvalidConfigurationException e) {
                throw new IllegalStateException("Could not load package file: " + e.getMessage(), e);
            }
        }
        quest.saveAll();
    }
}
