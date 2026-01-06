package org.betonquest.betonquest.quest.action.setblock;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.BlockSelector;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.quest.action.nullable.NullableActionAdapter;
import org.bukkit.Location;

/**
 * Factory to create setblock events from {@link Instruction}s.
 */
public class SetBlockActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * Create the setblock event factory.
     */
    public SetBlockActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return createSetBlockEvent(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return createSetBlockEvent(instruction);
    }

    private NullableActionAdapter createSetBlockEvent(final Instruction instruction) throws QuestException {
        final Argument<BlockSelector> blockSelector = instruction.blockSelector().get();
        final Argument<Location> location = instruction.location().get();
        final FlagArgument<Boolean> ignorePhysics = instruction.bool().getFlag("ignorePhysics", true);
        return new NullableActionAdapter(new SetBlockAction(blockSelector, location, ignorePhysics));
    }
}
