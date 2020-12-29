package pl.betoncraft.betonquest.utils;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONArray;
import org.json.JSONObject;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;

import java.io.*;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.logging.Level;

@SuppressWarnings("PMD.CommentRequired")
public class Updater {
    private static final long CHECK_DELAY = 1000 * 60 * 10;

    private static final String RELEASE_API_URL = "https://api.github.com/repos/BetonQuest/BetonQuest/releases";
    private static final String DEV_API_URL = "https://betonquest.org/api/v1/";
    private static final String DEV_API_LATEST = DEV_API_URL + "builds/latest";
    private static final String DEV_API_DOWNLOAD = DEV_API_URL + "/builds/download/:version/:versionNumber/BetonQuest.jar";

    private final BetonQuest plugin;
    private final String fileName;
    private Pair<Version, String> latest;
    private long lastCheck;

    public Updater(final BetonQuest plugin, final File file) {
        this.plugin = plugin;
        this.fileName = file.getName();
        this.latest = Pair.of(new Version(plugin.getDescription().getVersion()), null);
        searchForUpdate();
    }

    public final void searchForUpdate() {
        final UpdaterConfig config = new UpdaterConfig();
        if (!config.enabled) {
            return;
        }
        final long currentTime = new Date().getTime();
        if(lastCheck + CHECK_DELAY > currentTime) {
            return;
        }
        lastCheck = currentTime;

        new BukkitRunnable() {
            @Override
            public void run() {
                LogUtils.getLogger().log(Level.INFO, "(Autoupdater) Search for newer version...");
                try {
                    findNewDev(config);
                } catch (UnknownHostException e) {
                    LogUtils.getLogger().log(Level.WARNING, "(Autoupdater) The update url for dev builds is not reachable!");
                } catch (IOException e) {
                    LogUtils.getLogger().log(Level.WARNING, "(Autoupdater) Could not get the latest dev build number!", e);
                }
                try {
                    findNewRelease(config);
                } catch (UnknownHostException e) {
                    LogUtils.getLogger().log(Level.WARNING, "(Autoupdater) The update url for releases builds is not reachable!");
                } catch (IOException e) {
                    LogUtils.getLogger().log(Level.WARNING, "(Autoupdater) Could not get the latest release!", e);
                }
                if (latest.getValue() == null) {
                    LogUtils.getLogger().log(Level.INFO, "(Autoupdater) BetonQuest is on the newest version.");
                } else {
                    LogUtils.getLogger().log(Level.INFO, "(Autoupdater) Found newer version '" + latest.getKey().getVersion()
                            + "'" + (config.automatic ? ", it will be downloaded and automatically installed on the next restart." : ", it will be installed, if you execute '/q update'!"));
                    if (config.automatic) {
                        update(Bukkit.getConsoleSender());
                    }
                }
            }
        }.runTaskAsynchronously(BetonQuest.getInstance());
    }

    private void findNewDev(final UpdaterConfig config) throws IOException {
        final JSONObject json = new JSONObject(readStringFromURL(DEV_API_LATEST));
        final Iterator<String> keys = json.keys();
        while (keys.hasNext()) {
            final String key = keys.next();
            final String dev = json.getString(key);
            final Version version = new Version(key + "-DEV-" + dev);
            final String url = DEV_API_DOWNLOAD.replace(":versionNumber", dev).replace(":version", key);
            if (latest.getKey().isNewer(version, config.updateStrategy)) {
                latest = Pair.of(version, url);
            }
        }
    }

    private void findNewRelease(final UpdaterConfig config) throws IOException {
        final JSONArray releaseArray = new JSONArray(readStringFromURL(RELEASE_API_URL));
        for (int index = 0; index < releaseArray.length(); index++) {
            final JSONObject release = releaseArray.getJSONObject(index);
            final Version version = new Version(release.getString("tag_name").substring(1));
            final JSONArray assetsArray = release.getJSONArray("assets");
            for (int i = 0; i < assetsArray.length(); i++) {
                final JSONObject asset = assetsArray.getJSONObject(i);
                if("BetonQuest.jar".equals(asset.getString("name"))) {
                    final String url = asset.getString("browser_download_url");
                    if (latest.getKey().isNewer(version, config.updateStrategy)) {
                        latest = Pair.of(version, url);
                    }
                }
            }
        }
    }

