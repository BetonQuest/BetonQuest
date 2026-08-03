package org.betonquest.betonquest.quest.condition.point;

import org.betonquest.betonquest.api.QuestException;
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
public class GlobalPointConditionFactory extends DefaultPointConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * Creates the global point factory.
     *
     * @param globalData the global data
     */
    public GlobalPointConditionFactory(final GlobalData globalData) {
        super(profile -> globalData.points());
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return new NullableConditionAdapter(parseInstruction(instruction));
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        return new NullableConditionAdapter(parseInstruction(instruction));
    }
}
