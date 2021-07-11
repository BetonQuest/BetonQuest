package org.betonquest.betonquest.utils;

import org.betonquest.betonquest.utils.versioning.UpdateStrategy;
import org.betonquest.betonquest.utils.versioning.Version;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test should only ensure, that the {@link Version} dose not break anything or select wrong versions
 * and push them into the production server.
 */
@SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
public class VersionTest {
    /**
     * A version that represent a MAJOR Version
     */
    private static final Version V_1_0_0 = new Version("1.0.0");
    /**
     * A version that represent a newer MAJOR Version
     */
    private static final Version V_2_0_0 = new Version("2.0.0");
    /**
     * A version that represent a newer MINOR Version
     */
    private static final Version V_1_1_0 = new Version("1.1.0");
    /**
     * A version that represent a newer PATCH Version
     */
    private static final Version V_1_0_1 = new Version("1.0.1");
    /**
     * A version that represent a DEV Version
     */
    private static final Version V_1_0_0_DEV_1 = new Version("1.0.0-DEV-1");
    /**
     * A version that represent a newer DEV Version
     */
    private static final Version V_1_0_0_DEV_2 = new Version("1.0.0-DEV-2");
    /**
     * A version that represent a newer MAJOR-DEV Version
     */
    private static final Version V_2_0_0_DEV_1 = new Version("2.0.0-DEV-1");
    /**
     * A version that represent a newer MINOR-DEV Version
     */
    private static final Version V_1_1_0_DEV_146 = new Version("1.1.0-DEV-146");
    /**
     * A version that represent a newer PATCH-DEV Version
     */
    private static final Version V_1_0_1_DEV_1 = new Version("1.0.1-DEV-1");
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
     * Empty constructor
     */
    public VersionTest() {
    }

    /**
     * Test if the {@link Version}'s have the correct string version
     */
    @Test
    public void testVersionString() {
        assertVersionString(V_1_0_0, "1.0.0");
        assertVersionString(V_2_0_0, "2.0.0");
        assertVersionString(V_1_1_0, "1.1.0");
        assertVersionString(V_1_0_1, "1.0.1");
        assertVersionString(V_1_0_0_DEV_1, "1.0.0-DEV-1");
        assertVersionString(V_1_0_0_DEV_2, "1.0.0-DEV-2");
        assertVersionString(V_2_0_0_DEV_1, "2.0.0-DEV-1");
        assertVersionString(V_1_1_0_DEV_146, "1.1.0-DEV-146");
        assertVersionString(V_1_0_1_DEV_1, "1.0.1-DEV-1");
        assertVersionString(V_1_0_1_DEV_UNOFFICIAL, "1.0.1-DEV-UNOFFICIAL");
        assertVersionString(V_1_0_1_ARTIFACT_1, "1.0.1-ARTIFACT-1");
        assertVersionString(V_2_0_0_PRE_1, "2.0.0-PRE-1");
        assertVersionString(V_1_1_1_99, "1.1.1-99");
    }

    /**
     * Test if the {@link Version}'s have the correct qualifier
     */
    @Test
    public void testVersionQualifier() {
        assertVersionQualifier(V_1_0_0, null);
        assertVersionQualifier(V_2_0_0, null);
        assertVersionQualifier(V_1_1_0, null);
        assertVersionQualifier(V_1_0_1, null);
        assertVersionQualifier(V_1_0_0_DEV_1, "DEV-");
        assertVersionQualifier(V_1_0_0_DEV_2, "DEV-");
        assertVersionQualifier(V_2_0_0_DEV_1, "DEV-");
        assertVersionQualifier(V_1_1_0_DEV_146, "DEV-");
        assertVersionQualifier(V_1_0_1_DEV_1, "DEV-");
        assertVersionQualifier(V_1_0_1_DEV_UNOFFICIAL, "DEV-UNOFFICIAL");
        assertVersionQualifier(V_1_0_1_ARTIFACT_1, "ARTIFACT-");
        assertVersionQualifier(V_2_0_0_PRE_1, "PRE-");
        assertVersionQualifier(V_1_1_1_99, "");
    }

