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

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.LocationData;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Pastes a schematic at a given location.
 *
 * @author Jakub Sapalski
 */
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
            file = new File(folder, schemName);

            if (!file.exists()) {
                throw new InstructionParseException("Schematic " + schemName + " does not exist (" + folder.toPath().resolve(schemName + ".schematic") + ")");
            }
        }
        noAir = instruction.hasArgument("noair");
    }

    @Override
    public void run(String playerID) throws QuestRuntimeException {
        try {
            Location location = loc.getLocation(playerID);
            ClipboardFormat format = ClipboardFormats.findByFile(file);
            if (format == null) {
                throw new IOException("Unknown Schematic Format");
            }

            Clipboard clipboard;
            try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
                clipboard = reader.read();
            }


            try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(location.getWorld()), -1)) {
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(BukkitAdapter.asBlockVector(location))
                        .ignoreAirBlocks(noAir)
                        .build();

                Operations.complete(operation);
            }
        } catch (IOException | WorldEditException e) {
            Debug.error("Error while pasting a schematic: " + e.getMessage());
        }
    }

}
