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
package pl.betoncraft.betonquest.compatibility.worldedit;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;

import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.LocationData;

/**
 * Pastes a schematic at a given location.
 * 
 * @author Jakub Sapalski
 */
@SuppressWarnings("deprecation")
public class PasteSchematicEvent extends QuestEvent {

	private WorldEditPlugin we;
	private File file;
	private LocationData loc;
	private boolean noAir = true;
	private int maxBlocks = 32 * 32 * 32;

	public PasteSchematicEvent(String packName, String instructions) throws InstructionParseException {
		super(packName, instructions);
		String[] parts = instructions.split(" ");
		if (parts.length < 2) {
			throw new InstructionParseException("Not enough arguments");
		}
		loc = new LocationData(packName, parts[1]);
		we = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
		File folder = new File(we.getDataFolder(), "schematics");
		if (!folder.exists() || !folder.isDirectory()) {
			throw new InstructionParseException("Schematic folder does not exist");
		}
		file = new File(folder, parts[2] + ".schematic");
		if (!file.exists()) {
			throw new InstructionParseException("Schematic " + parts[2] + " does not exist");
		}
		for (String part : parts) {
			if (part.toLowerCase().startsWith("maxblocks:")) {
				try {
					maxBlocks = Integer.parseInt(part.substring(10));
				} catch (NumberFormatException e) {
					throw new InstructionParseException("Could not parse max blocks amount");
				}
				if (maxBlocks <= 0) {
					throw new InstructionParseException("Max blocks amount must be greater than 0");
				}
			} else if (part.equalsIgnoreCase("noair")) {
				noAir = true;
			}
		}
	}

	@Override
	public void run(String playerID) throws QuestRuntimeException {
		try {
			Location location = loc.getLocation(playerID);
			SchematicFormat schematic = SchematicFormat.getFormat(file);
			CuboidClipboard clipboard = schematic.load(file);
			BukkitWorld world = new BukkitWorld(location.getWorld());
			EditSession editSession = we.getWorldEdit().getEditSessionFactory().getEditSession(world, maxBlocks);
			Vector newOrigin = BukkitUtil.toVector(location);
			clipboard.paste(editSession, newOrigin, noAir);
		} catch (DataException | IOException | MaxChangedBlocksException e) {
			Debug.error("Error while pasting a schematic: " + e.getMessage());
		}
	}

}
