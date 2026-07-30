package org.betonquest.betonquest.quest.condition.point;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.NullableConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.database.GlobalData;

/**
 * Factory to create {@link HasGlobalPointCondition}s from {@link Instruction}s.
 */
public class HasGlobalPointConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * The global data.
     */
    private final GlobalData globalData;

    /**
     * Creates the has global point factory.
     *
     * @param globalData the global data
     */
    public HasGlobalPointConditionFactory(final GlobalData globalData) {
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

    private HasGlobalPointCondition parse(final Instruction instruction) throws QuestException {
        final Argument<String> category = instruction.packageIdentifier().get();
        return new HasGlobalPointCondition(globalData, category);
    }
}
