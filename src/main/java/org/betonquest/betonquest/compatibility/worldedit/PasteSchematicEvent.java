package org.betonquest.betonquest.compatibility.worldedit;

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
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Pastes a schematic at a given location.
 */
@SuppressWarnings("PMD.CommentRequired")
public class PasteSchematicEvent extends QuestEvent {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    private final VariableLocation loc;

    private final VariableNumber rotation;

    private final boolean noAir;

    private final File file;

    public PasteSchematicEvent(final Instruction instruction) throws QuestException {
        super(instruction, true);
        this.log = BetonQuest.getInstance().getLoggerFactory().create(getClass());
        staticness = true;
        persistent = true;
        loc = instruction.getLocation();
        rotation = instruction.getVarNum(instruction.getOptional("rotation", "0"));

        final WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
        final File folder = new File(worldEdit.getDataFolder(), "schematics");
        if (!folder.exists() || !folder.isDirectory()) {
            throw new QuestException("Schematic folder does not exist");
        }
        final String schemName = instruction.next();
        final File schemFile = new File(folder, schemName);
        if (schemFile.exists()) {
            file = schemFile;
        } else {
            file = new File(folder, schemName + ".schematic");
            if (!file.exists()) {
                throw new QuestException("Schematic " + schemName + " does not exist (" + folder.toPath().resolve(schemName + ".schematic") + ")");
            }
        }
        noAir = instruction.hasArgument("noair");
    }

    @Override
    protected Void execute(final Profile profile) throws QuestException {
        try {
            final Location location = loc.getValue(profile);
            final double rot = rotation.getValue(profile).doubleValue();

            final ClipboardHolder clipboard = new ClipboardHolder(getClipboard());
            final AffineTransform transform = new AffineTransform();
            clipboard.setTransform(clipboard.getTransform().combine(transform.rotateY(rot)));

            try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().maxBlocks(-1).world(BukkitAdapter.adapt(location.getWorld())).build()) {
                final Operation operation = clipboard
                        .createPaste(editSession)
                        .to(BukkitAdapter.asBlockVector(location))
                        .ignoreAirBlocks(noAir)
                        .build();
                Operations.complete(operation);
            }
        } catch (final IOException | WorldEditException e) {
            log.warn(instruction.getPackage(), "Error while pasting a schematic: " + e.getMessage(), e);
        }
        return null;
    }

    private Clipboard getClipboard() throws IOException {
        final ClipboardFormat format = ClipboardFormats.findByFile(file);
        if (format == null) {
            throw new IOException("Unknown Schematic Format");
        }

        try (ClipboardReader reader = format.getReader(Files.newInputStream(file.toPath()))) {
            return reader.read();
        }
    }
}
