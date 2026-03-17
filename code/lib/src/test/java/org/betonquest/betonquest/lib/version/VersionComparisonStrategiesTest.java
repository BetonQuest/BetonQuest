package org.betonquest.betonquest.lib.version;

import org.betonquest.betonquest.api.version.Version;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class VersionComparisonStrategiesTest {

    private static final DefaultVersionType SIMPLE_VERSION_TYPE = DefaultVersionType.builder()
            .number("major")
            .dot().number("minor")
            .opt()
            .dot().finite().number("patch", 0)
            .opt()
            .dash().finite().exact("snapshot", "SNAPSHOT")
            .build();

    private static Version[] generateVersions(final String... versions) {
        final Version[] versionArray = new DefaultVersion[versions.length];
        for (int i = 0; i < versions.length; i++) {
            versionArray[i] = VersionParser.parse(SIMPLE_VERSION_TYPE, versions[i]);
        }
        return versionArray;
    }

    @Test
    void ensure_versions_are_sorted_default() {
        final String[] versionStrings = {"1.2.3", "1.3", "1.3.5-SNAPSHOT", "1.3.5", "1.4.1", "2.0", "3.0-SNAPSHOT", "3.0.1-SNAPSHOT", "3.1"};
        final Version[] versions = generateVersions(versionStrings);
        for (int i = 0; i < versions.length - 1; i++) {
            assertTrue(versions[i].compareTo(versions[i + 1]) < 0,
                    "Version %s should be less than %s".formatted(versions[i], versions[i + 1]));
        }
    }

    @Test
    void ensure_versions_are_sorted_only_compare_minor() {
        final String[] versionStrings = {"1.2.3", "1.3", "1.4.5-SNAPSHOT", "1.5.5", "1.6.1", "2.7", "1.8-SNAPSHOT", "3.9.1-SNAPSHOT"};
        final Version[] versions = generateVersions(versionStrings);
        for (int i = 0; i < versions.length - 1; i++) {
            assertTrue(VersionComparisonStrategies.onlyCompare(Set.of("minor")).compare(versions[i], versions[i + 1]) < 0,
                    "Minor version of %s should be less than %s".formatted(versions[i], versions[i + 1]));
        }
    }

    @Test
    void ensure_versions_are_sorted_only_compare_compare_until_patch() {
        final String[] versionStrings = {"1.2.3", "1.3", "1.3.5-SNAPSHOT", "1.3.6", "1.3.8-SNAPSHOT", "1.4.1", "2.0", "3.0-SNAPSHOT", "3.0.1-SNAPSHOT", "3.1"};
        final Version[] versions = generateVersions(versionStrings);
        for (int i = 0; i < versions.length - 1; i++) {
            assertTrue(VersionComparisonStrategies.limitedMostSignificantDigit("patch").compare(versions[i], versions[i + 1]) < 0,
                    "Major, minor, patch - version %s should be less than %s".formatted(versions[i], versions[i + 1]));
        }
    }

    @Test
    void version_compare_with_snapshot() {
        final Version version = VersionParser.parse(SIMPLE_VERSION_TYPE, "1.1.0");
        final Version otherVersion = VersionParser.parse(SIMPLE_VERSION_TYPE, "1.1.0-SNAPSHOT");
        assertEquals(0, VersionComparisonStrategies.limitedMostSignificantDigit("patch").compare(version, otherVersion), "Snapshot should be ignored");
        assertEquals(1, VersionComparisonStrategies.DEFAULT.compare(version, otherVersion), "Snapshot should not be ignored");
    }

    @Test
    void version_compare_without_snapshot() {
        final Version version = VersionParser.parse(SIMPLE_VERSION_TYPE, "1.1.0");
        final Version otherVersion = VersionParser.parse(SIMPLE_VERSION_TYPE, "1.1.0");
        assertEquals(0, VersionComparisonStrategies.limitedMostSignificantDigit("snapshot").compare(version, otherVersion), "Version should be equal without snapshot");
        assertEquals(0, VersionComparisonStrategies.DEFAULT.compare(version, otherVersion), "Version should be equal without snapshot");
    }

    @Test
    void version_mismatch_should_fail() {
        final Version version = VersionParser.parse(SIMPLE_VERSION_TYPE, "1.1.0");
        final Version otherVersion = VersionParser.parse(DefaultVersionType.SIMPLE_SEMANTIC_VERSION, "1.1.0");
        assertThrows(IllegalArgumentException.class, () -> VersionComparisonStrategies.DEFAULT.compare(version, otherVersion), "Version should not be equal because of mismatching types");
    }
}