    @SuppressWarnings("PMD.AvoidFileStream")
    private void downloadUpdate() throws QuestRuntimeException {
        LogUtils.getLogger().log(Level.INFO, "(Autoupdater) Updater started download of new version...");
        if (latest.getValue() == null) {
            throw new QuestRuntimeException("The updater is disabled or the updater did not find an update!"
                    + " Check config entry 'update.enabled' or this can depend on your update_strategy, check config entry 'update.update_strategy'.");
        }
        LogUtils.getLogger().log(Level.INFO, "(Autoupdater) The target version is '" + latest.getKey().getVersion() + "'...");
        try {
            final URL remoteFile = new URL(latest.getValue());
            try (ReadableByteChannel rbc = Channels.newChannel(remoteFile.openStream())) {
                final File folder = Bukkit.getUpdateFolderFile();
                if (!folder.mkdirs()) {
                    throw new QuestRuntimeException("The updater could not create the folder '" + folder.getName() + "'!");
                }
                final File file = new File(folder, fileName);
                if (!file.createNewFile()) {
                    throw new QuestRuntimeException("The updater could not create the file '" + file.getName() + "'!");
                }
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                }
            }
            latest = Pair.of(new Version(plugin.getDescription().getVersion()), null);
            LogUtils.getLogger().log(Level.INFO, "(Autoupdater) Download finished.");
        } catch (IOException e) {
            throw new QuestRuntimeException("Could not download the file. Try again or update manually.", e);
        }
    }

    public void update(final CommandSender sender) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    downloadUpdate();
                    if (sender != null) {
                        sender.sendMessage("§2(Autoupdater) Download finished. Restart the server to update the plugin to version '" + getUpdateVersion() + "'.");
                    }
                } catch (QuestRuntimeException e) {
                    if (sender != null) {
                        sender.sendMessage("§c(Autoupdater) " + e.getMessage());
                    }
                    LogUtils.logThrowable(e);
                }
            }
        }.runTaskAsynchronously(BetonQuest.getInstance());
    }

    public boolean isUpdateAvailable() {
        return latest.getValue() != null;
    }

    public String getUpdateVersion() {
        if (latest != null) {
            return latest.getKey().getVersion();
        }
        return null;
    }

    private String readStringFromURL(final String url) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(new URL(url).openStream(), StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            final StringBuilder builder = new StringBuilder();
            int singleChar = bufferedReader.read();
            while (singleChar != -1) {
                builder.append((char) singleChar);
                singleChar = bufferedReader.read();
            }
            return builder.toString();
        }
    }

    private class UpdaterConfig {
        private final boolean enabled;
        private final UpdateStrategy updateStrategy;
        private final boolean automatic;

        public UpdaterConfig() {
            enabled = plugin.getConfig().getBoolean("update.enabled");

            UpdateStrategy strategy = UpdateStrategy.MINOR;
            final String updateStrategyString = plugin.getConfig().getString("update.strategy");
            if (updateStrategyString == null) {
                plugin.getConfig().set("update.strategy", strategy.toString());
                plugin.saveConfig();
            } else {
                strategy = UpdateStrategy.valueOf(updateStrategyString.toUpperCase(Locale.ROOT));
            }

            if (latest.getKey().isDev() && !strategy.isDev || latest.getKey().isUnofficial()) {
                updateStrategy = strategy.toDev();
                automatic = false;
            } else {
                updateStrategy = strategy;
                if(!plugin.getConfig().isSet("update.automatic")) {
                    plugin.getConfig().set("update.automatic", true);
                    plugin.saveConfig();
                }
                automatic = plugin.getConfig().getBoolean("update.automatic");
            }
        }
    }

    public enum UpdateStrategy {
        MAJOR(false),
        MINOR(false),
        PATCH(false),
        MAJOR_DEV(true),
        MINOR_DEV(true),
        PATCH_DEV(true);

        public final boolean isDev;

        UpdateStrategy(final boolean isDev) {
            this.isDev = isDev;
        }

        public UpdateStrategy toDev() {
            switch (this) {
                case MAJOR:
                    return MAJOR_DEV;
                case MINOR:
                    return MINOR_DEV;
                case PATCH:
                    return PATCH_DEV;
                default:
                    return this;
            }
        }
    }

    public static class Version {
        public static final String DEV_TAG = "DEV-";

        private final String versionString;
        private final DefaultArtifactVersion artifactVersion;
        private final Integer dev;
        private final boolean unofficial;

        public Version(final String versionString) {
            this.versionString = versionString;
            this.artifactVersion = new DefaultArtifactVersion(versionString);

            Integer dev = null;
            boolean unofficial = false;
            final String qualifier = artifactVersion.getQualifier();
            try {
                if (qualifier != null) {
                    dev = Integer.valueOf(qualifier.substring(DEV_TAG.length()));
                }
            } catch (NumberFormatException e) {
                unofficial = true;
            }
            this.dev = dev;
            this.unofficial = unofficial;
        }

        public Version(final Version versionString) {
            this.versionString = versionString.versionString;
            this.artifactVersion = versionString.artifactVersion;
            this.dev = versionString.dev;
            this.unofficial = versionString.unofficial;
        }

        @SuppressWarnings("PMD.CyclomaticComplexity")
        public boolean isNewer(final Version version, final UpdateStrategy updateStrategy) {
            if (version.isUnofficial() || !updateStrategy.isDev && version.isDev()) {
                return false;
            }
            final int majorVersion = Integer.compare(artifactVersion.getMajorVersion(), version.artifactVersion.getMajorVersion());
            final int minorVersion = Integer.compare(artifactVersion.getMinorVersion(), version.artifactVersion.getMinorVersion());
            final int patchVersion = Integer.compare(artifactVersion.getIncrementalVersion(), version.artifactVersion.getIncrementalVersion());
            switch (updateStrategy) {
                case MAJOR:
                case MAJOR_DEV:
                    if (majorVersion > 0) {
                        return false;
                    } else if (majorVersion < 0) {
                        return true;
                    }
                case MINOR:
                case MINOR_DEV:
                    if (majorVersion == 0) {
                        if (minorVersion > 0) {
                            return false;
                        } else if (minorVersion < 0) {
                            return true;
                        }
                    }
                case PATCH:
                case PATCH_DEV:
                    if (majorVersion == 0 && minorVersion == 0) {
                        return isNewerPatch(version, patchVersion);
                    }
                default:
                    return false;
            }
        }

        private boolean isNewerPatch(final Version version, final int patchVersion) {
            if (patchVersion > 0) {
                return false;
            } else if (patchVersion < 0) {
                return true;
            } else {
                final Integer thisDev = isDev() ? dev : isUnofficial() ? 0 : null;
                final Integer targetDev = version.isDev() ? version.dev : version.isUnofficial() ? 0 : null;
                if (thisDev == null || targetDev == null) {
                    return thisDev != null;
                } else {
                    return thisDev.compareTo(targetDev) < 0;
                }
            }
        }

        public String getVersion() {
            return versionString;
        }

        public boolean isDev() {
            return dev != null;
        }

        public boolean isUnofficial() {
            return unofficial;
        }
    }
}
