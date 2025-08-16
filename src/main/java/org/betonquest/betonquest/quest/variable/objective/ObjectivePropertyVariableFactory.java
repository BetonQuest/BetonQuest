package org.betonquest.betonquest.quest.variable.objective;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;

/**
 * Factory to create {@link ObjectivePropertyVariable}s from {@link Instruction}s.
 * <p>
 * Format:
 * {@code %objective.<id>.<property>%}
 */
public class ObjectivePropertyVariableFactory implements PlayerVariableFactory {

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Create a new factory to create Objective Property Variables.
     *
     * @param questTypeApi the Quest Type API
     */
    public ObjectivePropertyVariableFactory(final QuestTypeApi questTypeApi) {
        this.questTypeApi = questTypeApi;
    }

    @Override
    public PlayerVariable parsePlayer(final Instruction instruction) throws QuestException {
        final StringBuilder objectiveString = new StringBuilder();
        String next = "";
        while (instruction.hasNext()) {
            if (!objectiveString.isEmpty()) {
                objectiveString.append('.');
            }
            objectiveString.append(next);
            next = instruction.next();
        }
        final String propertyName = next;

        final ObjectiveID objectiveID;
        try {
            objectiveID = new ObjectiveID(instruction.getPackage(), objectiveString.toString());
        } catch (final QuestException e) {
            throw new QuestException("Error in objective property variable '" + instruction + "' " + e.getMessage(), e);
        }
        return new ObjectivePropertyVariable(questTypeApi, objectiveID, propertyName);
    }
}
