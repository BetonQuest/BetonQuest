package org.betonquest.betonquest.versioning;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test should only ensure, that the {@link Version} does not break anything or select wrong versions
 * and push them into the production server.
 */
@SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
class VersionComparatorTest {
    /**
     * PRE release qualifier
     */
    public static final String QUALIFIER_PRE = "PRE-";

    /**
     * DEV release qualifier
     */
    public static final String QUALIFIER_DEV = "DEV-";

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
    private static final Version V_1_1_0_99 = new Version("1.1.0-99");

    /**
     * Compares all version combinations with the
     * {@link UpdateStrategy#MAJOR} strategy.
     */
    @Test
    void testVersionCompareMajor() {
        final VersionComparator comparator = new VersionComparator(UpdateStrategy.MAJOR);

        assertMultipleVersions(comparator, false, V_1_0_0,
                V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_0,
                V_2_0_0, V_1_1_0, V_1_0_1);

        assertMultipleVersions(comparator, false, V_2_0_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);

        assertMultipleVersions(comparator, false, V_1_1_0,
                V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_1_0,
                V_2_0_0);

        assertMultipleVersions(comparator, false, V_1_0_1,
                V_1_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_1,
                V_2_0_0, V_1_1_0);

        assertMultipleVersions(comparator, false, V_1_0_0_DEV_1,
                V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_0_DEV_1,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1);

        assertMultipleVersions(comparator, false, V_1_0_0_DEV_2,
                V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_0_DEV_2,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1);

        assertMultipleVersions(comparator, false, V_2_0_0_DEV_1,
                V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_2_0_0_DEV_1,
                V_2_0_0);

        assertMultipleVersions(comparator, false, V_1_1_0_DEV_146,
                V_1_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_1_0_DEV_146,
                V_2_0_0, V_1_1_0);

        assertMultipleVersions(comparator, false, V_1_0_1_DEV_1,
                V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_1_DEV_1,
                V_2_0_0, V_1_1_0, V_1_0_1);

        assertMultipleVersions(comparator, false, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_1_DEV_UNOFFICIAL,
                V_2_0_0, V_1_1_0, V_1_0_1);

        assertMultipleVersions(comparator, false, V_1_0_1_ARTIFACT_1,
                V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_1_ARTIFACT_1,
                V_2_0_0, V_1_1_0, V_1_0_1);
    }

    /**
     * Compares all version combinations with the
     * {@link UpdateStrategy#MINOR} strategy.
     */
    @Test
    void testVersionCompareMinor() {
        final VersionComparator comparator = new VersionComparator(UpdateStrategy.MINOR);

        assertMultipleVersions(comparator, false, V_1_0_0,
                V_1_0_0, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_0,
                V_1_1_0, V_1_0_1);

        assertMultipleVersions(comparator, false, V_2_0_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);

        assertMultipleVersions(comparator, false, V_1_1_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);

        assertMultipleVersions(comparator, false, V_1_0_1,
                V_1_0_0, V_2_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_1,
                V_1_1_0);

        assertMultipleVersions(comparator, false, V_1_0_0_DEV_1,
                V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_0_DEV_1,
                V_1_0_0, V_1_1_0, V_1_0_1);

        assertMultipleVersions(comparator, false, V_1_0_0_DEV_2,
                V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_0_DEV_2,
                V_1_0_0, V_1_1_0, V_1_0_1);

        assertMultipleVersions(comparator, false, V_2_0_0_DEV_1,
                V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_2_0_0_DEV_1,
                V_2_0_0);

        assertMultipleVersions(comparator, false, V_1_1_0_DEV_146,
                V_1_0_0, V_2_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_1_0_DEV_146,
                V_1_1_0);

        assertMultipleVersions(comparator, false, V_1_0_1_DEV_1,
                V_2_0_0, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_1_DEV_1,
                V_1_1_0, V_1_0_1);

        assertMultipleVersions(comparator, false, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_0, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_1_DEV_UNOFFICIAL,
                V_1_1_0, V_1_0_1);

        assertMultipleVersions(comparator, false, V_1_0_1_ARTIFACT_1,
                V_1_0_0, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_1_ARTIFACT_1,
                V_1_1_0, V_1_0_1);
    }

