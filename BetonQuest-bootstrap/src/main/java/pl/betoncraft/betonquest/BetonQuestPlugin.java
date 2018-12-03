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

package pl.betoncraft.betonquest;

import au.com.grieve.multi_version_plugin.MultiVersionPlugin;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class BetonQuestPlugin extends MultiVersionPlugin {

    static {
        List<String> versions = new ArrayList<>();

        // Add Server Versions
        String serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1).replace("_",".");
        System.err.println(serverVersion + " - " + Bukkit.getVersion());
        switch(serverVersion.substring(0, StringUtils.ordinalIndexOf(serverVersion, ".", 2))) {
            case "1.8":
                versions.add("1_8_R3");
            case "1.9":
                versions.add("1_9_R2");
            case "1.10":
                versions.add("1_10_R1");
            case "1.11":
                versions.add("1_11_R1");
            case "1.12":
                versions.add("1_12_R1");
        }

        // Add WorldGuard Versions
        if (isClass("com.sk89q.worldedit.BlockVector")) {
            versions.add("WG_7_0_0_S1");
        }

        initPlugin("pl.betoncraft.betonquest", "BetonQuest", versions);
    }
}
