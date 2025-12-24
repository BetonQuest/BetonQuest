package org.betonquest.betonquest.compatibility.worldedit;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.bukkit.Location;

import java.io.File;

/**
 * Factory to create {@link PasteSchematicEvent}s from {@link Instruction}s.
 */
public class PasteSchematicEventFactory implements PlayerEventFactory, PlayerlessEventFactory {

    /**
     * Schematics folder.
     */
    private final File folder;

    /**
     * Create the door event factory.
     *
     * @param folder the schematics folder
     */
    public PasteSchematicEventFactory(final File folder) {
        this.folder = folder;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return new NullableEventAdapter(parseInstruction(instruction));
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        return new NullableEventAdapter(parseInstruction(instruction));
    }

    private NullableEvent parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<Location> loc = instruction.location().get();
        final Variable<Number> rotation = instruction.number().get("rotation", 0);

        if (!folder.exists() || !folder.isDirectory()) {
            throw new QuestException("Schematic folder does not exist");
        }
        final Variable<File> file = instruction.parse((value) -> {
            final File schematic = new File(folder, value);

            if (schematic.exists()) {
                return schematic;
            }
            final File alternativeSchematic = new File(folder, value + ".schematic");
            if (alternativeSchematic.exists()) {
                return alternativeSchematic;
            }
            throw new QuestException("Schematic " + value + " does not exist (" + folder.toPath().resolve(value + ".schematic") + ")");
        }).get();

        final boolean noAir = instruction.hasArgument("noair");
        return new PasteSchematicEvent(loc, rotation, noAir, file);
    }
}