    /**
     * Compares all version combinations with the
     * {@link UpdateStrategy#PATCH} strategy.
     */
    @Test
    void testVersionComparePatch() {
        final VersionComparator comparator = new VersionComparator(UpdateStrategy.PATCH);

        assertMultipleVersions(comparator, false, V_1_0_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_0,
                V_1_0_1);

        assertMultipleVersions(comparator, false, V_2_0_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);

        assertMultipleVersions(comparator, false, V_1_1_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);

        assertMultipleVersions(comparator, false, V_1_0_1,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);

        assertMultipleVersions(comparator, false, V_1_0_0_DEV_1,
                V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_0_DEV_1,
                V_1_0_0, V_1_0_1);

        assertMultipleVersions(comparator, false, V_1_0_0_DEV_2,
                V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_0_DEV_2,
                V_1_0_0, V_1_0_1);

        assertMultipleVersions(comparator, false, V_2_0_0_DEV_1,
                V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_2_0_0_DEV_1,
                V_2_0_0);

        assertMultipleVersions(comparator, false, V_1_1_0_DEV_146,
                V_1_0_0, V_2_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_1_0_DEV_146,
                V_1_1_0);

        assertMultipleVersions(comparator, false, V_1_0_1_DEV_1,
                V_2_0_0, V_1_1_0, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_1_DEV_1,
                V_1_0_1);

        assertMultipleVersions(comparator, false, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1);

        assertMultipleVersions(comparator, false, V_1_0_1_ARTIFACT_1,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_1_ARTIFACT_1,
                V_1_0_1);
    }

    /**
     * Compares all version combinations with the
     * {@link UpdateStrategy#MAJOR} strategy with dev qualifier.
     */
    @Test
    void testVersionCompareMajorDev() {
        final VersionComparator comparator = new VersionComparator(UpdateStrategy.MAJOR, QUALIFIER_PRE, QUALIFIER_DEV);

        assertMultipleVersions(comparator, false, V_1_0_0,
                V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_0,
                V_2_0_0, V_1_1_0, V_1_0_1, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1, V_2_0_0_PRE_1);

        assertMultipleVersions(comparator, false, V_2_0_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);

        assertMultipleVersions(comparator, false, V_1_1_0,
                V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_1_0,
                V_2_0_0, V_2_0_0_DEV_1, V_2_0_0_PRE_1);

        assertMultipleVersions(comparator, false, V_1_0_1,
                V_1_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1_ARTIFACT_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_1,
                V_2_0_0, V_1_1_0, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_2_0_0_PRE_1);

        assertMultipleVersions(comparator, false, V_1_0_0_DEV_1,
                V_1_0_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_0_DEV_1,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_2_0_0_PRE_1);

        assertMultipleVersions(comparator, false, V_1_0_0_DEV_2,
                V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_0_DEV_2,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1, V_2_0_0_PRE_1);

        assertMultipleVersions(comparator, false, V_2_0_0_DEV_1,
                V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_2_0_0_DEV_1,
                V_2_0_0, V_2_0_0_PRE_1);

        assertMultipleVersions(comparator, false, V_1_1_0_DEV_146,
                V_1_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_1_0_DEV_146, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1_ARTIFACT_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_1_0_DEV_146,
                V_2_0_0, V_1_1_0, V_2_0_0_DEV_1, V_2_0_0_PRE_1);

        assertMultipleVersions(comparator, false, V_1_0_1_DEV_1,
                V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1,
                V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_1_DEV_1,
                V_2_0_0, V_1_1_0, V_1_0_1, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_2_0_0_PRE_1);

        assertMultipleVersions(comparator, false, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_1_DEV_UNOFFICIAL,
                V_2_0_0, V_1_1_0, V_1_0_1, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1, V_2_0_0_PRE_1);

        assertMultipleVersions(comparator, false, V_1_0_1_ARTIFACT_1,
                V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_1_ARTIFACT_1,
                V_2_0_0, V_1_1_0, V_1_0_1, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1, V_2_0_0_PRE_1);
    }

