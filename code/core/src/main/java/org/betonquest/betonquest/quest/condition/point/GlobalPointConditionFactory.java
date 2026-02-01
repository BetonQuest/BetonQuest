package org.betonquest.betonquest.quest.condition.point;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.NullableConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.database.GlobalData;

/**
 * Factory to create global point conditions from {@link Instruction}s.
 */
public class GlobalPointConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * The global data.
     */
    private final GlobalData globalData;

    /**
     * Creates the global point factory.
     *
     * @param globalData the global data
     */
    public GlobalPointConditionFactory(final GlobalData globalData) {
        this.globalData = globalData;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return new NullableConditionAdapter(parse(instruction));
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        return new NullableConditionAdapter(parse(instruction));
    }

    private GlobalPointCondition parse(final Instruction instruction) throws QuestException {
        final Argument<String> category = instruction.packageIdentifier().get();
        final Argument<Number> count = instruction.number().get();
        final FlagArgument<Boolean> equal = instruction.bool().getFlag("equal", true);
        return new GlobalPointCondition(globalData, category, count, equal);
    }
}
