package org.betonquest.betonquest.lib.version;

import org.betonquest.betonquest.api.version.Version;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test should only ensure, that the {@link Version} does not break anything or select wrong versions
 * and push them into the production server.
 */
@SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
class VersionComparatorTest {

    /**
     * A version that represent a MAJOR Version.
     */
    private static final Version V_1_0_0 = BetonQuestVersion.parse("1.0.0");

    /**
     * A version that represent a newer MAJOR Version.
     */
    private static final Version V_2_0_0 = BetonQuestVersion.parse("2.0.0");

    /**
     * A version that represent a newer MINOR Version.
     */
    private static final Version V_1_1_0 = BetonQuestVersion.parse("1.1.0");

    /**
     * A version that represent a newer PATCH Version.
     */
    private static final Version V_1_0_1 = BetonQuestVersion.parse("1.0.1");

    /**
     * A version that represent a DEV Version.
     */
    private static final Version V_1_0_0_DEV_1 = BetonQuestVersion.parse("1.0.0-DEV-1");

    /**
     * A version that represent a newer DEV Version.
     */
    private static final Version V_1_0_0_DEV_2 = BetonQuestVersion.parse("1.0.0-DEV-2");

    /**
     * A version that represent a newer MAJOR-DEV Version.
     */
    private static final Version V_2_0_0_DEV_1 = BetonQuestVersion.parse("2.0.0-DEV-1");

    /**
     * A version that represent a newer MINOR-DEV Version.
     */
    private static final Version V_1_1_0_DEV_146 = BetonQuestVersion.parse("1.1.0-DEV-146");

    /**
     * A version that represent a newer PATCH-DEV Version.
     */
    private static final Version V_1_0_1_DEV_1 = BetonQuestVersion.parse("1.0.1-DEV-1");

    /**
     * A version that represent a local PATCH Version.
     */
    private static final Version V_1_0_1_DEV_UNOFFICIAL = BetonQuestVersion.parse("1.0.1-DEV-UNOFFICIAL");

    /**
     * A version that represent a fork PATCH Version.
     */
    private static final Version V_1_0_1_ARTIFACT_1 = BetonQuestVersion.parse("1.0.1-DEV-ARTIFACT-Betonquest/Betonquest-1");

