package org.betonquest.betonquest.quest.condition.tag;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.util.Utils;

/**
 * Factory to create global tag conditions from {@link Instruction}s.
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
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        final String tag = Utils.addPackage(instruction.getPackage(), instruction.next());
        return new GlobalTagCondition(globalData, tag);
    }
}
