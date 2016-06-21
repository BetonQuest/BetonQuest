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
package pl.betoncraft.betonquest.compatibility.holographicdisplays;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.LocationData;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Hides and shows holograms to players, based on conditions.
 * 
 * @author Jakub Sapalski
 */
public class HologramLoop {
	
	private HashMap<Hologram, String[]> holograms = new HashMap<>();
	private BukkitRunnable runnable;
	
	/**
	 * Starts a loop, which checks hologram conditions and shows them to players.
	 */
	public HologramLoop() {
		// get all holograms and their condition
		for (String packName : Config.getPackageNames()) {
			ConfigPackage pack = Config.getPackage(packName);
			ConfigurationSection section = pack.getMain().getConfig().getConfigurationSection("holograms");
			if (section == null)
				continue;
			for (String key : section.getKeys(false)) {
				if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
					Debug.error("Holograms won't be able to hide from players without ProtocolLib plugin! "
							+ "Install it to use conditioned holograms.");
					return;
				}
				List<String> lines = section.getStringList(key + ".lines");
				String rawConditions = pack.getString("main.holograms." + key + ".conditions");
				String rawLocation = pack.getString("main.holograms." + key + ".location");
				if (rawLocation == null) {
					Debug.error("Location is not specified in " + key + " hologram");
					continue;
				}
				String[] conditions = new String[]{};
				if (rawConditions != null) {
					conditions = rawConditions.split(",");
					for (int i = 0; i < conditions.length; i++) {
						String condition = conditions[i].trim();
						if (!condition.contains(".")) {
							condition = packName + "." + condition;
						}
						conditions[i] = condition;
					}
				}
				Location location = null;
				try {
					location = new LocationData(packName, rawLocation).getLocation(null);
				} catch (QuestRuntimeException | InstructionParseException e) {
					Debug.error("Could not parse location in " + key + " hologram: " + e.getMessage());
					continue;
				}
				Hologram hologram = HologramsAPI.createHologram(BetonQuest.getInstance(), location);
				hologram.getVisibilityManager().setVisibleByDefault(false);
				for (String line : lines) {
					hologram.appendTextLine(line.replace('&', '§'));
				}
				holograms.put(hologram, conditions);
			}
		}
		// loop the holograms to show/hide them
		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					String playerID = PlayerConverter.getID(player);
					holograms:
					for (Entry<Hologram, String[]> entry : holograms.entrySet()) {
						for (String condition : entry.getValue()) {
							if (!BetonQuest.condition(playerID, condition)) {
								entry.getKey().getVisibilityManager().hideTo(player);
								continue holograms;
							}
						}
						entry.getKey().getVisibilityManager().showTo(player);
					}
				}
			}
		};
		runnable.runTaskTimer(BetonQuest.getInstance(), 20, BetonQuest.getInstance().getConfig()
				.getInt("hologram_update_interval", 20 * 10));
	}
	
	/**
	 * Cancels hologram updating loop and removes all BetonQuest-registered holograms.
	 */
	public void cancel() {
		if (runnable == null)
			return;
		runnable.cancel();
		for (Hologram hologram : holograms.keySet()) {
			hologram.delete();
		}
	}

}
