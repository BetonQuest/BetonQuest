package org.betonquest.betonquest.compatibility.worldedit;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadStaticEvent;

import java.io.File;

/**
 * Factory to create {@link PasteSchematicEvent}s from {@link Instruction}s.
 */
public class PasteSchematicEventFactory implements EventFactory, StaticEventFactory {
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
    public Event parseEvent(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadEvent(new NullableEventAdapter(parseInstruction(instruction)), data);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadStaticEvent(new NullableEventAdapter(parseInstruction(instruction)), data);
    }

    private NullableEvent parseInstruction(final Instruction instruction) throws QuestException {
        final VariableLocation loc = instruction.get(VariableLocation::new);
        final VariableNumber rotation = instruction.get(instruction.getOptional("rotation", "0"), VariableNumber::new);

        if (!folder.exists() || !folder.isDirectory()) {
            throw new QuestException("Schematic folder does not exist");
        }
        final String schematicName = instruction.next();
        final File schemFile = new File(folder, schematicName);
        final File file;
        if (schemFile.exists()) {
            file = schemFile;
        } else {
            file = new File(folder, schematicName + ".schematic");
            if (!file.exists()) {
                throw new QuestException("Schematic " + schematicName + " does not exist (" + folder.toPath().resolve(schematicName + ".schematic") + ")");
            }
        }
        final boolean noAir = instruction.hasArgument("noair");
        return new PasteSchematicEvent(loc, rotation, noAir, file);
    }
}
