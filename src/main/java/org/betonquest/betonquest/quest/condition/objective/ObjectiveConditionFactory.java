package org.betonquest.betonquest.quest.condition.objective;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.instruction.Instruction;

/**
 * A factory for creating ObjectiveConditions.
 */
public class ObjectiveConditionFactory implements PlayerConditionFactory {

    /**
     * The BetonQuest instance.
     */
    private final BetonQuest betonQuest;

    /**
     * Creates a new ObjectiveConditionFactory.
     *
     * @param betonQuest the BetonQuest instance
     */
    public ObjectiveConditionFactory(final BetonQuest betonQuest) {
        this.betonQuest = betonQuest;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return new ObjectiveCondition(instruction.getID(ObjectiveID::new), betonQuest);
    }
}
