package org.betonquest.betonquest.config.patcher.migration;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.config.quest.Quest;
import org.betonquest.betonquest.versioning.Version;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;
import java.util.List;

/**
 * A version which can also place it inside a Quest.
 */
public class SettableVersion extends Version {
    /**
     * Creates a new Version.
     *
     * @param versionString The raw version string
     */
    public SettableVersion(final String versionString) {
        super(versionString);
    }

    /**
     * Sets this version.
     *
     * @param quest the quest to put the version in
     * @param path  the path to set the version at
     * @throws IOException when the version cannot be set
     */
    public void setVersion(final Quest quest, final String path) throws IOException {
        final MultiConfiguration config = quest.getQuestConfig();
        final boolean isSet = config.isSet(path);
        config.set(path, getVersion());
        config.setInlineComments(path, List.of("Don't change this! The plugin's automatic quest updater handles it."));
        if (!isSet) {
            try {
                final ConfigAccessor packageFile = quest.getOrCreateConfigAccessor("package.yml");
                config.associateWith(path, packageFile.getConfig());
            } catch (final InvalidConfigurationException e) {
                throw new IllegalStateException("Could not load package file: " + e.getMessage(), e);
            }
        }
    }
}
