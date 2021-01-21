package org.betonquest.betonquest.utils.updater;

import org.betonquest.betonquest.utils.Updater;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This test should only ensure, that the {@link Updater.Version} dose not break anything or select wrong versions
 * and push them into the production server.
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods"})
public class VersionTest {
    /**
     * A version that represent a MAJOR Version
     */
    private static final Updater.Version V_1_0_0 = new Updater.Version("1.0.0");
    /**
     * A version that represent a newer MAJOR Version
     */
    private static final Updater.Version V_2_0_0 = new Updater.Version("2.0.0");
    /**
     * A version that represent a newer MINOR Version
     */
    private static final Updater.Version V_1_1_0 = new Updater.Version("1.1.0");
    /**
     * A version that represent a newer PATCH Version
     */
    private static final Updater.Version V_1_0_1 = new Updater.Version("1.0.1");
    /**
     * A version that represent a DEV Version
     */
    private static final Updater.Version V_1_0_0_DEV_1 = new Updater.Version("1.0.0-DEV-1");
    /**
     * A version that represent a newer DEV Version
     */
    private static final Updater.Version V_1_0_0_DEV_2 = new Updater.Version("1.0.0-DEV-2");
    /**
     * A version that represent a newer MAJOR-DEV Version
     */
    private static final Updater.Version V_2_0_0_DEV_1 = new Updater.Version("2.0.0-DEV-1");
    /**
     * A version that represent a newer MINOR-DEV Version
     */
    private static final Updater.Version V_1_1_0_DEV_1 = new Updater.Version("1.1.0-DEV-1");
    /**
     * A version that represent a newer PATCH-DEV Version
     */
    private static final Updater.Version V_1_0_1_DEV_1 = new Updater.Version("1.0.1-DEV-1");
    /**
     * A version that represent a local PATCH Version
     */
    private static final Updater.Version V_1_0_1_DEV_UNOFFICIAL = new Updater.Version("1.0.1-DEV-UNOFFICIAL");
    /**
     * A version that represent a fork PATCH Version
     */
    private static final Updater.Version V_1_0_1_ARTIFACT_1 = new Updater.Version("1.0.1-ARTIFACT-1");

    /**
     * Empty constructor
     */
    public VersionTest() {
    }

    /**
     * Test if the {@link pl.betoncraft.betonquest.utils.Updater.Version}'s have the correct string version
     */
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    public void testVersionString() {
        assertVersionString(V_1_0_0, "1.0.0");
        assertVersionString(V_2_0_0, "2.0.0");
        assertVersionString(V_1_1_0, "1.1.0");
        assertVersionString(V_1_0_1, "1.0.1");
        assertVersionString(V_1_0_0_DEV_1, "1.0.0-DEV-1");
        assertVersionString(V_1_0_0_DEV_2, "1.0.0-DEV-2");
        assertVersionString(V_2_0_0_DEV_1, "2.0.0-DEV-1");
        assertVersionString(V_1_1_0_DEV_1, "1.1.0-DEV-1");
        assertVersionString(V_1_0_1_DEV_1, "1.0.1-DEV-1");
        assertVersionString(V_1_0_1_DEV_UNOFFICIAL, "1.0.1-DEV-UNOFFICIAL");
        assertVersionString(V_1_0_1_ARTIFACT_1, "1.0.1-ARTIFACT-1");
    }

    /**
     * Test if the {@link pl.betoncraft.betonquest.utils.Updater.Version}'s have the correct DEV boolean
     */
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    public void testVersionDev() {
        assertVersionDev(V_1_0_0, false);
        assertVersionDev(V_2_0_0, false);
        assertVersionDev(V_1_1_0, false);
        assertVersionDev(V_1_0_1, false);
        assertVersionDev(V_1_0_0_DEV_1, true);
        assertVersionDev(V_1_0_0_DEV_2, true);
        assertVersionDev(V_2_0_0_DEV_1, true);
        assertVersionDev(V_1_1_0_DEV_1, true);
        assertVersionDev(V_1_0_1_DEV_1, true);
        assertVersionDev(V_1_0_1_DEV_UNOFFICIAL, false);
        assertVersionDev(V_1_0_1_ARTIFACT_1, false);
    }