    /**
     * Test if the {@link Version}'s have the correct build number
     */
    @Test
    public void testVersionBuildNumber() {
        assertVersionBuildNumber(V_1_0_0, null);
        assertVersionBuildNumber(V_2_0_0, null);
        assertVersionBuildNumber(V_1_1_0, null);
        assertVersionBuildNumber(V_1_0_1, null);
        assertVersionBuildNumber(V_1_0_0_DEV_1, 1);
        assertVersionBuildNumber(V_1_0_0_DEV_2, 2);
        assertVersionBuildNumber(V_2_0_0_DEV_1, 1);
        assertVersionBuildNumber(V_1_1_0_DEV_146, 146);
        assertVersionBuildNumber(V_1_0_1_DEV_1, 1);
        assertVersionBuildNumber(V_1_0_1_DEV_UNOFFICIAL, null);
        assertVersionBuildNumber(V_1_0_1_ARTIFACT_1, 1);
        assertVersionBuildNumber(V_2_0_0_PRE_1, 1);
        assertVersionBuildNumber(V_1_1_1_99, 99);
    }

    /**
     * Compares all version combinations withe the
     * {@link UpdateStrategy#MAJOR} strategy.
     */
    @Test
    public void testVersionCompareMajor() {
        assertMultipleVersions(UpdateStrategy.MAJOR, false, false, V_1_0_0,
                V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MAJOR, false, true, V_1_0_0,
                V_2_0_0, V_1_1_0, V_1_0_1);

        assertMultipleVersions(UpdateStrategy.MAJOR, false, false, V_2_0_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);

        assertMultipleVersions(UpdateStrategy.MAJOR, false, false, V_1_1_0,
                V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MAJOR, false, true, V_1_1_0,
                V_2_0_0);

        assertMultipleVersions(UpdateStrategy.MAJOR, false, false, V_1_0_1,
                V_1_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MAJOR, false, true, V_1_0_1,
                V_2_0_0, V_1_1_0);

        assertMultipleVersions(UpdateStrategy.MAJOR, false, false, V_1_0_0_DEV_1,
                V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MAJOR, false, true, V_1_0_0_DEV_1,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1);

        assertMultipleVersions(UpdateStrategy.MAJOR, false, false, V_1_0_0_DEV_2,
                V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MAJOR, false, true, V_1_0_0_DEV_2,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1);

        assertMultipleVersions(UpdateStrategy.MAJOR, false, false, V_2_0_0_DEV_1,
                V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MAJOR, false, true, V_2_0_0_DEV_1,
                V_2_0_0);

        assertMultipleVersions(UpdateStrategy.MAJOR, false, false, V_1_1_0_DEV_146,
                V_1_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MAJOR, false, true, V_1_1_0_DEV_146,
                V_2_0_0, V_1_1_0);

        assertMultipleVersions(UpdateStrategy.MAJOR, false, false, V_1_0_1_DEV_1,
                V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MAJOR, false, true, V_1_0_1_DEV_1,
                V_2_0_0, V_1_1_0, V_1_0_1);

        assertMultipleVersions(UpdateStrategy.MAJOR, false, false, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MAJOR, false, true, V_1_0_1_DEV_UNOFFICIAL,
                V_2_0_0, V_1_1_0, V_1_0_1);

        assertMultipleVersions(UpdateStrategy.MAJOR, false, false, V_1_0_1_ARTIFACT_1,
                V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MAJOR, false, true, V_1_0_1_ARTIFACT_1,
                V_2_0_0, V_1_1_0, V_1_0_1);
    }

