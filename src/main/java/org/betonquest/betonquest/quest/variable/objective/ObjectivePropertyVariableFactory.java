package org.betonquest.betonquest.quest.variable.objective;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.exception.ObjectNotFoundException;
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
     * The BetonQuest instance.
     */
    private final BetonQuest betonQuest;

    /**
     * Create a new factory to create Objective Property Variables.
     *
     * @param betonQuest The BetonQuest instance.
     */
    public ObjectivePropertyVariableFactory(final BetonQuest betonQuest) {
        this.betonQuest = betonQuest;
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
        } catch (final ObjectNotFoundException e) {
            throw new QuestException("Error in objective property variable '" + instruction + "' " + e.getMessage(), e);
        }
        return new ObjectivePropertyVariable(betonQuest, objectiveID, propertyName);
    }
}