    /**
     * Compares all version combinations with the
     * {@link BetonQuestUpdateStrategy#MAJOR} strategy.
     */
    @Test
    void testVersionCompareMajor() {
        final Comparator<Version> comparator = BetonQuestUpdateStrategy.MAJOR.getComparator(true);
        final BetonQuestUpdateStrategy strategy = BetonQuestUpdateStrategy.MAJOR;

        assertMultipleVersions(comparator, strategy, false, V_1_0_0,
                V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_0,
                V_2_0_0, V_1_1_0, V_1_0_1);

        assertMultipleVersions(comparator, strategy, false, V_2_0_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        assertMultipleVersions(comparator, strategy, false, V_1_1_0,
                V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_1_0,
                V_2_0_0);

        assertMultipleVersions(comparator, strategy, false, V_1_0_1,
                V_1_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_1,
                V_2_0_0, V_1_1_0);

        assertMultipleVersions(comparator, strategy, false, V_1_0_0_DEV_1,
                V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_0_DEV_1,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1);

        assertMultipleVersions(comparator, strategy, false, V_1_0_0_DEV_2,
                V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_0_DEV_2,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1);

        assertMultipleVersions(comparator, strategy, false, V_2_0_0_DEV_1,
                V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_2_0_0_DEV_1,
                V_2_0_0);

        assertMultipleVersions(comparator, strategy, false, V_1_1_0_DEV_146,
                V_1_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_1_0_DEV_146,
                V_2_0_0, V_1_1_0);

        assertMultipleVersions(comparator, strategy, false, V_1_0_1_DEV_1,
                V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_1_DEV_1,
                V_2_0_0, V_1_1_0, V_1_0_1);

        assertMultipleVersions(comparator, strategy, false, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_1_DEV_UNOFFICIAL,
                V_2_0_0, V_1_1_0, V_1_0_1);

        assertMultipleVersions(comparator, strategy, false, V_1_0_1_ARTIFACT_1,
                V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_1_ARTIFACT_1,
                V_2_0_0, V_1_1_0, V_1_0_1);
    }

    /**
     * Compares all version combinations with the
     * {@link BetonQuestUpdateStrategy#MINOR} strategy.
     */
    @Test
    void testVersionCompareMinor() {
        final Comparator<Version> comparator = BetonQuestUpdateStrategy.MINOR.getComparator(true);
        final BetonQuestUpdateStrategy strategy = BetonQuestUpdateStrategy.MINOR;

        assertMultipleVersions(comparator, strategy, false, V_1_0_0,
                V_1_0_0, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_0,
                V_1_1_0, V_1_0_1);

        assertMultipleVersions(comparator, strategy, false, V_2_0_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        assertMultipleVersions(comparator, strategy, false, V_1_1_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        assertMultipleVersions(comparator, strategy, false, V_1_0_1,
                V_1_0_0, V_2_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_1,
                V_1_1_0);

        assertMultipleVersions(comparator, strategy, false, V_1_0_0_DEV_1,
                V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_0_DEV_1,
                V_1_0_0, V_1_1_0, V_1_0_1);

        assertMultipleVersions(comparator, strategy, false, V_1_0_0_DEV_2,
                V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_0_DEV_2,
                V_1_0_0, V_1_1_0, V_1_0_1);

        assertMultipleVersions(comparator, strategy, false, V_2_0_0_DEV_1,
                V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_2_0_0_DEV_1,
                V_2_0_0);

        assertMultipleVersions(comparator, strategy, false, V_1_1_0_DEV_146,
                V_1_0_0, V_2_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_1_0_DEV_146,
                V_1_1_0);

        assertMultipleVersions(comparator, strategy, false, V_1_0_1_DEV_1,
                V_2_0_0, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_1_DEV_1,
                V_1_1_0, V_1_0_1);

        assertMultipleVersions(comparator, strategy, false, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_0, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_1_DEV_UNOFFICIAL,
                V_1_1_0, V_1_0_1);

        assertMultipleVersions(comparator, strategy, false, V_1_0_1_ARTIFACT_1,
                V_1_0_0, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_1_ARTIFACT_1,
                V_1_1_0, V_1_0_1);
    }

    /**
     * Compares all version combinations with the
     * {@link BetonQuestUpdateStrategy#PATCH} strategy.
     */
    @Test
    void testVersionComparePatch() {
        final Comparator<Version> comparator = BetonQuestUpdateStrategy.PATCH.getComparator(true);
        final BetonQuestUpdateStrategy strategy = BetonQuestUpdateStrategy.PATCH;

        assertMultipleVersions(comparator, strategy, false, V_1_0_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_0,
                V_1_0_1);

        assertMultipleVersions(comparator, strategy, false, V_2_0_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        assertMultipleVersions(comparator, strategy, false, V_1_1_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        assertMultipleVersions(comparator, strategy, false, V_1_0_1,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        assertMultipleVersions(comparator, strategy, false, V_1_0_0_DEV_1,
                V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_0_DEV_1,
                V_1_0_0, V_1_0_1);

        assertMultipleVersions(comparator, strategy, false, V_1_0_0_DEV_2,
                V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_0_DEV_2,
                V_1_0_0, V_1_0_1);

        assertMultipleVersions(comparator, strategy, false, V_2_0_0_DEV_1,
                V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_2_0_0_DEV_1,
                V_2_0_0);

        assertMultipleVersions(comparator, strategy, false, V_1_1_0_DEV_146,
                V_1_0_0, V_2_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_1_0_DEV_146,
                V_1_1_0);

        assertMultipleVersions(comparator, strategy, false, V_1_0_1_DEV_1,
                V_2_0_0, V_1_1_0, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_1_DEV_1,
                V_1_0_1);

        assertMultipleVersions(comparator, strategy, false, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1);

        assertMultipleVersions(comparator, strategy, false, V_1_0_1_ARTIFACT_1,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_1_ARTIFACT_1,
                V_1_0_1);
    }

    /**
     * Compares all version combinations with the
     * {@link BetonQuestUpdateStrategy#MAJOR} strategy with dev qualifier.
     */
    @Test
    void testVersionCompareMajorDev() {
        final Comparator<Version> comparator = BetonQuestUpdateStrategy.MAJOR.getComparator(false);
        final BetonQuestUpdateStrategy strategy = BetonQuestUpdateStrategy.MAJOR;

        assertMultipleVersions(comparator, strategy, false, V_1_0_0,
                V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_0,
                V_2_0_0, V_1_1_0, V_1_0_1, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1);

        assertMultipleVersions(comparator, strategy, false, V_2_0_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        assertMultipleVersions(comparator, strategy, false, V_1_1_0,
                V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_1_0,
                V_2_0_0, V_2_0_0_DEV_1);

        assertMultipleVersions(comparator, strategy, false, V_1_0_1,
                V_1_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_1,
                V_2_0_0, V_1_1_0, V_2_0_0_DEV_1, V_1_1_0_DEV_146);

        assertMultipleVersions(comparator, strategy, false, V_1_0_0_DEV_1,
                V_1_0_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_0_DEV_1,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1);

        assertMultipleVersions(comparator, strategy, false, V_1_0_0_DEV_2,
                V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_0_DEV_2,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1);

        assertMultipleVersions(comparator, strategy, false, V_2_0_0_DEV_1,
                V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_2_0_0_DEV_1,
                V_2_0_0);

        assertMultipleVersions(comparator, strategy, false, V_1_1_0_DEV_146,
                V_1_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_1_0_DEV_146, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_1_0_DEV_146,
                V_2_0_0, V_1_1_0, V_2_0_0_DEV_1);

        assertMultipleVersions(comparator, strategy, false, V_1_0_1_DEV_1,
                V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_1_DEV_1,
                V_2_0_0, V_1_1_0, V_1_0_1, V_2_0_0_DEV_1, V_1_1_0_DEV_146);

        assertMultipleVersions(comparator, strategy, false, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_1_DEV_UNOFFICIAL,
                V_2_0_0, V_1_1_0, V_1_0_1, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1);

        assertMultipleVersions(comparator, strategy, false, V_1_0_1_ARTIFACT_1,
                V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_1_ARTIFACT_1,
                V_2_0_0, V_1_1_0, V_1_0_1, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1);
    }

    /**
     * Compares all version combinations with the
     * {@link BetonQuestUpdateStrategy#MINOR} strategy with dev qualifier.
     */
    @Test
    void testVersionCompareMinorDev() {
        final Comparator<Version> comparator = BetonQuestUpdateStrategy.MINOR.getComparator(false);
        final BetonQuestUpdateStrategy strategy = BetonQuestUpdateStrategy.MINOR;

        assertMultipleVersions(comparator, strategy, false, V_1_0_0,
                V_1_0_0, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_0,
                V_1_1_0, V_1_0_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1);

        assertMultipleVersions(comparator, strategy, false, V_2_0_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        assertMultipleVersions(comparator, strategy, false, V_1_1_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        assertMultipleVersions(comparator, strategy, false, V_1_0_1,
                V_1_0_0, V_2_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_1,
                V_1_1_0, V_1_1_0_DEV_146);

        assertMultipleVersions(comparator, strategy, false, V_1_0_0_DEV_1,
                V_2_0_0, V_1_0_0_DEV_1, V_2_0_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_0_DEV_1,
                V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_2, V_1_1_0_DEV_146, V_1_0_1_DEV_1);

        assertMultipleVersions(comparator, strategy, false, V_1_0_0_DEV_2,
                V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_0_DEV_2,
                V_1_0_0, V_1_1_0, V_1_0_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1);

        assertMultipleVersions(comparator, strategy, false, V_2_0_0_DEV_1,
                V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_2_0_0_DEV_1,
                V_2_0_0);

        assertMultipleVersions(comparator, strategy, false, V_1_1_0_DEV_146,
                V_1_0_0, V_2_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_1_0_DEV_146,
                V_1_1_0);

        assertMultipleVersions(comparator, strategy, false, V_1_0_1_DEV_1,
                V_2_0_0, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_1_DEV_1,
                V_1_1_0, V_1_0_1, V_1_1_0_DEV_146);

        assertMultipleVersions(comparator, strategy, false, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_0, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_1_DEV_UNOFFICIAL,
                V_1_1_0, V_1_0_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1);

        assertMultipleVersions(comparator, strategy, false, V_1_0_1_ARTIFACT_1,
                V_1_0_0, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_1_ARTIFACT_1,
                V_1_1_0, V_1_0_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1);
    }

    /**
     * Compares all version combinations with the
     * {@link BetonQuestUpdateStrategy#PATCH} strategy with dev qualifier.
     */
    @Test
    void testVersionComparePatchDev() {
        final Comparator<Version> comparator = BetonQuestUpdateStrategy.PATCH.getComparator(false);
        final BetonQuestUpdateStrategy strategy = BetonQuestUpdateStrategy.PATCH;

        assertMultipleVersions(comparator, strategy, false, V_1_0_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_0,
                V_1_0_1, V_1_0_1_DEV_1);

        assertMultipleVersions(comparator, strategy, false, V_2_0_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        assertMultipleVersions(comparator, strategy, false, V_1_1_0,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        assertMultipleVersions(comparator, strategy, false, V_1_0_1,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        assertMultipleVersions(comparator, strategy, false, V_1_0_0_DEV_1,
                V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_0_DEV_1,
                V_1_0_0, V_1_0_1, V_1_0_0_DEV_2, V_1_0_1_DEV_1);

        assertMultipleVersions(comparator, strategy, false, V_1_0_0_DEV_2,
                V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_0_DEV_2,
                V_1_0_0, V_1_0_1, V_1_0_1_DEV_1);

        assertMultipleVersions(comparator, strategy, false, V_2_0_0_DEV_1,
                V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_2_0_0_DEV_1,
                V_2_0_0);

        assertMultipleVersions(comparator, strategy, false, V_1_1_0_DEV_146,
                V_1_0_0, V_2_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_1_0_DEV_146,
                V_1_1_0);

        assertMultipleVersions(comparator, strategy, false, V_1_0_1_DEV_1,
                V_2_0_0, V_1_1_0, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146, V_1_0_1_DEV_1,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_1_DEV_1,
                V_1_0_1);

        assertMultipleVersions(comparator, strategy, false, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_1_DEV_UNOFFICIAL,
                V_1_0_1, V_1_0_1_DEV_1);

        assertMultipleVersions(comparator, strategy, false, V_1_0_1_ARTIFACT_1,
                V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_146,
                V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(comparator, strategy, true, V_1_0_1_ARTIFACT_1,
                V_1_0_1, V_1_0_1_DEV_1);
    }

    private void assertMultipleVersions(final Comparator<Version> versionComparator, final BetonQuestUpdateStrategy strategy, final boolean updateExpected,
                                        final Version current, final Version... targets) {
        for (final Version targetVersion : targets) {
            final String message = "UpdateStrategy: '" + strategy
                    + "', Version: '" + current.toString()
                    + "', with Version: '" + targetVersion.toString()
                    + "', expected: '" + updateExpected + "'!";
            assertEquals(updateExpected, current.isOlderThan(versionComparator, targetVersion), message);
        }
    }
}
