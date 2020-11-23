package pl.betoncraft.betonquest.utils;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UpdaterTest {
    private final static Updater.Version V_1_0_0 = new Updater.Version("1.0.0");
    private final static Updater.Version V_2_0_0 = new Updater.Version("2.0.0");
    private final static Updater.Version V_1_1_0 = new Updater.Version("1.1.0");
    private final static Updater.Version V_1_0_1 = new Updater.Version("1.0.1");
    private final static Updater.Version V_1_0_0_DEV_1 = new Updater.Version("1.0.0-DEV-1");
    private final static Updater.Version V_1_0_0_DEV_2 = new Updater.Version("1.0.0-DEV-2");
    private final static Updater.Version V_2_0_0_DEV_1 = new Updater.Version("2.0.0-DEV-1");
    private final static Updater.Version V_1_1_0_DEV_1 = new Updater.Version("1.1.0-DEV-1");
    private final static Updater.Version V_1_0_1_DEV_1 = new Updater.Version("1.0.1-DEV-1");
    private final static Updater.Version V_1_0_1_DEV_UNOFFICIAL = new Updater.Version("1.0.1-DEV-UNOFFICIAL");
    private final static Updater.Version V_1_0_1_ARTIFACT_1 = new Updater.Version("1.0.1-ARTIFACT-1");

    public UpdaterTest() {
    }

    @Test
    public void testVersionString() {
        checkVersionString(V_1_0_0, "1.0.0");
        checkVersionString(V_2_0_0, "2.0.0");
        checkVersionString(V_1_1_0, "1.1.0");
        checkVersionString(V_1_0_1, "1.0.1");
        checkVersionString(V_1_0_0_DEV_1, "1.0.0-DEV-1");
        checkVersionString(V_1_0_0_DEV_2, "1.0.0-DEV-2");
        checkVersionString(V_2_0_0_DEV_1, "2.0.0-DEV-1");
        checkVersionString(V_1_1_0_DEV_1, "1.1.0-DEV-1");
        checkVersionString(V_1_0_1_DEV_1, "1.0.1-DEV-1");
        checkVersionString(V_1_0_1_DEV_UNOFFICIAL, "1.0.1-DEV-UNOFFICIAL");
        checkVersionString(V_1_0_1_ARTIFACT_1, "1.0.1-ARTIFACT-1");
    }

    @Test
    public void testVersionDev() {
        checkVersionDev(V_1_0_0, false);
        checkVersionDev(V_2_0_0, false);
        checkVersionDev(V_1_1_0, false);
        checkVersionDev(V_1_0_1, false);
        checkVersionDev(V_1_0_0_DEV_1, true);
        checkVersionDev(V_1_0_0_DEV_2, true);
        checkVersionDev(V_2_0_0_DEV_1, true);
        checkVersionDev(V_1_1_0_DEV_1, true);
        checkVersionDev(V_1_0_1_DEV_1, true);
        checkVersionDev(V_1_0_1_DEV_UNOFFICIAL, false);
        checkVersionDev(V_1_0_1_ARTIFACT_1, false);
    }

    @Test
    public void testVersionOfficial() {
        checkVersionOfficial(V_1_0_0, false);
        checkVersionOfficial(V_2_0_0, false);
        checkVersionOfficial(V_1_1_0, false);
        checkVersionOfficial(V_1_0_1, false);
        checkVersionOfficial(V_1_0_0_DEV_1, false);
        checkVersionOfficial(V_1_0_0_DEV_2, false);
        checkVersionOfficial(V_2_0_0_DEV_1, false);
        checkVersionOfficial(V_1_1_0_DEV_1, false);
        checkVersionOfficial(V_1_0_1_DEV_1, false);
        checkVersionOfficial(V_1_0_1_DEV_UNOFFICIAL, true);
        checkVersionOfficial(V_1_0_1_ARTIFACT_1, true);
    }

    @Test
    public void testVersionCompareMayor() {
        compareMultipleVersions(Updater.UpdateStrategy.MAYOR, false, V_1_0_0, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MAYOR, true, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1);

        compareMultipleVersions(Updater.UpdateStrategy.MAYOR, false, V_2_0_0, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        compareMultipleVersions(Updater.UpdateStrategy.MAYOR, false, V_1_1_0, V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MAYOR, true, V_1_1_0, V_2_0_0);

        compareMultipleVersions(Updater.UpdateStrategy.MAYOR, false, V_1_0_1, V_1_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MAYOR, true, V_1_0_1, V_2_0_0, V_1_1_0);

        compareMultipleVersions(Updater.UpdateStrategy.MAYOR, false, V_1_0_0_DEV_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MAYOR, true, V_1_0_0_DEV_1, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1);

        compareMultipleVersions(Updater.UpdateStrategy.MAYOR, false, V_1_0_0_DEV_2, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MAYOR, true, V_1_0_0_DEV_2, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1);

        compareMultipleVersions(Updater.UpdateStrategy.MAYOR, false, V_2_0_0_DEV_1, V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MAYOR, true, V_2_0_0_DEV_1, V_2_0_0);

        compareMultipleVersions(Updater.UpdateStrategy.MAYOR, false, V_1_1_0_DEV_1, V_1_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MAYOR, true, V_1_1_0_DEV_1, V_2_0_0, V_1_1_0);

        compareMultipleVersions(Updater.UpdateStrategy.MAYOR, false, V_1_0_1_DEV_1, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MAYOR, true, V_1_0_1_DEV_1, V_2_0_0, V_1_1_0, V_1_0_1);

        compareMultipleVersions(Updater.UpdateStrategy.MAYOR, false, V_1_0_1_DEV_UNOFFICIAL, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MAYOR, true, V_1_0_1_DEV_UNOFFICIAL, V_2_0_0, V_1_1_0, V_1_0_1);

        compareMultipleVersions(Updater.UpdateStrategy.MAYOR, false, V_1_0_1_ARTIFACT_1, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MAYOR, true, V_1_0_1_ARTIFACT_1, V_2_0_0, V_1_1_0, V_1_0_1);
    }

    @Test
    public void testVersionCompareMinor() {
        compareMultipleVersions(Updater.UpdateStrategy.MINOR, false, V_1_0_0, V_1_0_0, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MINOR, true, V_1_0_0, V_1_1_0, V_1_0_1);

        compareMultipleVersions(Updater.UpdateStrategy.MINOR, false, V_2_0_0, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        compareMultipleVersions(Updater.UpdateStrategy.MINOR, false, V_1_1_0, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        compareMultipleVersions(Updater.UpdateStrategy.MINOR, false, V_1_0_1, V_1_0_0, V_2_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MINOR, true, V_1_0_1, V_1_1_0);

        compareMultipleVersions(Updater.UpdateStrategy.MINOR, false, V_1_0_0_DEV_1, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MINOR, true, V_1_0_0_DEV_1, V_1_0_0, V_1_1_0, V_1_0_1);

        compareMultipleVersions(Updater.UpdateStrategy.MINOR, false, V_1_0_0_DEV_2, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MINOR, true, V_1_0_0_DEV_2, V_1_0_0, V_1_1_0, V_1_0_1);

        compareMultipleVersions(Updater.UpdateStrategy.MINOR, false, V_2_0_0_DEV_1, V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MINOR, true, V_2_0_0_DEV_1, V_2_0_0);

        compareMultipleVersions(Updater.UpdateStrategy.MINOR, false, V_1_1_0_DEV_1, V_1_0_0, V_2_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MINOR, true, V_1_1_0_DEV_1, V_1_1_0);

        compareMultipleVersions(Updater.UpdateStrategy.MINOR, false, V_1_0_1_DEV_1, V_2_0_0, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MINOR, true, V_1_0_1_DEV_1, V_1_1_0, V_1_0_1);

        compareMultipleVersions(Updater.UpdateStrategy.MINOR, false, V_1_0_1_DEV_UNOFFICIAL, V_1_0_0, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MINOR, true, V_1_0_1_DEV_UNOFFICIAL, V_1_1_0, V_1_0_1);

        compareMultipleVersions(Updater.UpdateStrategy.MINOR, false, V_1_0_1_ARTIFACT_1, V_1_0_0, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MINOR, true, V_1_0_1_ARTIFACT_1, V_1_1_0, V_1_0_1);
    }

    @Test
    public void testVersionComparePatch() {
        compareMultipleVersions(Updater.UpdateStrategy.PATCH, false, V_1_0_0, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.PATCH, true, V_1_0_0, V_1_0_1);

        compareMultipleVersions(Updater.UpdateStrategy.PATCH, false, V_2_0_0, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        compareMultipleVersions(Updater.UpdateStrategy.PATCH, false, V_1_1_0, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        compareMultipleVersions(Updater.UpdateStrategy.PATCH, false, V_1_0_1, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        compareMultipleVersions(Updater.UpdateStrategy.PATCH, false, V_1_0_0_DEV_1, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.PATCH, true, V_1_0_0_DEV_1, V_1_0_0, V_1_0_1);

        compareMultipleVersions(Updater.UpdateStrategy.PATCH, false, V_1_0_0_DEV_2, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.PATCH, true, V_1_0_0_DEV_2, V_1_0_0, V_1_0_1);

        compareMultipleVersions(Updater.UpdateStrategy.PATCH, false, V_2_0_0_DEV_1, V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.PATCH, true, V_2_0_0_DEV_1, V_2_0_0);

        compareMultipleVersions(Updater.UpdateStrategy.PATCH, false, V_1_1_0_DEV_1, V_1_0_0, V_2_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.PATCH, true, V_1_1_0_DEV_1, V_1_1_0);

        compareMultipleVersions(Updater.UpdateStrategy.PATCH, false, V_1_0_1_DEV_1, V_2_0_0, V_1_1_0, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.PATCH, true, V_1_0_1_DEV_1, V_1_0_1);

        compareMultipleVersions(Updater.UpdateStrategy.PATCH, false, V_1_0_1_DEV_UNOFFICIAL, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.PATCH, true, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1);

        compareMultipleVersions(Updater.UpdateStrategy.PATCH, false, V_1_0_1_ARTIFACT_1, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.PATCH, true, V_1_0_1_ARTIFACT_1, V_1_0_1);
    }

    @Test
    public void testVersionCompareMayorDev() {
        compareMultipleVersions(Updater.UpdateStrategy.MAYOR_DEV, false, V_1_0_0, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MAYOR_DEV, true, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1);

        compareMultipleVersions(Updater.UpdateStrategy.MAYOR_DEV, false, V_2_0_0, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        compareMultipleVersions(Updater.UpdateStrategy.MAYOR_DEV, false, V_1_1_0, V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MAYOR_DEV, true, V_1_1_0, V_2_0_0, V_2_0_0_DEV_1);

        compareMultipleVersions(Updater.UpdateStrategy.MAYOR_DEV, false, V_1_0_1, V_1_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MAYOR_DEV, true, V_1_0_1, V_2_0_0, V_1_1_0, V_2_0_0_DEV_1, V_1_1_0_DEV_1);

        compareMultipleVersions(Updater.UpdateStrategy.MAYOR_DEV, false, V_1_0_0_DEV_1, V_1_0_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MAYOR_DEV, true, V_1_0_0_DEV_1, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1);

        compareMultipleVersions(Updater.UpdateStrategy.MAYOR_DEV, false, V_1_0_0_DEV_2, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MAYOR_DEV, true, V_1_0_0_DEV_2, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1);

        compareMultipleVersions(Updater.UpdateStrategy.MAYOR_DEV, false, V_2_0_0_DEV_1, V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MAYOR_DEV, true, V_2_0_0_DEV_1, V_2_0_0);

        compareMultipleVersions(Updater.UpdateStrategy.MAYOR_DEV, false, V_1_1_0_DEV_1, V_1_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MAYOR_DEV, true, V_1_1_0_DEV_1, V_2_0_0, V_1_1_0, V_2_0_0_DEV_1);

        compareMultipleVersions(Updater.UpdateStrategy.MAYOR_DEV, false, V_1_0_1_DEV_1, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MAYOR_DEV, true, V_1_0_1_DEV_1, V_2_0_0, V_1_1_0, V_1_0_1, V_2_0_0_DEV_1, V_1_1_0_DEV_1);

        compareMultipleVersions(Updater.UpdateStrategy.MAYOR_DEV, false, V_1_0_1_DEV_UNOFFICIAL, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MAYOR_DEV, true, V_1_0_1_DEV_UNOFFICIAL, V_2_0_0, V_1_1_0, V_1_0_1, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1);

        compareMultipleVersions(Updater.UpdateStrategy.MAYOR_DEV, false, V_1_0_1_ARTIFACT_1, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MAYOR_DEV, true, V_1_0_1_ARTIFACT_1, V_2_0_0, V_1_1_0, V_1_0_1, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1);
    }

    @Test
    public void testVersionCompareMinorDev() {
        compareMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, false, V_1_0_0, V_1_0_0, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, true, V_1_0_0, V_1_1_0, V_1_0_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1);

        compareMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, false, V_2_0_0, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        compareMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, false, V_1_1_0, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        compareMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, false, V_1_0_1, V_1_0_0, V_2_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, true, V_1_0_1, V_1_1_0, V_1_1_0_DEV_1);

        compareMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, false, V_1_0_0_DEV_1, V_2_0_0, V_1_0_0_DEV_1, V_2_0_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, true, V_1_0_0_DEV_1, V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_2, V_1_1_0_DEV_1, V_1_0_1_DEV_1);

        compareMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, false, V_1_0_0_DEV_2, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, true, V_1_0_0_DEV_2, V_1_0_0, V_1_1_0, V_1_0_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1);

        compareMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, false, V_2_0_0_DEV_1, V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, true, V_2_0_0_DEV_1, V_2_0_0);

        compareMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, false, V_1_1_0_DEV_1, V_1_0_0, V_2_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, true, V_1_1_0_DEV_1, V_1_1_0);

        compareMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, false, V_1_0_1_DEV_1, V_2_0_0, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, true, V_1_0_1_DEV_1, V_1_1_0, V_1_0_1, V_1_1_0_DEV_1);

        compareMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, false, V_1_0_1_DEV_UNOFFICIAL, V_1_0_0, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, true, V_1_0_1_DEV_UNOFFICIAL, V_1_1_0, V_1_0_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1);

        compareMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, false, V_1_0_1_ARTIFACT_1, V_1_0_0, V_2_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.MINOR_DEV, true, V_1_0_1_ARTIFACT_1, V_1_1_0, V_1_0_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1);
    }

    @Test
    public void testVersionComparePatchDev() {
        compareMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, false, V_1_0_0, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, true, V_1_0_0, V_1_0_1, V_1_0_1_DEV_1);

        compareMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, false, V_2_0_0, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        compareMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, false, V_1_1_0, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        compareMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, false, V_1_0_1, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);

        compareMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, false, V_1_0_0_DEV_1, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, true, V_1_0_0_DEV_1, V_1_0_0, V_1_0_1, V_1_0_0_DEV_2, V_1_0_1_DEV_1);

        compareMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, false, V_1_0_0_DEV_2, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, true, V_1_0_0_DEV_2, V_1_0_0, V_1_0_1, V_1_0_1_DEV_1);

        compareMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, false, V_2_0_0_DEV_1, V_1_0_0, V_1_1_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, true, V_2_0_0_DEV_1, V_2_0_0);

        compareMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, false, V_1_1_0_DEV_1, V_1_0_0, V_2_0_0, V_1_0_1, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, true, V_1_1_0_DEV_1, V_1_1_0);

        compareMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, false, V_1_0_1_DEV_1, V_2_0_0, V_1_1_0, V_1_0_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, true, V_1_0_1_DEV_1, V_1_0_1);

        compareMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, false, V_1_0_1_DEV_UNOFFICIAL, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, true, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1, V_1_0_1_DEV_1);

        compareMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, false, V_1_0_1_ARTIFACT_1, V_1_0_0, V_2_0_0, V_1_1_0, V_1_0_0_DEV_1, V_1_0_0_DEV_2, V_2_0_0_DEV_1, V_1_1_0_DEV_1, V_1_0_1_DEV_UNOFFICIAL, V_1_0_1_ARTIFACT_1);
        compareMultipleVersions(Updater.UpdateStrategy.PATCH_DEV, true, V_1_0_1_ARTIFACT_1, V_1_0_1, V_1_0_1_DEV_1);
    }

    private void compareMultipleVersions(final Updater.UpdateStrategy strategy, final boolean updateExpected, final Updater.Version currentVersion, final Updater.Version... targetVersions) {
        for (final Updater.Version targetVersion : targetVersions) {
            assertEquals(updateExpected, currentVersion.isNewer(targetVersion, strategy), "UpdateStrategy: '" + strategy + "', Version: '" + currentVersion.getVersion() + "', with Version: '" + targetVersion.getVersion() + "', expected: '" + updateExpected + "'!");
        }
    }

    private void checkVersionString(final Updater.Version expectedVersion, final String version) {
        assertEquals(version, expectedVersion.getVersion(), "Check Version on " + version);
    }

    private void checkVersionDev(final Updater.Version expectedVersion, final boolean isDev) {
        assertEquals(isDev, expectedVersion.isDev(), "Check Dev on " + expectedVersion.getVersion());
    }

    private void checkVersionOfficial(final Updater.Version expectedVersion, final boolean isUnofficial) {
        assertEquals(isUnofficial, expectedVersion.isUnofficial(), "Check Unofficial on " + expectedVersion.getVersion());
    }
}
