package org.betonquest.betonquest.quest.condition.redstone;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.NullableConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.bukkit.Location;

/**
 * Factory for {@link RedstoneCondition}s.
 */
public class RedstoneConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * Create the redstone factory.
     */
    public RedstoneConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return new NullableConditionAdapter(parse(instruction));
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        return new NullableConditionAdapter(parse(instruction));
    }

    private RedstoneCondition parse(final Instruction instruction) throws QuestException {
        final Argument<Location> redstoneLocation = instruction.location().get();
        final Argument<Number> redstonePower = instruction.number().inRange(1, 16).get();
        return new RedstoneCondition(redstoneLocation, redstonePower);
    }
}
