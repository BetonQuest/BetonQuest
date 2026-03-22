package org.betonquest.betonquest.lib.version;

import org.betonquest.betonquest.api.version.Version;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;

/**
 * This class provides predefined {@link VersionType} constant for the standard Minecraft version format.
 */
public final class MinecraftVersion {

    /**
     * The {@link VersionType} for Minecraft versions.
     */
    public static final VersionType MINECRAFT_VERSION_TYPE = VersionType.builder()
            .number("major").dot()
            .finite().number("minor")
            .opt()
            .dot().finite().number("patch", 0)
            .build();

    /**
     * Creates a new {@link MinecraftVersion} instance.
     * <p>
     * <b>INFO:</b> This class is not meant to be instantiated.
     */
    private MinecraftVersion() {
    }

    /**
     * Parse a version string into a {@link DefaultVersion} object using the {@link #MINECRAFT_VERSION_TYPE} version type.
     *
     * @param version the version string to parse
     * @return the parsed version
     * @throws IllegalArgumentException if the version string is invalid
     */
    @Contract(pure = true, value = "_ -> new")
    public static Version parse(final String version) {
        return VersionParser.parse(MINECRAFT_VERSION_TYPE, version);
    }

    /**
     * Retrieves the current Minecraft version as a {@link Version} object.
     * <p>
     * This method parses the current Minecraft version string obtained from {@link Bukkit#getMinecraftVersion()}
     * using the {@link MinecraftVersion#MINECRAFT_VERSION_TYPE} format and returns it as a
     * properly structured {@link Version} instance.
     *
     * @return a {@link Version} object representing the current Minecraft version
     * @throws IllegalArgumentException if the current Minecraft version string cannot be parsed
     */
    @Contract(pure = true, value = " -> new")
    public static Version current() {
        return parse(Bukkit.getMinecraftVersion());
    }

    /**
     * Determines whether the current Minecraft version is compatible with the specified version.
     * <p>
     * The method uses the default version comparison strategy to check if the current version is compatible
     * with the provided Minecraft version string.
     *
     * @param version the version string to check for compatibility
     * @return {@code true} if the current Minecraft version is compatible with the specified version, {@code false} otherwise
     * @throws IllegalArgumentException if the provided version string cannot be parsed
     */
    @Contract(pure = true)
    public static boolean isCompatibleWith(final String version) {
        return current().isCompatibleWith(VersionComparisonStrategies.DEFAULT, parse(version));
    }
}
