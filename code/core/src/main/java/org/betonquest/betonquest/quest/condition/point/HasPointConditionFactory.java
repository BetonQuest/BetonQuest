package org.betonquest.betonquest.quest.condition.point;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.data.PlayerDataStorage;

/**
 * Factory to create {@link HasPointCondition}s from {@link Instruction}s.
 */
public class HasPointConditionFactory implements PlayerConditionFactory {

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Creates the has point condition factory.
     *
     * @param dataStorage the BetonQuest instance
     */
    public HasPointConditionFactory(final PlayerDataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<String> category = instruction.packageIdentifier().get();
        return new HasPointCondition(dataStorage, category);
    }
}
