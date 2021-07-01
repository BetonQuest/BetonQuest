package org.betonquest.betonquest.utils.updater;

import org.apache.commons.io.FileUtils;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.utils.Updater;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test should only ensure, that the {@link Updater} dose not break anything or downloads broken jars
 * and push them into the production server.
 */
public class UpdaterTest {

    public static final String RELEASE_FILE = "src/test/resources/updater/github.json";
    public static final String DEV_FILE = "src/test/resources/updater/latest.json";
    private static final String RELEASE_DOWNLOAD_URL = "https://github.com/BetonQuest/BetonQuest/releases/download/v1.12.0/BetonQuest.jar";
    private static final String DEV_DOWNLOAD_URL = Updater.DEV_API_DOWNLOAD.replace(":versionNumber", "3").replace(":version", "1.12.1");
    /**
     * The file that represent a downloaded jar.
     */
    private static final String UPDATE_FILE = "src/test/resources/updater/BetonQuest.jar";
    /**
     * A temporary folder where the update related file are stored.
     */
    @Rule
    public TemporaryFolder pluginFolder = new TemporaryFolder();

    /**
     * The file, of the original plugin.jar to get the name.
     */
    private File pluginFile;
    /**
     * The folder, where the update is placed in.
     */
    private File updateFolder;

    /**
     * Empty constructor.
     */
    public UpdaterTest() {
        super();
    }

    @BeforeEach
    public void setUp() throws Exception {
        pluginFile = pluginFolder.newFile("BetonQuest.jar");
        updateFolder = pluginFolder.newFolder("update");
    }

    private MockedStatic<Bukkit> prepareBukkit() {
        final MockedStatic<Bukkit> bukkit = Mockito.mockStatic(Bukkit.class);
        final BukkitScheduler scheduler = Mockito.mock(BukkitScheduler.class);
        Mockito.when(scheduler.runTaskAsynchronously(ArgumentMatchers.any(), ArgumentMatchers.any(Runnable.class))).
                thenAnswer((Answer<BukkitTask>) args -> {
                    args.<Runnable>getArgument(1).run();
                    return null;
                });
        bukkit.when(Bukkit::getScheduler).thenReturn(scheduler);
        return bukkit;
    }

    private MockedStatic<BetonQuest> prepareBetonQuest(final FileConfiguration config) {
        final MockedStatic<BetonQuest> staticBetonQuest = Mockito.mockStatic(BetonQuest.class);
        final BetonQuest betonQuest = Mockito.mock(BetonQuest.class);
        Mockito.when(betonQuest.getConfig()).thenReturn(config);
        staticBetonQuest.when(BetonQuest::getInstance).thenReturn(betonQuest);
        return staticBetonQuest;
    }

    private MockedStatic<LogUtils> prepareLogUtils() {
        final MockedStatic<LogUtils> logUtils = Mockito.mockStatic(LogUtils.class);
        logUtils.when(LogUtils::getLogger).thenReturn(Logger.getGlobal());
        return logUtils;
    }

    private FileConfiguration prepareConfig(final boolean enabled, final Updater.UpdateStrategy strategy, final boolean automatic) {
        final FileConfiguration fileConfiguration = Mockito.mock(FileConfiguration.class);
        Mockito.when(fileConfiguration.getBoolean("update.enabled", true)).thenReturn(enabled);
        Mockito.when(fileConfiguration.getString("update.strategy")).thenReturn(strategy.toString());
        Mockito.when(fileConfiguration.getBoolean("update.automatic", false)).thenReturn(automatic);
        Mockito.when(fileConfiguration.getBoolean("update.ingameNotification", true)).thenReturn(false);
        return fileConfiguration;
    }

    private MockedConstruction<URL> prepareDownloadUrls() {
        final MockedConstruction<URL> url = Mockito.mockConstruction(URL.class,
                (URL newUrl, MockedConstruction.Context context) -> {
                    final String targetUrl = (String) context.arguments().get(0);
                    final File file;
                    final boolean shouldFail;
                    if (targetUrl.equals(Updater.RELEASE_API_URL)) {
                        file = new File(RELEASE_FILE);
                        shouldFail = false;
                    } else if (targetUrl.equals(Updater.DEV_API_LATEST)) {
                        file = new File(DEV_FILE);
                        shouldFail = false;
                    } else if (targetUrl.equals(RELEASE_DOWNLOAD_URL)) {
                        file = new File(UPDATE_FILE);
                        shouldFail = false;
                    } else if (targetUrl.equals(DEV_DOWNLOAD_URL)) {
                        file = new File(UPDATE_FILE);
                        shouldFail = true;
                    } else {
                        file = null;
                        shouldFail = true;
                    }
                    final InputStream stream;
                    if (shouldFail) {
                        stream = new FileInputStream(file) {
                            @Override
                            public int read(@NotNull final byte[] bytes) throws IOException {
                                throw new IOException("Fake Exception");
                            }
                        };
                    } else {
                        stream = Files.newInputStream(file.toPath());
                    }
                    final URLConnection connection = Mockito.mock(URLConnection.class);
                    Mockito.when(connection.getInputStream()).thenReturn(stream);
                    Mockito.when(newUrl.openConnection()).thenReturn(connection);
                    Mockito.when(newUrl.openStream()).thenReturn(stream);
                });
        return url;
    }

