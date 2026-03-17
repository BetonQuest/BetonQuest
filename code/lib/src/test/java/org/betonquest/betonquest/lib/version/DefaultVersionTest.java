package org.betonquest.betonquest.lib.version;

import org.betonquest.betonquest.api.version.Version;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test should only ensure, that the {@link DefaultVersion} does not break anything or select wrong versions
 * and push them into the production server.
 */
@SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
class DefaultVersionTest {

    /**
     * A version that represent a MAJOR Version.
     */
    private static final Version V_1_0_0 = BetonQuestVersion.parse("1.0.0");

    /**
     * A version that represent a DEV Version.
     */
    private static final Version V_1_0_0_DEV_1 = BetonQuestVersion.parse("1.0.0-DEV-1");

    /**
     * A version that represent a newer MINOR-DEV Version.
     */
    private static final Version V_1_1_0_DEV_146 = BetonQuestVersion.parse("1.1.0-DEV-146");

    /**
     * A version that represent a local PATCH Version.
     */
    private static final Version V_1_0_1_DEV_UNOFFICIAL = BetonQuestVersion.parse("1.0.1-DEV-UNOFFICIAL");

    /**
     * A version that represent a fork PATCH Version.
     */
    private static final Version V_1_0_1_ARTIFACT_55 = BetonQuestVersion.parse("1.0.1-DEV-ARTIFACT-Betonquest/Betonquest-55");

    /**
     * Test if the {@link DefaultVersion}'s have the correct string version.
     */
    @Test
    void testVersionString() {
        assertVersionString(V_1_0_0, "1.0.0");
        assertVersionString(V_1_0_0_DEV_1, "1.0.0-DEV-1");
        assertVersionString(V_1_1_0_DEV_146, "1.1.0-DEV-146");
        assertVersionString(V_1_0_1_DEV_UNOFFICIAL, "1.0.1-DEV-UNOFFICIAL");
        assertVersionString(V_1_0_1_ARTIFACT_55, "1.0.1-DEV-ARTIFACT-Betonquest/Betonquest-55");
    }

    /**
     * Test if the {@link DefaultVersion}'s have the correct qualifier.
     */
    @Test
    void testVersionQualifier() {
        assertVersionQualifier(V_1_0_0, "type", "RELEASE");
        assertVersionQualifier(V_1_0_0_DEV_1, "type", "DEV");
        assertVersionQualifier(V_1_1_0_DEV_146, "type", "DEV");
        assertVersionQualifier(V_1_0_1_DEV_UNOFFICIAL, "type", "DEV-UNOFFICIAL");
        assertVersionQualifier(V_1_0_1_ARTIFACT_55, "type", "DEV-ARTIFACT");
    }

    /**
     * Test if the {@link DefaultVersion}'s have the correct build number.
     */
    @Test
    void testVersionBuildNumber() {
        assertVersionQualifier(V_1_0_0, "build", "0");
        assertVersionQualifier(V_1_0_0_DEV_1, "build", "1");
        assertVersionQualifier(V_1_1_0_DEV_146, "build", "146");
        assertVersionQualifier(V_1_0_1_DEV_UNOFFICIAL, "build", "0");
        assertVersionQualifier(V_1_0_1_ARTIFACT_55, "run", "55");
    }

    private void assertVersionString(final Version expectedVersion, final String version) {
        assertEquals(version, expectedVersion.toString(), "Check Version on %s".formatted(version));
    }

    private void assertVersionQualifier(final Version expectedVersion, final String qualifierName, final String expectedQualifier) {
        assertEquals(expectedQualifier, expectedVersion.getNamedElement(qualifierName).orElse(null), "Check qualifier on %s".formatted(expectedVersion));
    }
}
