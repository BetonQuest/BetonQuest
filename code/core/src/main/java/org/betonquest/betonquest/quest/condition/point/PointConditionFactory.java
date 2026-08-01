package org.betonquest.betonquest.quest.condition.point;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.data.PlayerDataStorage;

/**
 * Factory to create player point conditions from {@link Instruction}s.
 */
public class PointConditionFactory extends AbstractPointConditionFactory implements PlayerConditionFactory {

    /**
     * Creates the point condition factory for player.
     *
     * @param dataStorage the storage providing player data
     */
    public PointConditionFactory(final PlayerDataStorage dataStorage) {
        super(profile -> dataStorage.getOffline(profile).points());
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final PointCondition condition = parseInstruction(instruction);
        return condition::check;
    }
}