    /**
     * Test a normal search with a MAJOR update strategy.
     */
    @Test
    public void testNormalUpdateSearch() {
        final FileConfiguration config = prepareConfig(true, Updater.UpdateStrategy.MAJOR, false);
        try (MockedStatic<Bukkit> bukkit = prepareBukkit();
             MockedStatic<BetonQuest> staticBetonQuest = prepareBetonQuest(config);
             MockedConstruction<URL> urls = prepareDownloadUrls();
             MockedStatic<LogUtils> logger = prepareLogUtils()) {
            final Updater updater = new Updater("1.11.0", pluginFile);
            assertEquals("1.12.0", updater.getUpdateVersion(), "The target version is not correct!");
        }
    }

    /**
     * Test a forced search with a MAJOR update strategy while on a DEV build.
     */
    @Test
    public void testForceUpdateSearch() {
        final FileConfiguration config = prepareConfig(true, Updater.UpdateStrategy.MAJOR, false);
        try (MockedStatic<Bukkit> bukkit = prepareBukkit(); MockedStatic<BetonQuest> staticBetonQuest = prepareBetonQuest(config)) {
            final Updater updater = new Updater("1.12.0-DEV-2", pluginFile);
            assertEquals("1.12.0", updater.getUpdateVersion(), "The target version is not correct!");
        }
    }

    /**
     * Test a normal search with a DEV update strategy.
     */
    @Test
    public void testNormalDevUpdateSearch() {
        final FileConfiguration config = prepareConfig(true, Updater.UpdateStrategy.MAJOR_DEV, false);
        try (MockedStatic<Bukkit> bukkit = prepareBukkit(); MockedStatic<BetonQuest> staticBetonQuest = prepareBetonQuest(config)) {
            final Updater updater = new Updater("1.12.0-DEV-1", pluginFile);
            assertEquals("1.12.1-DEV-3", updater.getUpdateVersion(), "The target version is not correct!");
        }
    }

    /**
     * Test a normal download.
     *
     * @throws IOException Thrown then there are unexpected test failures.
     */
    @Test
    public void testNormalUpdate() throws IOException {
        final FileConfiguration config = prepareConfig(true, Updater.UpdateStrategy.MAJOR, false);
        try (MockedStatic<Bukkit> bukkit = prepareBukkit(); MockedStatic<BetonQuest> staticBetonQuest = prepareBetonQuest(config)) {
            final Updater updater = new Updater("1.12.0-DEV-1", pluginFile);
            updater.update(null);

            final File updateFile = new File(updateFolder, pluginFile.getName());
            assertTrue(FileUtils.contentEquals(updateFile, new File(UPDATE_FILE)),
                    "The received file is not equal to the expected one!");
        }
    }

    /**
     * Test a normal download with automatic.
     *
     * @throws IOException Thrown then there are unexpected test failures.
     */
    @Test
    public void testNormalUpdateAutomatic() throws IOException {
        final FileConfiguration config = prepareConfig(true, Updater.UpdateStrategy.MAJOR, true);
        try (MockedStatic<Bukkit> bukkit = prepareBukkit(); MockedStatic<BetonQuest> staticBetonQuest = prepareBetonQuest(config)) {
            new Updater("1.11.0", pluginFile);

            final File updateFile = new File(updateFolder, pluginFile.getName());
            assertTrue(FileUtils.contentEquals(updateFile, new File(UPDATE_FILE)),
                    "The received file is not equal to the expected one!");
        }
    }

    /**
     * Test a major download with automatic but with dev version.
     */
    @Test
    public void testMajorWithDevUpdateAutomatic() {
        final FileConfiguration config = prepareConfig(true, Updater.UpdateStrategy.MAJOR, true);
        try (MockedStatic<Bukkit> bukkit = prepareBukkit(); MockedStatic<BetonQuest> staticBetonQuest = prepareBetonQuest(config)) {
            new Updater("1.12.0-DEV-3", pluginFile);

            final File updateFile = new File(updateFolder, pluginFile.getName());
            assertFalse(updateFile.exists(), "The received file should not exist!");
        }
    }

    /**
     * Test disabled updater.
     */
    @Test
    public void testDisablesUpdater() {
        final FileConfiguration config = prepareConfig(false, Updater.UpdateStrategy.MAJOR, false);
        try (MockedStatic<Bukkit> bukkit = prepareBukkit(); MockedStatic<BetonQuest> staticBetonQuest = prepareBetonQuest(config)) {
            final Updater updater = new Updater("1.12.0-DEV-2", pluginFile);

            assertFalse(updater.isUpdateAvailable(), "There should no update exist!");
        }
    }

    /**
     * Test broken download.
     */
    @Test
    public void testTransferException() {
        final FileConfiguration config = prepareConfig(true, Updater.UpdateStrategy.MAJOR_DEV, false);
        try (MockedStatic<Bukkit> bukkit = prepareBukkit(); MockedStatic<BetonQuest> staticBetonQuest = prepareBetonQuest(config)) {
            final Updater updater = new Updater("1.12.0-DEV-2", pluginFile);
            updater.update(null);

            final File updateFile = new File(updateFolder, pluginFile.getName());
            assertFalse(updateFile.exists(), "The received file should not exist!");
        }
    }
}
