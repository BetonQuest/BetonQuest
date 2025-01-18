package org.betonquest.betonquest.quest.condition.point;

import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.utils.Utils;

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
        final String category = Utils.addPackage(instruction.getPackage(), instruction.next());
        final VariableNumber count = instruction.get(VariableNumber::new);
        final boolean equal = instruction.hasArgument("equal");
        return new GlobalPointCondition(globalData, category, count, equal);
    }
}
