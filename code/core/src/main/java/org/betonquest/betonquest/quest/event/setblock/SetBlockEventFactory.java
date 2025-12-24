package org.betonquest.betonquest.quest.event.setblock;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.parser.DefaultArgumentParsers;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.util.BlockSelector;
import org.bukkit.Location;

/**
 * Factory to create setblock events from {@link Instruction}s.
 */
public class SetBlockEventFactory implements PlayerEventFactory, PlayerlessEventFactory {

    /**
     * Create the setblock event factory.
     */
    public SetBlockEventFactory() {
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return createSetBlockEvent(instruction);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        return createSetBlockEvent(instruction);
    }

    private NullableEventAdapter createSetBlockEvent(final Instruction instruction) throws QuestException {
        final Variable<BlockSelector> blockSelector = instruction.parse(DefaultArgumentParsers.BLOCK_SELECTOR).get();
        final Variable<Location> variableLocation = instruction.location().get();
        final boolean applyPhysics = !instruction.hasArgument("ignorePhysics");
        return new NullableEventAdapter(new SetBlockEvent(blockSelector, variableLocation, applyPhysics));
    }
}
