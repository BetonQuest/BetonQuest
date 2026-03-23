package org.betonquest.betonquest.lib.integration.policy;

import org.betonquest.betonquest.api.integration.policy.Policy;
import org.betonquest.betonquest.api.version.Version;

/**
 * The VersionedPolicy interface represents a specialized type of {@link Policy} that enforces rules
 * or conditions based on versioning. Implementations of this interface are expected to provide
 * specific versioning constraints and define a strategy for comparing versions.
 */
public interface VersionedPolicy extends Policy {

    /**
     * Retrieves the name of this versioned element.
     *
     * @return the element name
     */
    String name();

    /**
     * Retrieves the {@link Version} associated with the policy.
     * The version is typically used to enforce or evaluate conditions
     * based on versioning constraints.
     *
     * @return the version identifier
     */
    Version version();

    /**
     * Retrieves the version comparison strategy used to determine compatibility between versions.
     * <p>
     * The comparison strategy defines how versions are evaluated by leveraging a specific compatibility rule.
     *
     * @return the version comparison strategy implemented by {@link VersionCompareStrategy} for
     * defining compatibility checks between versions.
     */
    VersionCompareStrategy versionCompareStrategy();
}