    /**
     * Compares all version combinations withe the
     * {@link UpdateStrategy#MINOR} strategy.
     */
    @Test
    public void testVersionCompareMinor() {
        assertMultipleVersions(UpdateStrategy.MINOR, false, false, V_1_0_0,
                V_1_0_0, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MINOR, false, true, V_1_0_0,
                V_1_1_0, V_1_0_1);

        assertMultipleVersions(UpdateStrategy.MINOR, false, false, V_2_0_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);

        assertMultipleVersions(UpdateStrategy.MINOR, false, false, V_1_1_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);

        assertMultipleVersions(UpdateStrategy.MINOR, false, false, V_1_0_1,
                V_1_0_0, V_2_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MINOR, false, true, V_1_0_1,
                V_1_1_0);

        assertMultipleVersions(UpdateStrategy.MINOR, false, false, V_1_0_0_DEV_1,
                V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MINOR, false, true, V_1_0_0_DEV_1,
                V_1_0_0, V_1_1_0, V_1_0_1);

        assertMultipleVersions(UpdateStrategy.MINOR, false, false, V_1_0_0_DEV_2,
                V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MINOR, false, true, V_1_0_0_DEV_2,
                V_1_0_0, V_1_1_0, V_1_0_1);

        assertMultipleVersions(UpdateStrategy.MINOR, false, false, V_2_0_0_DEV_1,
                V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MINOR, false, true, V_2_0_0_DEV_1,
                V_2_0_0);

        assertMultipleVersions(UpdateStrategy.MINOR, false, false, V_1_1_0_DEV_146,
                V_1_0_0, V_2_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MINOR, false, true, V_1_1_0_DEV_146,
                V_1_1_0);

        assertMultipleVersions(UpdateStrategy.MINOR, false, false, V_1_0_1_DEV_1,
                V_2_0_0, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MINOR, false, true, V_1_0_1_DEV_1,
                V_1_1_0, V_1_0_1);

        assertMultipleVersions(UpdateStrategy.MINOR, false, false, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_0, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MINOR, false, true, V_1_0_1_DEV_UNOFFICIAL,
                V_1_1_0, V_1_0_1);

        assertMultipleVersions(UpdateStrategy.MINOR, false, false, V_1_0_1_ARTIFACT_1,
                V_1_0_0, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MINOR, false, true, V_1_0_1_ARTIFACT_1,
                V_1_1_0, V_1_0_1);
    }

    /**
     * Compares all version combinations withe the
     * {@link UpdateStrategy#PATCH} strategy.
     */
    @Test
    public void testVersionComparePatch() {
        assertMultipleVersions(UpdateStrategy.PATCH, false, false, V_1_0_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.PATCH, false, true, V_1_0_0,
                V_1_0_1);

        assertMultipleVersions(UpdateStrategy.PATCH, false, false, V_2_0_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);

        assertMultipleVersions(UpdateStrategy.PATCH, false, false, V_1_1_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);

        assertMultipleVersions(UpdateStrategy.PATCH, false, false, V_1_0_1,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);

        assertMultipleVersions(UpdateStrategy.PATCH, false, false, V_1_0_0_DEV_1,
                V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.PATCH, false, true, V_1_0_0_DEV_1,
                V_1_0_0, V_1_0_1);

        assertMultipleVersions(UpdateStrategy.PATCH, false, false, V_1_0_0_DEV_2,
                V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.PATCH, false, true, V_1_0_0_DEV_2,
                V_1_0_0, V_1_0_1);

        assertMultipleVersions(UpdateStrategy.PATCH, false, false, V_2_0_0_DEV_1,
                V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.PATCH, false, true, V_2_0_0_DEV_1,
                V_2_0_0);

        assertMultipleVersions(UpdateStrategy.PATCH, false, false, V_1_1_0_DEV_146,
                V_1_0_0, V_2_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.PATCH, false, true, V_1_1_0_DEV_146,
                V_1_1_0);

        assertMultipleVersions(UpdateStrategy.PATCH, false, false, V_1_0_1_DEV_1,
                V_2_0_0, V_1_1_0, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.PATCH, false, true, V_1_0_1_DEV_1,
                V_1_0_1);

        assertMultipleVersions(UpdateStrategy.PATCH, false, false, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.PATCH, false, true, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1);

        assertMultipleVersions(UpdateStrategy.PATCH, false, false, V_1_0_1_ARTIFACT_1,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.PATCH, false, true, V_1_0_1_ARTIFACT_1,
                V_1_0_1);
    }

