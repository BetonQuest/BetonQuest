package org.betonquest.betonquest.lib.integration.policy;

import org.betonquest.betonquest.api.version.Version;
import org.betonquest.betonquest.lib.integration.PluginProvider;
import org.betonquest.betonquest.lib.version.BetonQuestVersion;

/**
 * Represents a policy that validates the compatibility with BetonQuest based on its version.
 * The presence of BetonQuest is not considered in this validation, as it has to be present for this code to be called.
 * <p>
 * This class implements the {@link VersionedPolicy} interface and provides a mechanism
 * for ensuring that the BetonQuest plugin is compatible with the required version.
 *
 * @param version                the required version of BetonQuest to be compatible
 * @param versionCompareStrategy the strategy for comparing the BetonQuest version with the required version
 * @param description            the description of the policy used for logging or debugging purposes
 */
public record BetonQuestPolicy(Version version, VersionCompareStrategy versionCompareStrategy,
                               String description) implements VersionedPolicy, PluginPolicy {

    @Override
    public boolean validate() {
        return pluginProvider().version().map(actual -> versionCompareStrategy.test(actual, version)).orElse(false);
    }

    @Override
    public String name() {
        return "BetonQuest";
    }

    @Override
    public PluginProvider pluginProvider() {
        return PluginProvider.forName(name()).withVersionType(BetonQuestVersion.BETONQUEST_VERSION_TYPE);
    }
}
