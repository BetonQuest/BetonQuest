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
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LocationData;
import pl.betoncraft.betonquest.utils.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Pastes a schematic at a given location.
 */
public class PasteSchematicEvent extends QuestEvent {

    private WorldEditPlugin worldEdit;
    private File file;
    private LocationData loc;
    private boolean noAir;

    public PasteSchematicEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        loc = instruction.getLocation();
        worldEdit = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
        final File folder = new File(worldEdit.getDataFolder(), "schematics");
        if (!folder.exists() || !folder.isDirectory()) {
            throw new InstructionParseException("Schematic folder does not exist");
        }
        final String schemName = instruction.next();
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
    protected Void execute(final String playerID) throws QuestRuntimeException {
        try {
            final ClipboardFormat format = ClipboardFormats.findByFile(file);
            if (format == null) {
                throw new IOException("Unknown Schematic Format");
            }

            final Clipboard clipboard;
            try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
                clipboard = reader.read();
            }

            final Location location = loc.getLocation(playerID);
            try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(location.getWorld()), -1)) {
                final Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(BukkitAdapter.asBlockVector(location))
                        .ignoreAirBlocks(noAir)
                        .build();

                Operations.complete(operation);
            }
        } catch (IOException | WorldEditException e) {
            LogUtils.getLogger().log(Level.WARNING, "Error while pasting a schematic: " + e.getMessage());
            LogUtils.logThrowable(e);
        }
        return null;
    }

}
