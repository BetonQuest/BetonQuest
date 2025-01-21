package org.betonquest.betonquest.quest.condition.point;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.util.Utils;

/**
 * Factory to create point conditions from {@link Instruction}s.
 */
public class PointConditionFactory implements PlayerConditionFactory {

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Creates the point condition factory.
     *
     * @param dataStorage the BetonQuest instance
     */
    public PointConditionFactory(final PlayerDataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final String category = Utils.addPackage(instruction.getPackage(), instruction.next());
        final VariableNumber count = instruction.get(VariableNumber::new);
        final boolean equal = instruction.hasArgument("equal");
        return new PointCondition(dataStorage, category, count, equal);
    }
}
