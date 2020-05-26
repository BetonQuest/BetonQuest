package pl.betoncraft.betonquest.utils;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.maven.artifact.versioning.ComparableVersion;
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
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Iterator;
import java.util.logging.Level;

public class Updater {
    private static final String RELEASE_API_URL = "https://api.github.com/repos/BetonQuest/BetonQuest/releases";
    private static final String DEV_API_URL = "https://betonquest.pl/api/v1/";
    private static final String DEV_API_LATEST = DEV_API_URL + "builds/latest";
    private static final String DEV_API_DOWNLOAD = DEV_API_URL + "/builds/download/:version/:versionNumber/BetonQuest.jar";

    private final BetonQuest plugin;
    private final String fileName;
    private final Version localVersion;
    private final ConfigValues config;

    private Pair<Version, String> latest;

    public Updater(File file) {
        fileName = file.getName();
        plugin = BetonQuest.getInstance();
        String version = plugin.getDescription().getVersion();
        localVersion = new Version(version);
        config = new ConfigValues();
        searchForUpdate();
    }

    private void searchForUpdate() {
        latest = null;
        config.load();
        if (!config.enabled) {
            return;
        }

        if (localVersion.isDev() && !localVersion.isUnofficial()) {
            LogUtils.getLogger().log(Level.WARNING, "Detected unofficial development version. Autoupdater disabled.");
            return;
        }
        LogUtils.getLogger().log(Level.INFO, "Autoupdater enabled.");
        new BukkitRunnable() {
            @Override
            public void run() {
                LogUtils.getLogger().log(Level.INFO, "Search for newer version...");
                try {
                    findDev();
                } catch (Exception e) {
                    LogUtils.getLogger().log(Level.WARNING, "Could not get the latest dev build number!", e);
                    return;
                }
                try {
                    findRelease();
                } catch (Exception e) {
                    LogUtils.getLogger().log(Level.WARNING, "Could not get the latest release!", e);
                    return;
                }
                if (latest != null) {
                    LogUtils.getLogger().log(Level.INFO, "Found newer version '" + latest.getKey().getVersion()
                            + "'" + (config.automatic ? ", it will be downloaded on next restart/reload.": "!"));
                }
            }
        }.runTaskAsynchronously(BetonQuest.getInstance());
    }

    private void findRelease() throws Exception {
        Version highestVersion = new Version(localVersion);
        String highestVersionURL = null;
        JSONArray json = new JSONArray(readFromURL(RELEASE_API_URL));
        for (int i = 0; i < json.length(); i++) {
            JSONObject release = json.getJSONObject(i);
            Version version = new Version(release.getString("tag_name").substring(1));
            String url = release.getJSONArray("assets").getJSONObject(0).getString("browser_download_url");
            if (highestVersion.isNewer(version)) {
                highestVersion = version;
                highestVersionURL = url;
            }

        }
        latest = Pair.of(highestVersion, highestVersionURL);
    }

