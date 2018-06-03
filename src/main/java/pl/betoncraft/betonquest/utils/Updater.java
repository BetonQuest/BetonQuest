/**
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONArray;
import org.json.JSONObject;

import pl.betoncraft.betonquest.BetonQuest;

/**
 * Updates the plugin to the newest version and displays notifications about new
 * releases.
 *
 * @author Jakub Sapalski
 */
public class Updater {

	private static final String RELEASE_API_URL = "https://api.github.com/repos/Co0sh/BetonQuest/releases";
	private static final String DEV_API_URL = "https://betonquest.pl/latest.txt";
	private static final String DEV_DOWNLOAD_LINK = "https://betonquest.pl/{number}/BetonQuest.jar";

	private BetonQuest plugin;
	private String fileName; // name of the plugin file
	private boolean enabled = true;

	// configuration settings
	private boolean updateBugFixes;
	private boolean notifyDevBuild;
	private boolean notifyNewRelease;

	private boolean isOfficial;
	private boolean isDevBuild;
	private int devBuildNumber;

	// if these are null it means there is no update of this type
	private String releaseAddress; // this can be the same as bugfixAddress
	private String bugfixAddress;
	private String devBuildAddress;

	// version strings
	private String remoteRelease;
	private String remoteBugfix;
	private String remoteDevBuild;

	/**
	 * Initializes the updater. Does not do anything if updater is disabled in
	 * the config.
	 *
	 * @param file
	 *            the file to which the update will be saved
	 */
	public Updater(File file) {
		fileName = file.getName();
		plugin = BetonQuest.getInstance();
		// do nothing if the autoupdate is disabled
		if (!plugin.getConfig().getBoolean("update.enabled")) {
			enabled = false;
			return;
		}
		String version = plugin.getDescription().getVersion();
		final Version locVer = new Version();
		isDevBuild = version.contains("dev");
		isOfficial = isDevBuild && version.contains("#");
		// parse current version string to get all required information
		// correct version string is in one of those formats:
		// "1.2", "1.2.3", "1.2-dev", "1.2.3-dev", "1.2-dev#3" or "1.2.3-dev#4"
		try {
			// just keep parsing, if there are any errors it means that version
			// string is incorrect and should not be parsed
			String[] numbers = version.split("-")[0].split("\\.");
			locVer.coreVersion = Integer.parseInt(numbers[0]);
			locVer.majorVersion = Integer.parseInt(numbers[1]);
			if (numbers.length > 2)
				locVer.bugfixVersion = Integer.parseInt(numbers[2]);
			if (isDevBuild && isOfficial) {
				String raw = version.split("#")[1];
				devBuildNumber = Integer.parseInt(raw);
			}
		} catch (Exception e) {
			Debug.broadcast("Could not parse version string: '" + version + "'. Autoupdater disabled.");
			return;
		}
		if (isDevBuild && !isOfficial) {
			// this is unofficial dev build, compiled by the developer
			Debug.broadcast("Detected unofficial development version. Autoupdater disabled.");
			return;
		}
		// read updater settings from configuration
		load();
		Debug.broadcast("Autoupdater enabled!");
		// check for updates
		new BukkitRunnable() {
			@Override
			public void run() {
				// handle checking dev build server
				try {
					int remoteDevBuildNumber = Integer.parseInt(readFromURL(DEV_API_URL));
					remoteDevBuild = "#" + remoteDevBuildNumber;
					if (devBuildNumber < remoteDevBuildNumber) {
						devBuildAddress = DEV_DOWNLOAD_LINK.replace("{number}", String.valueOf(remoteDevBuildNumber));
					}
				} catch (IOException | NumberFormatException e) {
					Debug.error("Could not get the latest dev build number");
					return;
				}
				// handle checking github releases
				try {
					HashMap<Version, String> remoteVersions = new HashMap<>();
					Version highestRelease = new Version(locVer.coreVersion, locVer.majorVersion, locVer.bugfixVersion);
					Version highestBugfix = new Version(locVer.coreVersion, locVer.majorVersion, locVer.bugfixVersion);
					JSONArray json = new JSONArray(readFromURL(RELEASE_API_URL));
					for (int i = 0; i < json.length(); i++) {
						// read all info from each release and put it into the
						// hashmap
						JSONObject release = json.getJSONObject(i);
						Version version = parseTagVersion(release.getString("tag_name"));
						String url = release.getJSONArray("assets").getJSONObject(0).getString("browser_download_url");
						remoteVersions.put(version, url);
						// check if this release is an update target
						if (version.coreVersion == locVer.coreVersion) {
							if (version.majorVersion >= highestRelease.majorVersion) {
								// if the version is higher than what we found
								// already, make sure bugfix version is also
								// updated
								if (version.majorVersion > highestRelease.majorVersion) {
									highestRelease.bugfixVersion = version.bugfixVersion;
								}
								highestRelease.majorVersion = version.majorVersion;
								if (version.bugfixVersion >= highestRelease.bugfixVersion) {
									highestRelease.bugfixVersion = version.bugfixVersion;
								} else {

								}
							}
							if (version.majorVersion == locVer.majorVersion) {
								if (version.bugfixVersion >= highestBugfix.bugfixVersion) {
									highestBugfix.bugfixVersion = version.bugfixVersion;
								}
							}
						}
					}
					// if the update targets are different that current version,
					// get their urls
					if (!isDevBuild && !highestBugfix.equals(locVer)) {
						for (Version v : remoteVersions.keySet()) {
							if (highestBugfix.equals(v)) {
								bugfixAddress = remoteVersions.get(v);
								break;
							}
						}
						remoteBugfix = highestBugfix.toString();
					}
					if (!highestRelease.equals(locVer) || isDevBuild) {
						// if it's dev build, it will try to get the address of
						// the update. if there is no such
						// address in the map it means that there is no update
						// yet
						for (Version v : remoteVersions.keySet()) {
							if (highestRelease.equals(v)) {
								releaseAddress = remoteVersions.get(v);
								break;
							}
						}
						remoteRelease = highestRelease.toString();
					}
				} catch (Exception e) {
					Debug.error("Could not get the latest release");
				}
				// display notifications
				new BukkitRunnable() {
					@Override
					public void run() {
						if (updateBugFixes && bugfixAddress != null) {
							Debug.broadcast("Found bugfix version: " + remoteBugfix
									+ ", it will be downloaded on next restart/reload.");
						}
						if (notifyNewRelease && releaseAddress != null && !remoteRelease.equals(remoteBugfix)) {
							Debug.broadcast(
									"Found new release: " + remoteRelease + ", use '/q update' to download it.");
						}
						if (notifyDevBuild && devBuildAddress != null) {
							Debug.broadcast("Found new development build: " + remoteDevBuild
									+ ", use '/q update --dev' to download it.");
						}
					}
				}.runTask(plugin);
			}
		}.runTaskAsynchronously(plugin);
	}

