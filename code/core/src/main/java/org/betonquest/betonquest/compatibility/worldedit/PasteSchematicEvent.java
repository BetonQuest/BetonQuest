package org.betonquest.betonquest.compatibility.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Pastes a schematic at a given location.
 */
public class PasteSchematicEvent implements NullableEvent {

    /**
     * Root location of placement.
     */
    private final Variable<Location> loc;

    /**
     * Rotation of placement.
     */
    private final Variable<Number> rotation;

    /**
     * 'No Air' boolean for pasting.
     */
    private final boolean noAir;

    /**
     * Schematic file.
     */
    private final Variable<File> file;

    /**
     * Create a new paste schematic event.
     *
     * @param loc      the root location to place at
     * @param rotation the rotation
     * @param noAir    the 'no air' paste argument
     * @param file     the schematic file
     */
    public PasteSchematicEvent(final Variable<Location> loc, final Variable<Number> rotation, final boolean noAir, final Variable<File> file) {
        this.loc = loc;
        this.rotation = rotation;
        this.noAir = noAir;
        this.file = file;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        try {
            final Location location = loc.getValue(profile);
            final double rot = rotation.getValue(profile).doubleValue();

            final ClipboardHolder clipboard = new ClipboardHolder(getClipboard(profile));
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
            throw new QuestException("Error while pasting a schematic: " + e.getMessage(), e);
        }
    }

    private Clipboard getClipboard(@Nullable final Profile profile) throws IOException, QuestException {
        final File file = this.file.getValue(profile);
        final ClipboardFormat format = ClipboardFormats.findByFile(file);
        if (format == null) {
            throw new IOException("Unknown Schematic Format");
        }

        try (ClipboardReader reader = format.getReader(Files.newInputStream(file.toPath()))) {
            return reader.read();
        }
    }
}
