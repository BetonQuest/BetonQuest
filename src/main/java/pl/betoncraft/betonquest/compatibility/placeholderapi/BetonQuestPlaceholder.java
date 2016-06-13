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
package pl.betoncraft.betonquest.compatibility.placeholderapi;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.clip.placeholderapi.external.EZPlaceholderHook;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class BetonQuestPlaceholder extends EZPlaceholderHook {

	public BetonQuestPlaceholder(Plugin plugin, String identifier) {
		super(plugin, identifier);
	}

	@Override
	public String onPlaceholderRequest(Player player, String identifier) {
		String pack = null;
		if (identifier.contains(":")) {
			pack = identifier.substring(0, identifier.indexOf(':'));
			identifier = identifier.substring(identifier.indexOf(':') + 1);
		} else {
			pack = BetonQuest.getInstance().getConfig().getString("default_package", "default");
		}
		return BetonQuest.getInstance().getVariableValue(pack, '%' + identifier + '%', PlayerConverter.getID(player));
	}

}
