package org.betonquest.betonquest.lib.versioning;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the Minecraft version.
 */
@SuppressWarnings("PMD.UselessOverridingMethod")
public class LegacyMinecraftVersion extends LegacyVersion {

    /**
     * The version comparator.
     */
    private final VersionComparator versionComparator;

    /**
     * Creates a new MinecraftVersion instance using {@link Bukkit#getServer()}
     * and {@link LegacyMinecraftVersion#LegacyMinecraftVersion(Server)}.
     */
    public LegacyMinecraftVersion() {
        this(Bukkit.getServer());
    }

    /**
     * Creates a new MinecraftVersion instance
     * and retrieves the version using {@link Server#getMinecraftVersion()}.
     * Uses {@link UpdateStrategy#PATCH} by default for the comparator.
     *
     * @param server the server instance the retrieve the version from
     */
    public LegacyMinecraftVersion(final Server server) {
        this(server.getMinecraftVersion(), new VersionComparator(UpdateStrategy.PATCH));
    }

    /**
     * Creates a new MinecraftVersion instance with a custom version string and comparator.
     *
     * @param versionString     the version string
     * @param versionComparator the version comparator
     */
    public LegacyMinecraftVersion(final String versionString, final VersionComparator versionComparator) {
        super(versionString);
        this.versionComparator = versionComparator;
    }

    /**
     * Compare the version with another version and decide which one is newer.
     *
     * @param other the other version to compare to
     * @return true if this version is newer than the other version
     */
    public boolean isNewerThan(final String other) {
        final LegacyVersion otherVersion = new LegacyVersion(other);
        return super.isNewerThan(versionComparator, otherVersion);
    }

    /**
     * Checks if this version is compatible with the other version based on the comparator.
     * A version is compatible if it is equal or newer than the other version.
     *
     * @param other the other version to compare to
     * @return true if this version is compatible with the other version
     */
    public boolean isCompatibleWith(final String other) {
        final LegacyVersion otherVersion = new LegacyVersion(other);
        return super.isCompatibleWith(versionComparator, otherVersion);
    }

    /**
     * Checks if this version is older than the other version based on the comparator.
     *
     * @param other the other version to compare to
     * @return true if this version is older than the other version
     */
    public boolean isOlderThan(final String other) {
        final LegacyVersion otherVersion = new LegacyVersion(other);
        return super.isOlderThan(versionComparator, otherVersion);
    }

    @Override
    public boolean equals(@Nullable final Object object) {
        return super.equals(object);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + versionComparator.hashCode();
        return result;
    }
}
