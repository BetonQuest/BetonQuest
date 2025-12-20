package org.betonquest.betonquest.quest.condition.tag;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.argument.PackageArgument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.database.GlobalData;

/**
 * Factory to create global tag conditions from {@link DefaultInstruction}s.
 */
public class GlobalTagConditionFactory implements PlayerlessConditionFactory {

    /**
     * The global data.
     */
    private final GlobalData globalData;

    /**
     * Creates the global tag condition factory.
     *
     * @param globalData the global data
     */
    public GlobalTagConditionFactory(final GlobalData globalData) {
        this.globalData = globalData;
    }

    @Override
    public PlayerlessCondition parsePlayerless(final DefaultInstruction instruction) throws QuestException {
        final Variable<String> tag = instruction.get(PackageArgument.IDENTIFIER);
        return new GlobalTagCondition(globalData, tag);
    }
}