    /**
     * Compares all version combinations withe the
     * {@link UpdateStrategy#MAJOR} strategy with dev qualifier.
     */
    @Test
    public void testVersionCompareMajorDev() {
        assertMultipleVersions(UpdateStrategy.MAJOR, true, false, V_1_0_0,
                V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MAJOR, true, true, V_1_0_0,
                V_2_0_0, V_1_1_0, V_1_0_1, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1, V_2_0_0_PRE_1);

        assertMultipleVersions(UpdateStrategy.MAJOR, true, false, V_2_0_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);

        assertMultipleVersions(UpdateStrategy.MAJOR, true, false, V_1_1_0,
                V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MAJOR, true, true, V_1_1_0,
                V_2_0_0, V_2_0_0_DEV_1, V_2_0_0_PRE_1);

        assertMultipleVersions(UpdateStrategy.MAJOR, true, false, V_1_0_1,
                V_1_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1_ARTIFACT_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MAJOR, true, true, V_1_0_1,
                V_2_0_0, V_1_1_0, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_2_0_0_PRE_1);

        assertMultipleVersions(UpdateStrategy.MAJOR, true, false, V_1_0_0_DEV_1,
                V_1_0_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MAJOR, true, true, V_1_0_0_DEV_1,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_2_0_0_PRE_1);

        assertMultipleVersions(UpdateStrategy.MAJOR, true, false, V_1_0_0_DEV_2,
                V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MAJOR, true, true, V_1_0_0_DEV_2,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1, V_2_0_0_PRE_1);

        assertMultipleVersions(UpdateStrategy.MAJOR, true, false, V_2_0_0_DEV_1,
                V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MAJOR, true, true, V_2_0_0_DEV_1,
                V_2_0_0, V_2_0_0_PRE_1);

        assertMultipleVersions(UpdateStrategy.MAJOR, true, false, V_1_1_0_DEV_146,
                V_1_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_1_0_DEV_146, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1_ARTIFACT_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MAJOR, true, true, V_1_1_0_DEV_146,
                V_2_0_0, V_1_1_0, V_2_0_0_DEV_1, V_2_0_0_PRE_1);

        assertMultipleVersions(UpdateStrategy.MAJOR, true, false, V_1_0_1_DEV_1,
                V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1,
                V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MAJOR, true, true, V_1_0_1_DEV_1,
                V_2_0_0, V_1_1_0, V_1_0_1, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_2_0_0_PRE_1);

        assertMultipleVersions(UpdateStrategy.MAJOR, true, false, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MAJOR, true, true, V_1_0_1_DEV_UNOFFICIAL,
                V_2_0_0, V_1_1_0, V_1_0_1, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1, V_2_0_0_PRE_1);

        assertMultipleVersions(UpdateStrategy.MAJOR, true, false, V_1_0_1_ARTIFACT_1,
                V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MAJOR, true, true, V_1_0_1_ARTIFACT_1,
                V_2_0_0, V_1_1_0, V_1_0_1, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1, V_2_0_0_PRE_1);
    }

    /**
     * Compares all version combinations withe the
     * {@link UpdateStrategy#MINOR} strategy with dev qualifier.
     */
    @Test
    public void testVersionCompareMinorDev() {
        assertMultipleVersions(UpdateStrategy.MINOR, true, false, V_1_0_0,
                V_1_0_0, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MINOR, true, true, V_1_0_0,
                V_1_1_0, V_1_0_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1);

        assertMultipleVersions(UpdateStrategy.MINOR, true, false, V_2_0_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);

        assertMultipleVersions(UpdateStrategy.MINOR, true, false, V_1_1_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);

        assertMultipleVersions(UpdateStrategy.MINOR, true, false, V_1_0_1,
                V_1_0_0, V_2_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MINOR, true, true, V_1_0_1,
                V_1_1_0, V_1_1_0_DEV_146);

        assertMultipleVersions(UpdateStrategy.MINOR, true, false, V_1_0_0_DEV_1,
                V_2_0_0, V_1_0_0_DEV_1, V_2_0_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1,
                V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MINOR, true, true, V_1_0_0_DEV_1,
                V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_2, V_1_1_0_DEV_146, V_1_0_1_DEV_1);

        assertMultipleVersions(UpdateStrategy.MINOR, true, false, V_1_0_0_DEV_2,
                V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1,
                V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MINOR, true, true, V_1_0_0_DEV_2,
                V_1_0_0, V_1_1_0, V_1_0_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1);

        assertMultipleVersions(UpdateStrategy.MINOR, true, false, V_2_0_0_DEV_1,
                V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MINOR, true, true, V_2_0_0_DEV_1,
                V_2_0_0, V_2_0_0_PRE_1);

        assertMultipleVersions(UpdateStrategy.MINOR, true, false, V_1_1_0_DEV_146,
                V_1_0_0, V_2_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MINOR, true, true, V_1_1_0_DEV_146,
                V_1_1_0);

        assertMultipleVersions(UpdateStrategy.MINOR, true, false, V_1_0_1_DEV_1,
                V_2_0_0, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MINOR, true, true, V_1_0_1_DEV_1,
                V_1_1_0, V_1_0_1, V_1_1_0_DEV_146);

        assertMultipleVersions(UpdateStrategy.MINOR, true, false, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_0, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MINOR, true, true, V_1_0_1_DEV_UNOFFICIAL,
                V_1_1_0, V_1_0_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1);

        assertMultipleVersions(UpdateStrategy.MINOR, true, false, V_1_0_1_ARTIFACT_1,
                V_1_0_0, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.MINOR, true, true, V_1_0_1_ARTIFACT_1,
                V_1_1_0, V_1_0_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1);
    }