    /**
     * Test if the {@link pl.betoncraft.betonquest.utils.Updater.Version}'s have the correct official boolean
     */
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    public void testVersionOfficial() {
        assertVersionOfficial(V_1_0_0, false);
        assertVersionOfficial(V_2_0_0, false);
        assertVersionOfficial(V_1_1_0, false);
        assertVersionOfficial(V_1_0_1, false);
        assertVersionOfficial(V_1_0_0_DEV_1, false);
        assertVersionOfficial(V_1_0_0_DEV_2, false);
        assertVersionOfficial(V_2_0_0_DEV_1, false);
        assertVersionOfficial(V_1_1_0_DEV_1, false);
        assertVersionOfficial(V_1_0_1_DEV_1, false);
        assertVersionOfficial(V_1_0_1_DEV_UNOFFICIAL, true);
        assertVersionOfficial(V_1_0_1_ARTIFACT_1, true);
    }

    /**
     * Compares all version combinations withe the
     * {@link pl.betoncraft.betonquest.utils.Updater.UpdateStrategy#MAJOR} strategy.
     */
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    public void testVersionCompareMajor() {
        assertMultipleVersions(Updater.UpdateStrategy.MAJOR, false, V_1_0_0, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MAJOR, true, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1);

        assertMultipleVersions(Updater.UpdateStrategy.MAJOR, false, V_2_0_0, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        assertMultipleVersions(Updater.UpdateStrategy.MAJOR, false, V_1_1_0, V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MAJOR, true, V_1_1_0, V_2_0_0);

        assertMultipleVersions(Updater.UpdateStrategy.MAJOR, false, V_1_0_1, V_1_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MAJOR, true, V_1_0_1, V_2_0_0, V_1_1_0);

        assertMultipleVersions(Updater.UpdateStrategy.MAJOR, false, V_1_0_0_DEV_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MAJOR, true, V_1_0_0_DEV_1, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1);

        assertMultipleVersions(Updater.UpdateStrategy.MAJOR, false, V_1_0_0_DEV_2, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MAJOR, true, V_1_0_0_DEV_2, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1);

        assertMultipleVersions(Updater.UpdateStrategy.MAJOR, false, V_2_0_0_DEV_1, V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MAJOR, true, V_2_0_0_DEV_1, V_2_0_0);

        assertMultipleVersions(Updater.UpdateStrategy.MAJOR, false, V_1_1_0_DEV_1, V_1_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MAJOR, true, V_1_1_0_DEV_1, V_2_0_0, V_1_1_0);

        assertMultipleVersions(Updater.UpdateStrategy.MAJOR, false, V_1_0_1_DEV_1, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MAJOR, true, V_1_0_1_DEV_1, V_2_0_0, V_1_1_0, V_1_0_1);

        assertMultipleVersions(Updater.UpdateStrategy.MAJOR, false, V_1_0_1_DEV_UNOFFICIAL, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MAJOR, true, V_1_0_1_DEV_UNOFFICIAL, V_2_0_0, V_1_1_0, V_1_0_1);

        assertMultipleVersions(Updater.UpdateStrategy.MAJOR, false, V_1_0_1_ARTIFACT_1, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MAJOR, true, V_1_0_1_ARTIFACT_1, V_2_0_0, V_1_1_0, V_1_0_1);
    }

    /**
     * Compares all version combinations withe the
     * {@link pl.betoncraft.betonquest.utils.Updater.UpdateStrategy#MINOR} strategy.
     */
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    public void testVersionCompareMinor() {
        assertMultipleVersions(Updater.UpdateStrategy.MINOR, false, V_1_0_0, V_1_0_0, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MINOR, true, V_1_0_0, V_1_1_0, V_1_0_1);

        assertMultipleVersions(Updater.UpdateStrategy.MINOR, false, V_2_0_0, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        assertMultipleVersions(Updater.UpdateStrategy.MINOR, false, V_1_1_0, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        assertMultipleVersions(Updater.UpdateStrategy.MINOR, false, V_1_0_1, V_1_0_0, V_2_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MINOR, true, V_1_0_1, V_1_1_0);

        assertMultipleVersions(Updater.UpdateStrategy.MINOR, false, V_1_0_0_DEV_1, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MINOR, true, V_1_0_0_DEV_1, V_1_0_0, V_1_1_0, V_1_0_1);

        assertMultipleVersions(Updater.UpdateStrategy.MINOR, false, V_1_0_0_DEV_2, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MINOR, true, V_1_0_0_DEV_2, V_1_0_0, V_1_1_0, V_1_0_1);

        assertMultipleVersions(Updater.UpdateStrategy.MINOR, false, V_2_0_0_DEV_1, V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MINOR, true, V_2_0_0_DEV_1, V_2_0_0);

        assertMultipleVersions(Updater.UpdateStrategy.MINOR, false, V_1_1_0_DEV_1, V_1_0_0, V_2_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MINOR, true, V_1_1_0_DEV_1, V_1_1_0);

        assertMultipleVersions(Updater.UpdateStrategy.MINOR, false, V_1_0_1_DEV_1, V_2_0_0, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MINOR, true, V_1_0_1_DEV_1, V_1_1_0, V_1_0_1);

        assertMultipleVersions(Updater.UpdateStrategy.MINOR, false, V_1_0_1_DEV_UNOFFICIAL, V_1_0_0, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MINOR, true, V_1_0_1_DEV_UNOFFICIAL, V_1_1_0, V_1_0_1);

        assertMultipleVersions(Updater.UpdateStrategy.MINOR, false, V_1_0_1_ARTIFACT_1, V_1_0_0, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MINOR, true, V_1_0_1_ARTIFACT_1, V_1_1_0, V_1_0_1);
    }

    /**
     * Compares all version combinations withe the
     * {@link pl.betoncraft.betonquest.utils.Updater.UpdateStrategy#PATCH} strategy.
     */
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    public void testVersionComparePatch() {
        assertMultipleVersions(Updater.UpdateStrategy.PATCH, false, V_1_0_0, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.PATCH, true, V_1_0_0, V_1_0_1);

        assertMultipleVersions(Updater.UpdateStrategy.PATCH, false, V_2_0_0, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        assertMultipleVersions(Updater.UpdateStrategy.PATCH, false, V_1_1_0, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        assertMultipleVersions(Updater.UpdateStrategy.PATCH, false, V_1_0_1, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        assertMultipleVersions(Updater.UpdateStrategy.PATCH, false, V_1_0_0_DEV_1, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.PATCH, true, V_1_0_0_DEV_1, V_1_0_0, V_1_0_1);

        assertMultipleVersions(Updater.UpdateStrategy.PATCH, false, V_1_0_0_DEV_2, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.PATCH, true, V_1_0_0_DEV_2, V_1_0_0, V_1_0_1);

        assertMultipleVersions(Updater.UpdateStrategy.PATCH, false, V_2_0_0_DEV_1, V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.PATCH, true, V_2_0_0_DEV_1, V_2_0_0);

        assertMultipleVersions(Updater.UpdateStrategy.PATCH, false, V_1_1_0_DEV_1, V_1_0_0, V_2_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.PATCH, true, V_1_1_0_DEV_1, V_1_1_0);

        assertMultipleVersions(Updater.UpdateStrategy.PATCH, false, V_1_0_1_DEV_1, V_2_0_0, V_1_1_0, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.PATCH, true, V_1_0_1_DEV_1, V_1_0_1);

        assertMultipleVersions(Updater.UpdateStrategy.PATCH, false, V_1_0_1_DEV_UNOFFICIAL, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.PATCH, true, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1);

        assertMultipleVersions(Updater.UpdateStrategy.PATCH, false, V_1_0_1_ARTIFACT_1, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.PATCH, true, V_1_0_1_ARTIFACT_1, V_1_0_1);
    }

    /**
     * Compares all version combinations withe the
     * {@link pl.betoncraft.betonquest.utils.Updater.UpdateStrategy#MAJOR_DEV} strategy.
     */
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    public void testVersionCompareMajorDev() {
        assertMultipleVersions(Updater.UpdateStrategy.MAJOR_DEV, false, V_1_0_0, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MAJOR_DEV, true, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1);

        assertMultipleVersions(Updater.UpdateStrategy.MAJOR_DEV, false, V_2_0_0, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        assertMultipleVersions(Updater.UpdateStrategy.MAJOR_DEV, false, V_1_1_0, V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MAJOR_DEV, true, V_1_1_0, V_2_0_0, V_2_0_0_DEV_1);

        assertMultipleVersions(Updater.UpdateStrategy.MAJOR_DEV, false, V_1_0_1, V_1_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MAJOR_DEV, true, V_1_0_1, V_2_0_0, V_1_1_0, V_2_0_0_DEV_1, V_1_1_0_DEV_1);

        assertMultipleVersions(Updater.UpdateStrategy.MAJOR_DEV, false, V_1_0_0_DEV_1, V_1_0_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MAJOR_DEV, true, V_1_0_0_DEV_1, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1);

        assertMultipleVersions(Updater.UpdateStrategy.MAJOR_DEV, false, V_1_0_0_DEV_2, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MAJOR_DEV, true, V_1_0_0_DEV_2, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1);

        assertMultipleVersions(Updater.UpdateStrategy.MAJOR_DEV, false, V_2_0_0_DEV_1, V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MAJOR_DEV, true, V_2_0_0_DEV_1, V_2_0_0);

        assertMultipleVersions(Updater.UpdateStrategy.MAJOR_DEV, false, V_1_1_0_DEV_1, V_1_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MAJOR_DEV, true, V_1_1_0_DEV_1, V_2_0_0, V_1_1_0, V_2_0_0_DEV_1);

        assertMultipleVersions(Updater.UpdateStrategy.MAJOR_DEV, false, V_1_0_1_DEV_1, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MAJOR_DEV, true, V_1_0_1_DEV_1, V_2_0_0, V_1_1_0, V_1_0_1, V_2_0_0_DEV_1, V_1_1_0_DEV_1);

        assertMultipleVersions(Updater.UpdateStrategy.MAJOR_DEV, false, V_1_0_1_DEV_UNOFFICIAL, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MAJOR_DEV, true, V_1_0_1_DEV_UNOFFICIAL, V_2_0_0, V_1_1_0, V_1_0_1, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1);

        assertMultipleVersions(Updater.UpdateStrategy.MAJOR_DEV, false, V_1_0_1_ARTIFACT_1, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MAJOR_DEV, true, V_1_0_1_ARTIFACT_1, V_2_0_0, V_1_1_0, V_1_0_1, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1);
    }

    /**
     * Compares all version combinations withe the
     * {@link pl.betoncraft.betonquest.utils.Updater.UpdateStrategy#MINOR_DEV} strategy.
     */
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    public void testVersionCompareMinorDev() {
        assertMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, false, V_1_0_0, V_1_0_0, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, true, V_1_0_0, V_1_1_0, V_1_0_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1);

        assertMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, false, V_2_0_0, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        assertMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, false, V_1_1_0, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        assertMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, false, V_1_0_1, V_1_0_0, V_2_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, true, V_1_0_1, V_1_1_0, V_1_1_0_DEV_1);

        assertMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, false, V_1_0_0_DEV_1, V_2_0_0, V_1_0_0_DEV_1, V_2_0_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, true, V_1_0_0_DEV_1, V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_2, V_1_1_0_DEV_1, V_1_0_1_DEV_1);

        assertMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, false, V_1_0_0_DEV_2, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, true, V_1_0_0_DEV_2, V_1_0_0, V_1_1_0, V_1_0_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1);

        assertMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, false, V_2_0_0_DEV_1, V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, true, V_2_0_0_DEV_1, V_2_0_0);

        assertMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, false, V_1_1_0_DEV_1, V_1_0_0, V_2_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, true, V_1_1_0_DEV_1, V_1_1_0);

        assertMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, false, V_1_0_1_DEV_1, V_2_0_0, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, true, V_1_0_1_DEV_1, V_1_1_0, V_1_0_1, V_1_1_0_DEV_1);

        assertMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, false, V_1_0_1_DEV_UNOFFICIAL, V_1_0_0, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, true, V_1_0_1_DEV_UNOFFICIAL, V_1_1_0, V_1_0_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1);

        assertMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, false, V_1_0_1_ARTIFACT_1, V_1_0_0, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, true, V_1_0_1_ARTIFACT_1, V_1_1_0, V_1_0_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1);
    }

    /**
     * Compares all version combinations withe the
     * {@link pl.betoncraft.betonquest.utils.Updater.UpdateStrategy#PATCH_DEV} strategy.
     */
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    public void testVersionComparePatchDev() {
        assertMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, false, V_1_0_0, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, true, V_1_0_0, V_1_0_1, V_1_0_1_DEV_1);

        assertMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, false, V_2_0_0, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        assertMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, false, V_1_1_0, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        assertMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, false, V_1_0_1, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        assertMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, false, V_1_0_0_DEV_1, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, true, V_1_0_0_DEV_1, V_1_0_0, V_1_0_1, V_1_0_0_DEV_2, V_1_0_1_DEV_1);

        assertMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, false, V_1_0_0_DEV_2, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, true, V_1_0_0_DEV_2, V_1_0_0, V_1_0_1, V_1_0_1_DEV_1);

        assertMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, false, V_2_0_0_DEV_1, V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, true, V_2_0_0_DEV_1, V_2_0_0);

        assertMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, false, V_1_1_0_DEV_1, V_1_0_0, V_2_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, true, V_1_1_0_DEV_1, V_1_1_0);

        assertMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, false, V_1_0_1_DEV_1, V_2_0_0, V_1_1_0, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, true, V_1_0_1_DEV_1, V_1_0_1);

        assertMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, false, V_1_0_1_DEV_UNOFFICIAL, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, true, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1, V_1_0_1_DEV_1);

        assertMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, false, V_1_0_1_ARTIFACT_1, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        assertMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, true, V_1_0_1_ARTIFACT_1, V_1_0_1, V_1_0_1_DEV_1);
    }

    private void assertMultipleVersions(final Updater.UpdateStrategy strategy, final boolean updateExpected, final Updater.Version currentVersion, final Updater.Version... targetVersions) {
        for (final Updater.Version targetVersion : targetVersions) {
            assertEquals(updateExpected, currentVersion.isNewer(targetVersion, strategy), "UpdateStrategy: '" + strategy + "', Version: '" + currentVersion.getVersion() + "', with Version: '" + targetVersion.getVersion() + "', expected: '" + updateExpected + "'!");
        }
    }

    private void assertVersionString(final Updater.Version expectedVersion, final String version) {
        assertEquals(version, expectedVersion.getVersion(), "Check Version on " + version);
    }

    private void assertVersionDev(final Updater.Version expectedVersion, final boolean isDev) {
        assertEquals(isDev, expectedVersion.isDev(), "Check Dev on " + expectedVersion.getVersion());
    }

    private void assertVersionOfficial(final Updater.Version expectedVersion, final boolean isUnofficial) {
        assertEquals(isUnofficial, expectedVersion.isUnofficial(), "Check Unofficial on " + expectedVersion.getVersion());
    }
}
