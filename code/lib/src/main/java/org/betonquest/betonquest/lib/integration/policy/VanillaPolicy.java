package org.betonquest.betonquest.lib.integration.policy;

import org.betonquest.betonquest.lib.versioning.MinecraftVersion;
import org.betonquest.betonquest.lib.versioning.Version;

/**
 * Represents a vanilla policy enforcing specific versioning rules based on a provided version and comparison strategy.
 * <p>
 * This policy is validated by testing the provided version against the Minecraft server's version
 * using {@link MinecraftVersion} and the specified {@link VersionCompareStrategy}.
 *
 * @param version                the version string to enforce
 * @param versionCompareStrategy the strategy to compare versions
 * @param description            the description of the policy
 */
public record VanillaPolicy(String version, VersionCompareStrategy versionCompareStrategy,
                            String description) implements VersionedPolicy {

    @Override
    public boolean validate() {
        return versionCompareStrategy.test(new MinecraftVersion(), new Version(version));
    }

    @Override
    public String name() {
        return "Minecraft";
    }
}