    /**
     * Compares all version combinations withe the
     * {@link UpdateStrategy#PATCH} strategy with dev qualifier.
     */
    @Test
    public void testVersionComparePatchDev() {
        assertMultipleVersions(UpdateStrategy.PATCH, true, false, V_1_0_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.PATCH, true, true, V_1_0_0,
                V_1_0_1, V_1_0_1_DEV_1);

        assertMultipleVersions(UpdateStrategy.PATCH, true, false, V_2_0_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);

        assertMultipleVersions(UpdateStrategy.PATCH, true, false, V_1_1_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);

        assertMultipleVersions(UpdateStrategy.PATCH, true, false, V_1_0_1,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);

        assertMultipleVersions(UpdateStrategy.PATCH, true, false, V_1_0_0_DEV_1,
                V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.PATCH, true, true, V_1_0_0_DEV_1,
                V_1_0_0, V_1_0_1, V_1_0_0_DEV_2, V_1_0_1_DEV_1);

        assertMultipleVersions(UpdateStrategy.PATCH, true, false, V_1_0_0_DEV_2,
                V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.PATCH, true, true, V_1_0_0_DEV_2,
                V_1_0_0, V_1_0_1, V_1_0_1_DEV_1);

        assertMultipleVersions(UpdateStrategy.PATCH, true, false, V_2_0_0_DEV_1,
                V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.PATCH, true, true, V_2_0_0_DEV_1,
                V_2_0_0, V_2_0_0_PRE_1);

        assertMultipleVersions(UpdateStrategy.PATCH, true, false, V_1_1_0_DEV_146,
                V_1_0_0, V_2_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.PATCH, true, true, V_1_1_0_DEV_146,
                V_1_1_0);

        assertMultipleVersions(UpdateStrategy.PATCH, true, false, V_1_0_1_DEV_1,
                V_2_0_0, V_1_1_0, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.PATCH, true, true, V_1_0_1_DEV_1,
                V_1_0_1);

        assertMultipleVersions(UpdateStrategy.PATCH, true, false, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.PATCH, true, true, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1, V_1_0_1_DEV_1);

        assertMultipleVersions(UpdateStrategy.PATCH, true, false, V_1_0_1_ARTIFACT_1,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_1_99);
        assertMultipleVersions(UpdateStrategy.PATCH, true, true, V_1_0_1_ARTIFACT_1,
                V_1_0_1, V_1_0_1_DEV_1);
    }

    @Test
    public void testEmptyQualifier() {
        assertTrue(Version.isNewer(V_1_1_0_DEV_146, V_1_1_1_99, UpdateStrategy.PATCH, "", "DEV-"),
                "Expected update");
        assertTrue(Version.isNewer(V_1_1_0_DEV_146, V_1_1_1_99, UpdateStrategy.PATCH, "DEV-", ""),
                "Expected no update");
    }

    private void assertMultipleVersions(final UpdateStrategy strategy, final boolean dev,
                                        final boolean updateExpected, final Version otherVersion,
                                        final Version... targetVersions) {
        for (final Version targetVersion : targetVersions) {
            final String message = "UpdateStrategy: '" + strategy + "', Version: '" + otherVersion.getVersion()
                    + "', with Version: '" + targetVersion.getVersion()
                    + "', expected: '" + updateExpected + "'!";

            if (dev) {
                assertEquals(updateExpected, Version.isNewer(otherVersion, targetVersion, strategy, "PRE-", "DEV-"), message);
            } else {
                assertEquals(updateExpected, Version.isNewer(otherVersion, targetVersion, strategy), message);
            }
        }
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
