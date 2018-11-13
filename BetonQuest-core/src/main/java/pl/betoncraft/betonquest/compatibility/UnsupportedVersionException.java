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
package pl.betoncraft.betonquest.compatibility;

import org.bukkit.plugin.Plugin;

/**
 * Thrown if BetonQuest tries to hook a version of a plugin that is not supported
 * <p>
 * Created on 01.07.2018.
 *
 * @author Jonas Blocher
 */
public class UnsupportedVersionException extends Exception {

    private final String currentVersion;
    private final String requiredVersion;
    private final Plugin plugin;

    public UnsupportedVersionException(Plugin plugin, String required) {
        super(String.format("%s version %s is not supported. Please install version %s!",
                plugin.getName(),
                plugin.getDescription().getVersion(),
                required));
        this.plugin = plugin;
        this.currentVersion = plugin.getDescription().getVersion();
        this.requiredVersion = required;
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
