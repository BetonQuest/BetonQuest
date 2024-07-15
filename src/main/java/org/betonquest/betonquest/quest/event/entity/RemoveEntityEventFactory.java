package org.betonquest.betonquest.quest.event.entity;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.ComposedEvent;
import org.betonquest.betonquest.api.quest.event.ComposedEventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadComposedEvent;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.entity.EntityType;

import java.util.Locale;

/**
 * Factory for {@link RemoveEntityEvent} to create from {@link Instruction}.
 * <p>
 * Created on 29.06.2018.
 */
public class RemoveEntityEventFactory implements ComposedEventFactory {

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Creates a new KillMobEventFactory.
     *
     * @param data the data for primary server thread access
     */
    public RemoveEntityEventFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public ComposedEvent parseComposedEvent(final Instruction instruction) throws InstructionParseException {
        final String[] entities = instruction.getArray();
        final EntityType[] types = new EntityType[entities.length];
        for (int i = 0; i < types.length; i++) {
            try {
                types[i] = EntityType.valueOf(entities[i].toUpperCase(Locale.ROOT));
            } catch (final IllegalArgumentException e) {
                throw new InstructionParseException("Entity type '" + entities[i] + "' does not exist", e);
            }
        }
        final VariableLocation loc = instruction.getLocation();
        final VariableNumber range = instruction.getVarNum();
        final String name = instruction.getOptional("name");
        final boolean kill = instruction.hasArgument("kill");
        final String markedString = instruction.getOptional("marked");
        final VariableString marked = markedString == null ? null : new VariableString(instruction.getPackage(), Utils.addPackage(instruction.getPackage(), markedString));
        return new PrimaryServerThreadComposedEvent(new RemoveEntityEvent(types, loc, range, name, marked, kill), data);
    }
}
