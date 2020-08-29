package pl.betoncraft.betonquest.utils;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UpdaterTest {

    public UpdaterTest() {}

    @Test
    public void testVersionCompare() {
        final Updater.Version version1 = createAndCheckVersion("1.0.0", false, false);
        final Updater.Version version2 = createAndCheckVersion("2.0.0", false, false);
        final Updater.Version version3 = createAndCheckVersion("1.1.0", false, false);
        final Updater.Version version4 = createAndCheckVersion("1.0.1", false, false);
        final Updater.Version version5 = createAndCheckVersion("1.0.0-DEV-1", true, false);
        final Updater.Version version6 = createAndCheckVersion("1.0.0-DEV-2", true, false);
        final Updater.Version version7 = createAndCheckVersion("2.0.0-DEV-1", true, false);
        final Updater.Version version8 = createAndCheckVersion("1.1.0-DEV-1", true, false);
        final Updater.Version version9 = createAndCheckVersion("1.0.1-DEV-1", true, false);
        final Updater.Version version10 = createAndCheckVersion("1.0.1-DEV-UNOFFICIAL", false, true);
        final Updater.Version version11 = createAndCheckVersion("1.0.1-ARTIFACT-1", false, true);

        {
            assertBooleanMulti(Updater.UpdateStrategy.MAYOR, false, version1, version1, version5, version6, version7, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MAYOR, true, version1, version2, version3, version4);

            assertBooleanMulti(Updater.UpdateStrategy.MAYOR, false, version2, version1, version2, version3, version4, version5, version6, version7, version8, version9, version10, version11);

            assertBooleanMulti(Updater.UpdateStrategy.MAYOR, false, version3, version1, version3, version4, version5, version6, version7, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MAYOR, true, version3, version2);

            assertBooleanMulti(Updater.UpdateStrategy.MAYOR, false, version4, version1, version4, version5, version6, version7, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MAYOR, true, version4, version2, version3);

            assertBooleanMulti(Updater.UpdateStrategy.MAYOR, false, version5, version5, version6, version7, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MAYOR, true, version5, version1, version2, version3, version4);

            assertBooleanMulti(Updater.UpdateStrategy.MAYOR, false, version6, version5, version6, version7, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MAYOR, true, version6, version1, version2, version3, version4);

            assertBooleanMulti(Updater.UpdateStrategy.MAYOR, false, version7, version1, version3, version4, version5, version6, version7, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MAYOR, true, version7, version2);

            assertBooleanMulti(Updater.UpdateStrategy.MAYOR, false, version8, version1, version4, version5, version6, version7, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MAYOR, true, version8, version2, version3);

            assertBooleanMulti(Updater.UpdateStrategy.MAYOR, false, version9, version1, version5, version6, version7, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MAYOR, true, version9, version2, version3, version4);

            assertBooleanMulti(Updater.UpdateStrategy.MAYOR, false, version10, version1, version5, version6, version7, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MAYOR, true, version10, version2, version3, version4);

            assertBooleanMulti(Updater.UpdateStrategy.MAYOR, false, version11, version1, version5, version6, version7, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MAYOR, true, version11, version2, version3, version4);
        }

        {
            assertBooleanMulti(Updater.UpdateStrategy.MINOR, false, version1, version1, version2, version5, version6, version7, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MINOR, true, version1, version3, version4);

            assertBooleanMulti(Updater.UpdateStrategy.MINOR, false, version2, version1, version2, version3, version4, version5, version6, version7, version8, version9, version10, version11);

            assertBooleanMulti(Updater.UpdateStrategy.MINOR, false, version3, version1, version2, version3, version4, version5, version6, version7, version8, version9, version10, version11);

            assertBooleanMulti(Updater.UpdateStrategy.MINOR, false, version4, version1, version2, version4, version5, version6, version7, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MINOR, true, version4, version3);

            assertBooleanMulti(Updater.UpdateStrategy.MINOR, false, version5, version2, version5, version6, version7, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MINOR, true, version5, version1, version3, version4);

            assertBooleanMulti(Updater.UpdateStrategy.MINOR, false, version6, version2, version5, version6, version7, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MINOR, true, version6, version1, version3, version4);

            assertBooleanMulti(Updater.UpdateStrategy.MINOR, false, version7, version1, version3, version4, version5, version6, version7, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MINOR, true, version7, version2);

            assertBooleanMulti(Updater.UpdateStrategy.MINOR, false, version8, version1, version2, version4, version5, version6, version7, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MINOR, true, version8, version3);

            assertBooleanMulti(Updater.UpdateStrategy.MINOR, false, version9, version2, version1, version5, version6, version7, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MINOR, true, version9, version3, version4);

            assertBooleanMulti(Updater.UpdateStrategy.MINOR, false, version10, version1, version2, version5, version6, version7, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MINOR, true, version10, version3, version4);

            assertBooleanMulti(Updater.UpdateStrategy.MINOR, false, version11, version1, version2, version5, version6, version7, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MINOR, true, version11, version3, version4);
        }

        {
            assertBooleanMulti(Updater.UpdateStrategy.PATCH, false, version1, version1, version2, version3, version5, version6, version7, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.PATCH, true, version1, version4);

            assertBooleanMulti(Updater.UpdateStrategy.PATCH, false, version2, version1, version2, version3, version4, version5, version6, version7, version8, version9, version10, version11);

            assertBooleanMulti(Updater.UpdateStrategy.PATCH, false, version3, version1, version2, version3, version4, version5, version6, version7, version8, version9, version10, version11);

            assertBooleanMulti(Updater.UpdateStrategy.PATCH, false, version4, version1, version2, version3, version4, version5, version6, version7, version8, version9, version10, version11);

            assertBooleanMulti(Updater.UpdateStrategy.PATCH, false, version5, version2, version3, version5, version6, version7, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.PATCH, true, version5, version1, version4);

            assertBooleanMulti(Updater.UpdateStrategy.PATCH, false, version6, version2, version3, version5, version6, version7, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.PATCH, true, version6, version1, version4);

            assertBooleanMulti(Updater.UpdateStrategy.PATCH, false, version7, version1, version3, version4, version5, version6, version7, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.PATCH, true, version7, version2);

            assertBooleanMulti(Updater.UpdateStrategy.PATCH, false, version8, version1, version2, version4, version5, version6, version7, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.PATCH, true, version8, version3);

            assertBooleanMulti(Updater.UpdateStrategy.PATCH, false, version9, version2, version3, version1, version5, version6, version7, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.PATCH, true, version9, version4);

            assertBooleanMulti(Updater.UpdateStrategy.PATCH, false, version10, version1, version2, version3, version5, version6, version7, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.PATCH, true, version10, version4);

            assertBooleanMulti(Updater.UpdateStrategy.PATCH, false, version11, version1, version2, version3, version5, version6, version7, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.PATCH, true, version11, version4);
        }

        {
            assertBooleanMulti(Updater.UpdateStrategy.MAYOR_DEV, false, version1, version1, version5, version6, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MAYOR_DEV, true, version1, version2, version3, version4, version7, version8, version9);

            assertBooleanMulti(Updater.UpdateStrategy.MAYOR_DEV, false, version2, version1, version2, version3, version4, version5, version6, version7, version8, version9, version10, version11);

            assertBooleanMulti(Updater.UpdateStrategy.MAYOR_DEV, false, version3, version1, version3, version4, version5, version6, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MAYOR_DEV, true, version3, version2, version7);

            assertBooleanMulti(Updater.UpdateStrategy.MAYOR_DEV, false, version4, version1, version4, version5, version6, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MAYOR_DEV, true, version4, version2, version3, version7, version8);

            assertBooleanMulti(Updater.UpdateStrategy.MAYOR_DEV, false, version5, version5, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MAYOR_DEV, true, version5, version1, version2, version3, version4, version6, version7, version8, version9);

            assertBooleanMulti(Updater.UpdateStrategy.MAYOR_DEV, false, version6, version5, version6, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MAYOR_DEV, true, version6, version1, version2, version3, version4, version7, version8, version9);

            assertBooleanMulti(Updater.UpdateStrategy.MAYOR_DEV, false, version7, version1, version3, version4, version5, version6, version7, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MAYOR_DEV, true, version7, version2);

            assertBooleanMulti(Updater.UpdateStrategy.MAYOR_DEV, false, version8, version1, version4, version5, version6, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MAYOR_DEV, true, version8, version2, version3, version7);

            assertBooleanMulti(Updater.UpdateStrategy.MAYOR_DEV, false, version9, version1, version5, version6, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MAYOR_DEV, true, version9, version2, version3, version4, version7, version8);

            assertBooleanMulti(Updater.UpdateStrategy.MAYOR_DEV, false, version10, version1, version5, version6, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MAYOR_DEV, true, version10, version2, version3, version4, version7, version8, version9);

            assertBooleanMulti(Updater.UpdateStrategy.MAYOR_DEV, false, version11, version1, version5, version6, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MAYOR_DEV, true, version11, version2, version3, version4, version7, version8, version9);
        }

        {
            assertBooleanMulti(Updater.UpdateStrategy.MINOR_DEV, false, version1, version1, version2, version5, version6, version7, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MINOR_DEV, true, version1, version3, version4, version8, version9);

            assertBooleanMulti(Updater.UpdateStrategy.MINOR_DEV, false, version2, version1, version2, version3, version4, version5, version6, version7, version8, version9, version10, version11);

            assertBooleanMulti(Updater.UpdateStrategy.MINOR_DEV, false, version3, version1, version2, version3, version4, version5, version6, version7, version8, version9, version10, version11);

            assertBooleanMulti(Updater.UpdateStrategy.MINOR_DEV, false, version4, version1, version2, version4, version5, version6, version7, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MINOR_DEV, true, version4, version3, version8);

            assertBooleanMulti(Updater.UpdateStrategy.MINOR_DEV, false, version5, version2, version5, version7, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MINOR_DEV, true, version5, version1, version3, version4, version6, version8, version9);

            assertBooleanMulti(Updater.UpdateStrategy.MINOR_DEV, false, version6, version2, version5, version6, version7, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MINOR_DEV, true, version6, version1, version3, version4, version8, version9);

            assertBooleanMulti(Updater.UpdateStrategy.MINOR_DEV, false, version7, version1, version3, version4, version5, version6, version7, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MINOR_DEV, true, version7, version2);

            assertBooleanMulti(Updater.UpdateStrategy.MINOR_DEV, false, version8, version1, version2, version4, version5, version6, version7, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MINOR_DEV, true, version8, version3);

            assertBooleanMulti(Updater.UpdateStrategy.MINOR_DEV, false, version9, version2, version1, version5, version6, version7, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MINOR_DEV, true, version9, version3, version4, version8);

            assertBooleanMulti(Updater.UpdateStrategy.MINOR_DEV, false, version10, version1, version2, version5, version6, version7, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MINOR_DEV, true, version10, version3, version4, version8, version9);

            assertBooleanMulti(Updater.UpdateStrategy.MINOR_DEV, false, version11, version1, version2, version5, version6, version7, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.MINOR_DEV, true, version11, version3, version4, version8, version9);
        }

        {
            assertBooleanMulti(Updater.UpdateStrategy.PATCH_DEV, false, version1, version1, version2, version3, version5, version6, version7, version8, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.PATCH_DEV, true, version1, version4, version9);

            assertBooleanMulti(Updater.UpdateStrategy.PATCH_DEV, false, version2, version1, version2, version3, version4, version5, version6, version7, version8, version9, version10, version11);

            assertBooleanMulti(Updater.UpdateStrategy.PATCH_DEV, false, version3, version1, version2, version3, version4, version5, version6, version7, version8, version9, version10, version11);

            assertBooleanMulti(Updater.UpdateStrategy.PATCH_DEV, false, version4, version1, version2, version3, version4, version5, version6, version7, version8, version9, version10, version11);

            assertBooleanMulti(Updater.UpdateStrategy.PATCH_DEV, false, version5, version2, version3, version5, version7, version8, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.PATCH_DEV, true, version5, version1, version4, version6, version9);

            assertBooleanMulti(Updater.UpdateStrategy.PATCH_DEV, false, version6, version2, version3, version5, version6, version7, version8, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.PATCH_DEV, true, version6, version1, version4, version9);

            assertBooleanMulti(Updater.UpdateStrategy.PATCH_DEV, false, version7, version1, version3, version4, version5, version6, version7, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.PATCH_DEV, true, version7, version2);

            assertBooleanMulti(Updater.UpdateStrategy.PATCH_DEV, false, version8, version1, version2, version4, version5, version6, version7, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.PATCH_DEV, true, version8, version3);

            assertBooleanMulti(Updater.UpdateStrategy.PATCH_DEV, false, version9, version2, version3, version1, version5, version6, version7, version8, version9, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.PATCH_DEV, true, version9, version4);

            assertBooleanMulti(Updater.UpdateStrategy.PATCH_DEV, false, version10, version1, version2, version3, version5, version6, version7, version8, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.PATCH_DEV, true, version10, version4, version9);

            assertBooleanMulti(Updater.UpdateStrategy.PATCH_DEV, false, version11, version1, version2, version3, version5, version6, version7, version8, version10, version11);
            assertBooleanMulti(Updater.UpdateStrategy.PATCH_DEV, true, version11, version4, version9);
        }
    }

    private void assertBooleanMulti(final Updater.UpdateStrategy strategy, final boolean expected, final Updater.Version currentVersion, final Updater.Version... targetVersions) {
        for (final Updater.Version targetVersion : targetVersions) {
            assertEquals(expected, currentVersion.isNewer(targetVersion, strategy), "UpdateStrategy: '" + strategy + "', Version: '" + currentVersion.getVersion() + "', with Version: '" + targetVersion.getVersion() + "', expected: '" + expected + "'!");
        }
    }

    private Updater.Version createAndCheckVersion(final String version, final boolean isDev, final boolean isUnofficial) {
        final Updater.Version expectedVersion = new Updater.Version(version);
        assertEquals(version, expectedVersion.getVersion(), "Check Version on " + version);
        assertEquals(isDev, expectedVersion.isDev(), "Check Dev on " + version);
        assertEquals(isUnofficial, expectedVersion.isUnofficial(), "Check Unofficial on " + version);
        return expectedVersion;
    }
}