    private void findDev() throws Exception {
        if (!config.updateStrategy.isDev) {
            return;
        }
        Version highestVersion = new Version(localVersion);
        String highestVersionURL = null;
        JSONObject json = new JSONObject(readFromURL(DEV_API_LATEST));
        Iterator<String> keys = json.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            String dev = json.getString(key);
            Version version = new Version(key + dev);
            String url = DEV_API_DOWNLOAD.replace(":version", key).replace(":versionNumber", dev);
            if (highestVersion.isNewer(version)) {
                highestVersion = version;
                highestVersionURL = url;
            }

        }
        latest = Pair.of(highestVersion, highestVersionURL);
    }

    private void downloadUpdate() throws QuestRuntimeException {
        LogUtils.getLogger().log(Level.INFO, "Updater started download of new version...");
        if (!config.enabled) {
            throw new QuestRuntimeException("The updater is disabled in the config! Check config entry 'update.enabled'.");
        }
        if (latest == null) {
            throw new QuestRuntimeException("The updater did not find an update! This can depend on your update_strategy, check config entry 'update.update_strategy'.");
        }
        LogUtils.getLogger().log(Level.INFO, "The target version is '" + latest.getKey().getVersion() + "'...");
        try {
            URL remoteFile = new URL(latest.getValue());
            try (ReadableByteChannel rbc = Channels.newChannel(remoteFile.openStream())) {
                File folder = Bukkit.getUpdateFolderFile();
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                File file = new File(folder, fileName);
                if (!file.exists()) {
                    file.createNewFile();
                }
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                }
            }
            latest = null;
            LogUtils.getLogger().log(Level.INFO, "Download finished.");
        } catch (IOException e) {
            throw new QuestRuntimeException("Could not download the file. Try again or update manually.", e);
        }
    }

    public void update(CommandSender sender, boolean checkAutoUpdate) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if(checkAutoUpdate && !config.automatic) {
                        throw new QuestRuntimeException("The auto updater is disabled! Check config entry 'update.auto_update'.");
                    }
                    String version = getUpdateVersion();
                    downloadUpdate();
                    if (sender != null) {
                        sender.sendMessage("§2Download finished. Restart the server to update the plugin to version '" + version + "'.");
                    }
                } catch (QuestRuntimeException e) {
                    if (sender != null) {
                        sender.sendMessage("§c" + e.getMessage());
                    }
                    LogUtils.logThrowable(e);
                }
            }
        }.runTaskAsynchronously(BetonQuest.getInstance());
    }

    public boolean isUpdateAvailable() {
        return latest != null;
    }

    public String getUpdateVersion() {
        if (latest != null) {
            return latest.getKey().getVersion();
        }
        return null;
    }

    public void reload() {
        searchForUpdate();
    }

    private String readFromURL(String url) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(new URL(url).openStream()); BufferedReader br = new BufferedReader(reader)) {
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = br.read()) != -1) {
                sb.append((char) cp);
            }
            return sb.toString();
        }
    }

    private class ConfigValues {
        private boolean enabled;
        private UpdateStrategy updateStrategy;
        private boolean automatic;

        private void load() {
            plugin.getConfig().set("update.download_bugfixes", null);
            plugin.getConfig().set("update.notify_new_release", null);
            plugin.getConfig().set("update.notify_dev_build", null);

            enabled = plugin.getConfig().getBoolean("update.enabled");
            if(plugin.getConfig().isSet("update.strategy")) {
                updateStrategy = UpdateStrategy.valueOf(plugin.getConfig().getString("update.strategy").toUpperCase());
            }
            else {
                updateStrategy = UpdateStrategy.MINOR;
                plugin.getConfig().set("update.strategy", updateStrategy.toString().toLowerCase());
                plugin.saveConfig();
            }
            if(plugin.getConfig().isSet("updater.automatic")) {
                automatic = plugin.getConfig().getBoolean("updater.automatic");
            }
            else {
                automatic = true;
                plugin.getConfig().set("update.automatic", automatic);
                plugin.saveConfig();
            }
        }
    }

    public enum UpdateStrategy {
        MAYOR(false),
        MINOR(false),
        PATCH(false),
        MAYOR_DEV(true),
        MINOR_DEV(true),
        PATCH_DEV(true);

        public final boolean isDev;

        UpdateStrategy(final boolean isDev) {
            this.isDev = isDev;
        }
    }

    private class Version {
        public static final String DEV_TAG = "-DEV-";
        public static final String UNOFFICIAL_TAG = "UNOFFICIAL";
        public static final String ARTIFACT_TAG = "ARTIFACT-";

        private final String version;
        private final DefaultArtifactVersion artifactVersion;

        private final boolean dev;
        private final boolean unofficial;

        public Version(String version) {
            this.version = version;
            artifactVersion = new DefaultArtifactVersion(version);

            dev = version.contains(DEV_TAG);
            unofficial = dev && (version.contains(ARTIFACT_TAG) || version.endsWith(UNOFFICIAL_TAG));
        }

        public Version(Version v) {
            this.version = v.version;
            this.artifactVersion = v.artifactVersion;
            this.dev = v.dev;
            this.unofficial = v.unofficial;
        }

        public boolean isNewer(final Version v) {
            if (!config.updateStrategy.isDev && v.dev) {
                return false;
            }
            int mayorVersion = Integer.compare(artifactVersion.getMajorVersion(), v.artifactVersion.getMajorVersion());
            int minorVersion = Integer.compare(artifactVersion.getMinorVersion(), v.artifactVersion.getMinorVersion());
            int patchVersion = Integer.compare(artifactVersion.getIncrementalVersion(), v.artifactVersion.getIncrementalVersion());
            switch (config.updateStrategy) {
                case MAYOR:
                case MAYOR_DEV:
                    if (mayorVersion > 0) {
                        return false;
                    } else if (mayorVersion < 0) {
                        return true;
                    }
                case MINOR:
                case MINOR_DEV:
                    if (mayorVersion == 0) {
                        if (minorVersion > 0) {
                            return false;
                        } else if (minorVersion < 0) {
                            return true;
                        }
                    }
                case PATCH:
                case PATCH_DEV:
                    if (mayorVersion == 0 && minorVersion == 0) {
                        if (patchVersion > 0) {
                            return false;
                        } else if (patchVersion < 0) {
                            return true;
                        } else {
                            return new ComparableVersion(version).compareTo(new ComparableVersion(v.version)) < 0;
                        }
                    }
                default:
                    return false;
            }
        }

        public String getVersion() {
            return version;
        }

        public boolean isDev() {
            return dev;
        }

        public boolean isUnofficial() {
            return unofficial;
        }
    }
}