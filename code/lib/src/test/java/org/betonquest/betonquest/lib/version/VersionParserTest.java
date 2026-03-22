package org.betonquest.betonquest.lib.version;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("PMD.AvoidUsingHardCodedIP")
class VersionParserTest {

    private static final Set<String> VALID_BQ_VERSIONS = Set.of("1.0.0", "1.2.5", "2.0.0", "2.2.0", "3.0.0",
            "1.0.0-DEV-55", "1.2.5-DEV-23", "2.0.0-DEV-334", "2.2.0-DEV-918", "3.0.0-DEV-1",
            "5.0.0-ARTIFACT-1", "5.0.0-PRE-1", "5.0.0-DEV-UNOFFICIAL", "1.0.0-55");

    private static final Set<String> INVALID_BQ_VERSIONS = Set.of("1.0", "1.0.", "3.0.0-dev-1.1", "1.0.0b", "3.0.0-dev.1",
            "1.2.5-DEV-1.1", "2.2.a", "2.2.0-SNAPSHOT", "2.2.0-SNAPSHOT-1", "2.2.0-alpha.1", "1.1.1.1", "1.0.0+dev-55",
            "1.0.0-DEV", "1.2.5-DEV", "2.0.0-DEV", "2.2.0-DEV", "3.0.0-DEV", "1.0.0-dev-", "2.2.0-rc.1",
            "3.0.0-UNOFFICIAL", "2.2.-DEV-50", "2.2.0-dev-1");

    private static final Set<String> VALID_MINECRAFT_VERSIONS = Set.of("1.18.2", "1.19.2", "1.20", "1.20.2", "1.21.2", "1.22.2");

    private static final Set<String> INVALID_MINECRAFT_VERSIONS = Set.of("5", "1.5.", "3.0.0-dev-1.1", "1.0.0b", "3.0.0-dev.1", "1.2.5-DEV-1.1",
            "2.2.a", "2.2.0-SNAPSHOT", "2.2.0-SNAPSHOT-1", "2.2.0-alpha.1", "1.1.1.1", "1.0.0+dev-55");

    private static Stream<Arguments> validBetonQuestVersions() {
        return VALID_BQ_VERSIONS.stream().map(version -> Arguments.of(BetonQuestVersion.BETONQUEST_VERSION_TYPE, version));
    }

    private static Stream<Arguments> invalidBetonQuestVersions() {
        return INVALID_BQ_VERSIONS.stream().map(version -> Arguments.of(BetonQuestVersion.BETONQUEST_VERSION_TYPE, version));
    }

    private static Stream<Arguments> validMinecraftVersions() {
        return VALID_MINECRAFT_VERSIONS.stream().map(version -> Arguments.of(MinecraftVersion.MINECRAFT_VERSION_TYPE, version));
    }

    private static Stream<Arguments> invalidMinecraftVersions() {
        return INVALID_MINECRAFT_VERSIONS.stream().map(version -> Arguments.of(MinecraftVersion.MINECRAFT_VERSION_TYPE, version));
    }

    @ParameterizedTest
    @MethodSource("validBetonQuestVersions")
    @MethodSource("validMinecraftVersions")
    void parse_valid_versions(final VersionType type, final String version) {
        assertDoesNotThrow(() -> VersionParser.parse(type, version), () -> String.format("Parsing version '%s' for type '%s' should not throw an exception", version, type));
    }

    @ParameterizedTest
    @MethodSource("invalidBetonQuestVersions")
    @MethodSource("invalidMinecraftVersions")
    void parse_invalid_versions(final VersionType type, final String version) {
        assertThrows(IllegalArgumentException.class, () -> VersionParser.parse(type, version), () -> String.format("Parsing version '%s' for type '%s' should throw an exception", version, type));
    }
}
