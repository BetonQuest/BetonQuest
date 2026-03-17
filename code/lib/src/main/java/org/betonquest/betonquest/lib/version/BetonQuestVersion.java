package org.betonquest.betonquest.lib.version;

import org.betonquest.betonquest.api.version.Version;
import org.betonquest.betonquest.api.version.VersionType;
import org.jetbrains.annotations.Contract;

import java.util.Comparator;
import java.util.List;

/**
 * This class provides predefined {@link DefaultVersionType} constants for standard BetonQuest version formats.
 */
public final class BetonQuestVersion {

    /**
     * The comparator used to compare version types.
     */
    private static final Comparator<String> TYPE_COMPARATOR = Comparator.comparing(value ->
            List.of("DEV-UNOFFICIAL", "DEV-ARTIFACT", "DEV", "RELEASE").indexOf(value));

    /**
     * The {@link VersionType} for BetonQuest standard versions.
     */
    public static final VersionType BETONQUEST_VERSION_TYPE = DefaultVersionType.builder()
            .number("major").dot()
            .number("minor")
            .opt()
            .dot().finite().number("patch")
            .opt()
            .dash().finite().exact("type", "DEV-UNOFFICIAL", TYPE_COMPARATOR)
            .opt()
            .dash().exact("type", "DEV-ARTIFACT", TYPE_COMPARATOR)
            .dash().exact("repository", "[^\\-]+")
            .dash().finite().number("run")
            .opt()
            .dash().exact("type", "DEV", TYPE_COMPARATOR, "RELEASE")
            .dash().finite().number("build", 0)
            .build();

    /**
     * Creates a new {@link BetonQuestVersion} instance.
     * <p>
     * <b>INFO:</b> This class is not meant to be instantiated.
     */
    private BetonQuestVersion() {
    }

    /**
     * Parse a version string into a {@link BetonQuestVersion} object using the BetonQuest version type.
     * Is equivalent to {@link VersionParser#parse(VersionType, String)} with {@link #BETONQUEST_VERSION_TYPE}.
     *
     * @param versionString the version string to parse
     * @return the parsed version
     * @throws IllegalArgumentException if the version string is invalid
     */
    @Contract(pure = true, value = "_ -> new")
    public static Version parse(final String versionString) {
        return VersionParser.parse(BETONQUEST_VERSION_TYPE, versionString);
    }
}
