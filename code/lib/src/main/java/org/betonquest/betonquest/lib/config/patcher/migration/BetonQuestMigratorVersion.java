package org.betonquest.betonquest.lib.config.patcher.migration;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.section.multi.MultiConfiguration;
import org.betonquest.betonquest.api.version.Version;
import org.betonquest.betonquest.api.version.VersionType;
import org.betonquest.betonquest.lib.config.quest.Quest;
import org.betonquest.betonquest.lib.version.DefaultVersionType;
import org.betonquest.betonquest.lib.version.VersionParser;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.VisibleForTesting;

import java.io.IOException;
import java.util.List;

/**
 * A version which can also place it inside a Quest.
 *
 * @param wrappedVersion the wrapped version
 */
public record BetonQuestMigratorVersion(Version wrappedVersion) {

    /**
     * The version type for the quest package version.
     */
    @VisibleForTesting
    public static final VersionType QUEST_PACKAGE_VERSION_TYPE = DefaultVersionType.builder()
            .number("major")
            .dot().number("minor")
            .dot().number("patch")
            .dash().exact("quest", "QUEST")
            .dash().finite().number("version")
            .build();

    /**
     * Creates a new SettableVersion with a raw version string to parse and wrap.
     *
     * @param versionString the raw version string
     */
    public BetonQuestMigratorVersion(final String versionString) {
        this(VersionParser.parse(QUEST_PACKAGE_VERSION_TYPE, versionString));
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
        config.set(path, wrappedVersion().toString());
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