    /**
     * Compares all version combinations with the
     * {@link UpdateStrategy#MINOR} strategy with dev qualifier.
     */
    @Test
    void testVersionCompareMinorDev() {
        final VersionComparator comparator = new VersionComparator(UpdateStrategy.MINOR, QUALIFIER_PRE, QUALIFIER_DEV);

        assertMultipleVersions(comparator, false, V_1_0_0,
                V_1_0_0, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_0,
                V_1_1_0, V_1_0_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1);

        assertMultipleVersions(comparator, false, V_2_0_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);

        assertMultipleVersions(comparator, false, V_1_1_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);

        assertMultipleVersions(comparator, false, V_1_0_1,
                V_1_0_0, V_2_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_1,
                V_1_1_0, V_1_1_0_DEV_146);

        assertMultipleVersions(comparator, false, V_1_0_0_DEV_1,
                V_2_0_0, V_1_0_0_DEV_1, V_2_0_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1,
                V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_0_DEV_1,
                V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_2, V_1_1_0_DEV_146, V_1_0_1_DEV_1);

        assertMultipleVersions(comparator, false, V_1_0_0_DEV_2,
                V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1,
                V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_0_DEV_2,
                V_1_0_0, V_1_1_0, V_1_0_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1);

        assertMultipleVersions(comparator, false, V_2_0_0_DEV_1,
                V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_2_0_0_DEV_1,
                V_2_0_0, V_2_0_0_PRE_1);

        assertMultipleVersions(comparator, false, V_1_1_0_DEV_146,
                V_1_0_0, V_2_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_1_0_DEV_146,
                V_1_1_0);

        assertMultipleVersions(comparator, false, V_1_0_1_DEV_1,
                V_2_0_0, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_1_DEV_1,
                V_1_1_0, V_1_0_1, V_1_1_0_DEV_146);

        assertMultipleVersions(comparator, false, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_0, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_1_DEV_UNOFFICIAL,
                V_1_1_0, V_1_0_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1);

        assertMultipleVersions(comparator, false, V_1_0_1_ARTIFACT_1,
                V_1_0_0, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_1_ARTIFACT_1,
                V_1_1_0, V_1_0_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1);
    }

    /**
     * Compares all version combinations with the
     * {@link UpdateStrategy#PATCH} strategy with dev qualifier.
     */
    @Test
    void testVersionComparePatchDev() {
        final VersionComparator comparator = new VersionComparator(UpdateStrategy.PATCH, QUALIFIER_PRE, QUALIFIER_DEV);

        assertMultipleVersions(comparator, false, V_1_0_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_0,
                V_1_0_1, V_1_0_1_DEV_1);

        assertMultipleVersions(comparator, false, V_2_0_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);

        assertMultipleVersions(comparator, false, V_1_1_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);

        assertMultipleVersions(comparator, false, V_1_0_1,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);

        assertMultipleVersions(comparator, false, V_1_0_0_DEV_1,
                V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_0_DEV_1,
                V_1_0_0, V_1_0_1, V_1_0_0_DEV_2, V_1_0_1_DEV_1);

        assertMultipleVersions(comparator, false, V_1_0_0_DEV_2,
                V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_0_DEV_2,
                V_1_0_0, V_1_0_1, V_1_0_1_DEV_1);

        assertMultipleVersions(comparator, false, V_2_0_0_DEV_1,
                V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_2_0_0_DEV_1,
                V_2_0_0, V_2_0_0_PRE_1);

        assertMultipleVersions(comparator, false, V_1_1_0_DEV_146,
                V_1_0_0, V_2_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_1_0_DEV_146,
                V_1_1_0);

        assertMultipleVersions(comparator, false, V_1_0_1_DEV_1,
                V_2_0_0, V_1_1_0, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_1_DEV_1,
                V_1_0_1);

        assertMultipleVersions(comparator, false, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1, V_1_0_1_DEV_1);

        assertMultipleVersions(comparator, false, V_1_0_1_ARTIFACT_1,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1, V_2_0_0_PRE_1, V_1_1_0_99);
        assertMultipleVersions(comparator, true, V_1_0_1_ARTIFACT_1,
                V_1_0_1, V_1_0_1_DEV_1);
    }

    private void assertMultipleVersions(final VersionComparator versionComparator, final boolean updateExpected,
                                        final Version current, final Version... targets) {
        for (final Version targetVersion : targets) {
            final String message = "UpdateStrategy: '" + versionComparator.getUpdateStrategy()
                    + "', Version: '" + current.getVersion()
                    + "', with Version: '" + targetVersion.getVersion()
                    + "', expected: '" + updateExpected + "'!";
            assertEquals(updateExpected, versionComparator.isOtherNewerThanCurrent(current, targetVersion), message);
        }
    }

    @Test
    void testEmptyQualifier() {
        final VersionComparator vc1 = new VersionComparator(UpdateStrategy.PATCH, "", QUALIFIER_DEV);
        assertTrue(vc1.isOtherNewerThanCurrent(V_1_1_0_DEV_146, V_1_1_0_99), "Expected update");

        final VersionComparator vc2 = new VersionComparator(UpdateStrategy.PATCH, QUALIFIER_DEV, "");
        assertTrue(vc2.isOtherNewerThanCurrent(V_1_1_0_99, V_1_1_0_DEV_146), "Expected no update");
    }
}
