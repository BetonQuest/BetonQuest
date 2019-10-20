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
package pl.betoncraft.betonquest.compatibility.worldedit;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.sk89q.worldedit.world.DataException;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LocationData;
import pl.betoncraft.betonquest.utils.LogUtils;

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
    private boolean noAir;

    public PasteSchematicEvent(Instruction instruction) throws InstructionParseException {
        super(instruction);
        loc = instruction.getLocation();
        we = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
        File folder = new File(we.getDataFolder(), "schematics");
        if (!folder.exists() || !folder.isDirectory()) {
            throw new InstructionParseException("Schematic folder does not exist");
        }
        String schemName = instruction.next();
        file = new File(folder, schemName + ".schematic");
        if (!file.exists()) {
            throw new InstructionParseException("Schematic " + schemName + " does not exist");
        }
        noAir = instruction.hasArgument("noair");
    }

    @Override
    public void run(String playerID) throws QuestRuntimeException {
        try {
            Location location = loc.getLocation(playerID);
            SchematicFormat schematic = SchematicFormat.getFormat(file);
            CuboidClipboard clipboard = schematic.load(file);
            BukkitWorld world = new BukkitWorld(location.getWorld());
            EditSession editSession = we.getWorldEdit().getEditSessionFactory().getEditSession(world, -1);
            Vector newOrigin = BukkitUtil.toVector(location);
            clipboard.paste(editSession, newOrigin, noAir);
        } catch (DataException | IOException | MaxChangedBlocksException e) {
        	LogUtils.getLogger().log(Level.WARNING, "Error while pasting a schematic: " + e.getMessage());
            LogUtils.logThrowable(e);
        }
    }

}
