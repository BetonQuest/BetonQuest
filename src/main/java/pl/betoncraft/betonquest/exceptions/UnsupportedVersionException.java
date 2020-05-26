/*
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

/*
 * Created on 01.07.2018.
 *
 * @author Jonas Blocher
 */
package pl.betoncraft.betonquest.exceptions;

import org.bukkit.plugin.Plugin;

/**
 * Thrown if BetonQuest tries to hook a version of a plugin that is not
 * supported
 */
public class UnsupportedVersionException extends Exception {

    private static final long serialVersionUID = 7965553395053833302L;

    /**
     * The version of the running plugin
     */
    private final String currentVersion;
    /**
     * The supported version of the plugin
     */
    private final String requiredVersion;
    /**
     * The plugin
     */
    private final Plugin plugin;

    /**
     * Constructs a new exception
     * 
     * @param plugin
     *            The plugin, in the wrong version
     * @param requiredVersion
     *            The expected version
     */
    public UnsupportedVersionException(final Plugin plugin, final String requiredVersion) {
        super(String.format("%s version %s is not supported. Please install version %s!",
                plugin.getName(),
                plugin.getDescription().getVersion(),
                requiredVersion));
        this.plugin = plugin;
        this.currentVersion = plugin.getDescription().getVersion();
        this.requiredVersion = requiredVersion;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public String getRequiredVersion() {
        return requiredVersion;
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
