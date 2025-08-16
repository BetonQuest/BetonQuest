package org.betonquest.betonquest.compatibility.worldedit;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadPlayerlessEvent;
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
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the door event factory.
     *
     * @param folder the schematics folder
     * @param data   the data for primary server thread access
     */
    public PasteSchematicEventFactory(final File folder, final PrimaryServerThreadData data) {
        this.folder = folder;
        this.data = data;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadEvent(new NullableEventAdapter(parseInstruction(instruction)), data);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadPlayerlessEvent(new NullableEventAdapter(parseInstruction(instruction)), data);
    }

    private NullableEvent parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<Location> loc = instruction.get(Argument.LOCATION);
        final Variable<Number> rotation = instruction.getValue("rotation", Argument.NUMBER, 0);

        if (!folder.exists() || !folder.isDirectory()) {
            throw new QuestException("Schematic folder does not exist");
        }
        final Variable<File> file = instruction.get((value) -> {
            final File schematic = new File(folder, value);

            if (schematic.exists()) {
                return schematic;
            }
            final File alternativeSchematic = new File(folder, value + ".schematic");
            if (alternativeSchematic.exists()) {
                return alternativeSchematic;
            }
            throw new QuestException("Schematic " + value + " does not exist (" + folder.toPath().resolve(value + ".schematic") + ")");
        });

        final boolean noAir = instruction.hasArgument("noair");
        return new PasteSchematicEvent(loc, rotation, noAir, file);
    }
}