	private void load() {
		updateBugFixes = plugin.getConfig().getBoolean("update.download_bugfixes");
		notifyNewRelease = plugin.getConfig().getBoolean("update.notify_new_release");
		// if it's an official dev build and there is no dev option, try to add
		// it
		if (isDevBuild && isOfficial) {
			if (plugin.getConfig().isSet("update.notify_dev_build")) {
				notifyDevBuild = plugin.getConfig().getBoolean("update.notify_dev_build");
			} else {
				notifyDevBuild = true;
				plugin.getConfig().set("update.notify_dev_build", true);
				plugin.saveConfig();
			}
		} else {
			if (plugin.getConfig().isSet("update.notify_dev_build")) {
				plugin.getConfig().set("update.notify_dev_build", null);
				plugin.saveConfig();
			}
			notifyDevBuild = false;
		}
	}

	private String readFromURL(String url) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = br.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	private Version parseTagVersion(String tag) throws Exception {
		tag = tag.replace("v", "");
		String[] numbers = tag.split("\\.");
		Version version = new Version();
		version.coreVersion = Integer.parseInt(numbers[0]);
		version.majorVersion = Integer.parseInt(numbers[1]);
		if (numbers.length > 2)
			version.bugfixVersion = Integer.parseInt(numbers[2]);
		return version;

	}

	private void downloadUpdate(final String address, final CommandSender sender) {
		try {
			URL remoteFile = new URL(address);
			ReadableByteChannel rbc = Channels.newChannel(remoteFile.openStream());
			File folder = Bukkit.getUpdateFolderFile();
			if (!folder.exists()) {
				folder.mkdirs();
			}
			File file = new File(folder, fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
			if (sender != null) {
				sender.sendMessage("§2Download finished. Restart/reload the server to update the plugin.");
			} else {
				Debug.broadcast("Download finished.");
			}
		} catch (IOException e) {
			if (sender != null) {
				sender.sendMessage("§cCould not download the file. Try again or update manually.");
			} else {
				Debug.broadcast("Could not download the file.");
			}
		}
	}

	public boolean updateBugfixes() {
		if (enabled && updateBugFixes && bugfixAddress != null) {
			Debug.broadcast("Downloading bugfix version " + remoteBugfix);
			downloadUpdate(bugfixAddress, null);
			return true;
		}
		return false;
	}

	public boolean updateNewRelease(final CommandSender sender) {
		if (enabled && releaseAddress != null) {
			new BukkitRunnable() {
				@Override
				public void run() {
					downloadUpdate(releaseAddress, sender);
					bugfixAddress = null; // this will prevent bugfixes from
											// overwriting the release
				}
			}.runTaskAsynchronously(plugin);
			return true;
		}
		return false;
	}

	public boolean updateDevBuild(final CommandSender sender) {
		if (enabled && devBuildAddress != null) {
			new BukkitRunnable() {
				@Override
				public void run() {
					downloadUpdate(devBuildAddress, sender);
					bugfixAddress = null;
				}
			}.runTaskAsynchronously(plugin);
			return true;
		}
		return false;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public String getRemoteRelease() {
		return remoteRelease;
	}

	public String getRemoteBugfix() {
		return remoteBugfix;
	}

	public String getRemoteDevBuild() {
		return remoteDevBuild;
	}

	public void reload() {
		enabled = plugin.getConfig().getBoolean("update.enabled");
		load();
	}

	private class Version {
		int coreVersion = 0;
		int majorVersion = 0;
		int bugfixVersion = 0;

		public Version() {
		}

		public Version(int a, int b, int c) {
			coreVersion = a;
			majorVersion = b;
			bugfixVersion = c;
		}

		@Override
		public String toString() {
			return "v" + coreVersion + "." + majorVersion + "." + bugfixVersion;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Version) {
				Version v = (Version) o;
				return coreVersion == v.coreVersion && majorVersion == v.majorVersion
						&& bugfixVersion == v.bugfixVersion;
			}
			return false;
		}
	}

}
