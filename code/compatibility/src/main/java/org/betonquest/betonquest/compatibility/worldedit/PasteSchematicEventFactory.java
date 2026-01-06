package org.betonquest.betonquest.compatibility.worldedit;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.quest.action.nullable.NullableAction;
import org.betonquest.betonquest.api.quest.action.nullable.NullableActionAdapter;
import org.bukkit.Location;

import java.io.File;

/**
 * Factory to create {@link PasteSchematicEvent}s from {@link Instruction}s.
 */
public class PasteSchematicEventFactory implements PlayerActionFactory, PlayerlessActionFactory {

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
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return new NullableActionAdapter(parseInstruction(instruction));
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return new NullableActionAdapter(parseInstruction(instruction));
    }

    private NullableAction parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<Location> loc = instruction.location().get();
        final Argument<Number> rotation = instruction.number().get("rotation", 0);

        if (!folder.exists() || !folder.isDirectory()) {
            throw new QuestException("Schematic folder does not exist");
        }
        final Argument<File> file = instruction.parse((value) -> {
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

        final FlagArgument<Boolean> noAir = instruction.bool().getFlag("noair", true);
        return new PasteSchematicEvent(loc, rotation, noAir, file);
    }
}
