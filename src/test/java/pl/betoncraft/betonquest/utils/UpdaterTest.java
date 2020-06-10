package pl.betoncraft.betonquest.utils;

import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

public class UpdaterTest {

    @Test
    public void testVersionCompare() {
        Updater.Version version1 = createAndCheckVersion("1.0.0", false, false);
        Updater.Version version2 = createAndCheckVersion("2.0.0", false, false);
        Updater.Version version3 = createAndCheckVersion("1.1.0", false, false);
        Updater.Version version4 = createAndCheckVersion("1.0.1", false, false);
        Updater.Version version5 = createAndCheckVersion("1.0.0-DEV-1", true, false);
        Updater.Version version6 = createAndCheckVersion("1.0.0-DEV-2", true, false);
        Updater.Version version7 = createAndCheckVersion("2.0.0-DEV-1", true, false);
        Updater.Version version8 = createAndCheckVersion("1.1.0-DEV-1", true, false);
        Updater.Version version9 = createAndCheckVersion("1.0.1-DEV-1", true, false);
        Updater.Version version10 = createAndCheckVersion("1.0.1-DEV-UNOFFICIAL", false, true);
        Updater.Version version11 = createAndCheckVersion("1.0.1-ARTIFACT-1", false, true);

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

            assertBooleanMulti(Updater.UpdateStrategy.MAYOR, false, version10, version1, version2, version3, version4, version5, version6, version7, version8, version9, version10, version11);

            assertBooleanMulti(Updater.UpdateStrategy.MAYOR, false, version11, version1, version2, version3, version4, version5, version6, version7, version8, version9, version10, version11);
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

            assertBooleanMulti(Updater.UpdateStrategy.MINOR, false, version10, version1, version2, version3, version4, version5, version6, version7, version8, version9, version10, version11);

            assertBooleanMulti(Updater.UpdateStrategy.MINOR, false, version11, version1, version2, version3, version4, version5, version6, version7, version8, version9, version10, version11);
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

            assertBooleanMulti(Updater.UpdateStrategy.PATCH, false, version10, version1, version2, version3, version4, version5, version6, version7, version8, version9, version10, version11);

            assertBooleanMulti(Updater.UpdateStrategy.PATCH, false, version11, version1, version2, version3, version4, version5, version6, version7, version8, version9, version10, version11);
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

            assertBooleanMulti(Updater.UpdateStrategy.MAYOR_DEV, false, version10, version1, version2, version3, version4, version5, version6, version7, version8, version9, version10, version11);

            assertBooleanMulti(Updater.UpdateStrategy.MAYOR_DEV, false, version11, version1, version2, version3, version4, version5, version6, version7, version8, version9, version10, version11);
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

            assertBooleanMulti(Updater.UpdateStrategy.MINOR_DEV, false, version10, version1, version2, version3, version4, version5, version6, version7, version8, version9, version10, version11);

            assertBooleanMulti(Updater.UpdateStrategy.MINOR_DEV, false, version11, version1, version2, version3, version4, version5, version6, version7, version8, version9, version10, version11);
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

            assertBooleanMulti(Updater.UpdateStrategy.PATCH_DEV, false, version10, version1, version2, version3, version4, version5, version6, version7, version8, version9, version10, version11);

            assertBooleanMulti(Updater.UpdateStrategy.PATCH_DEV, false, version11, version1, version2, version3, version4, version5, version6, version7, version8, version9, version10, version11);
        }
    }

    private void assertBooleanMulti(Updater.UpdateStrategy strategy, boolean expected, Updater.Version v, Updater.Version... versions) {
        for (Updater.Version version : versions) {
            assertEquals(expected, v.isNewer(version, strategy), "UpdateStrategy: '" + strategy + "', Version: '" + v.getVersion() + "', with Version: '" + version.getVersion() + "', expected: '" + expected + "'!");
        }
    }

    private Updater.Version createAndCheckVersion(String v, boolean isDev, boolean isUnofficial) {
        Updater.Version version = new Updater.Version(v);
        assertEquals(v, version.getVersion(), "Check Version on " + v);
        assertEquals(isDev, version.isDev(), "Check Dev on " + v);
        assertEquals(isUnofficial, version.isUnofficial(), "Check Unofficial on " + v);
        return version;
    }
}
