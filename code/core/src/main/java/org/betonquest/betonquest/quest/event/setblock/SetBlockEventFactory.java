package org.betonquest.betonquest.quest.event.setblock;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadPlayerlessEvent;
import org.betonquest.betonquest.util.BlockSelector;
import org.bukkit.Location;

/**
 * Factory to create setblock events from {@link Instruction}s.
 */
public class SetBlockEventFactory implements PlayerEventFactory, PlayerlessEventFactory {
    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the setblock event factory.
     *
     * @param data the data for primary server thread access
     */
    public SetBlockEventFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadEvent(createSetBlockEvent(instruction), data);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadPlayerlessEvent(createSetBlockEvent(instruction), data);
    }

    private NullableEventAdapter createSetBlockEvent(final Instruction instruction) throws QuestException {
        final Variable<BlockSelector> blockSelector = instruction.get(Argument.BLOCK_SELECTOR);
        final Variable<Location> variableLocation = instruction.get(Argument.LOCATION);
        final boolean applyPhysics = !instruction.hasArgument("ignorePhysics");
        return new NullableEventAdapter(new SetBlockEvent(blockSelector, variableLocation, applyPhysics));
    }
}
