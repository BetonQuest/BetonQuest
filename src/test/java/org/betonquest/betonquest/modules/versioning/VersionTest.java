package org.betonquest.betonquest.modules.versioning;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test should only ensure, that the {@link Version} does not break anything or select wrong versions
 * and push them into the production server.
 */
@SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
class VersionTest {
    /**
     * A version that represent a MAJOR Version
     */
    private static final Version V_1_0_0 = new Version("1.0.0");

    /**
     * A version that represent a DEV Version
     */
    private static final Version V_1_0_0_DEV_1 = new Version("1.0.0-DEV-1");

    /**
     * A version that represent a newer MINOR-DEV Version
     */
    private static final Version V_1_1_0_DEV_146 = new Version("1.1.0-DEV-146");

    /**
     * A version that represent a local PATCH Version
     */
    private static final Version V_1_0_1_DEV_UNOFFICIAL = new Version("1.0.1-DEV-UNOFFICIAL");

    /**
     * A version that represent a fork PATCH Version
     */
    private static final Version V_1_0_1_ARTIFACT_1 = new Version("1.0.1-ARTIFACT-1");

    /**
     * A version that represent a PRE release version
     */
    private static final Version V_2_0_0_PRE_1 = new Version("2.0.0-PRE-1");

    /**
     * A version that represent a version without a qualifier but a build number
     */
    private static final Version V_1_1_1_99 = new Version("1.1.1-99");

    /**
     * Test if the {@link Version}'s have the correct string version
     */
    @Test
    void testVersionString() {
        assertVersionString(V_1_0_0, "1.0.0");
        assertVersionString(V_1_0_0_DEV_1, "1.0.0-DEV-1");
        assertVersionString(V_1_1_0_DEV_146, "1.1.0-DEV-146");
        assertVersionString(V_1_0_1_DEV_UNOFFICIAL, "1.0.1-DEV-UNOFFICIAL");
        assertVersionString(V_1_0_1_ARTIFACT_1, "1.0.1-ARTIFACT-1");
        assertVersionString(V_2_0_0_PRE_1, "2.0.0-PRE-1");
        assertVersionString(V_1_1_1_99, "1.1.1-99");
    }

    /**
     * Test if the {@link Version}'s have the correct qualifier
     */
    @Test
    void testVersionQualifier() {
        assertVersionQualifier(V_1_0_0, null);
        assertVersionQualifier(V_1_0_0_DEV_1, "DEV-");
        assertVersionQualifier(V_1_1_0_DEV_146, "DEV-");
        assertVersionQualifier(V_1_0_1_DEV_UNOFFICIAL, "DEV-UNOFFICIAL");
        assertVersionQualifier(V_1_0_1_ARTIFACT_1, "ARTIFACT-");
        assertVersionQualifier(V_2_0_0_PRE_1, "PRE-");
        assertVersionQualifier(V_1_1_1_99, "");
    }

    /**
     * Test if the {@link Version}'s have the correct build number
     */
    @Test
    void testVersionBuildNumber() {
        assertVersionBuildNumber(V_1_0_0, null);
        assertVersionBuildNumber(V_1_0_0_DEV_1, 1);
        assertVersionBuildNumber(V_1_1_0_DEV_146, 146);
        assertVersionBuildNumber(V_1_0_1_DEV_UNOFFICIAL, null);
        assertVersionBuildNumber(V_1_0_1_ARTIFACT_1, 1);
        assertVersionBuildNumber(V_2_0_0_PRE_1, 1);
        assertVersionBuildNumber(V_1_1_1_99, 99);
    }

    private void assertVersionString(final Version expectedVersion, final String version) {
        assertEquals(version, expectedVersion.getVersion(), "Check Version on " + version);
    }

    private void assertVersionQualifier(final Version expectedVersion, final String qualifier) {
        assertEquals(qualifier, expectedVersion.getQualifier(), "Check qualifier on " + expectedVersion.getVersion());
    }

    private void assertVersionBuildNumber(final Version expectedVersion, final Integer buildNumber) {
        assertEquals(buildNumber, expectedVersion.getBuildNumber(), "Check build number on " + expectedVersion.getVersion());
    }
}
