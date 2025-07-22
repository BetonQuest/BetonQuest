package org.betonquest.betonquest.quest.variable.objective;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.instruction.Instruction;

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
    private final QuestTypeAPI questTypeAPI;

    /**
     * Create a new factory to create Objective Property Variables.
     *
     * @param questTypeAPI the Quest Type API
     */
    public ObjectivePropertyVariableFactory(final QuestTypeAPI questTypeAPI) {
        this.questTypeAPI = questTypeAPI;
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
        return new ObjectivePropertyVariable(questTypeAPI, objectiveID, propertyName);
    }
}
