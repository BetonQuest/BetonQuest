package org.betonquest.betonquest.variables;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.ObjectiveID;
import org.jetbrains.annotations.Nullable;

/**
 * Resolves to a specified property of an objective.
 */
@SuppressWarnings("PMD.CommentRequired")
public class ObjectivePropertyVariable extends Variable {
    private final String propertyName;

    private final ObjectiveID objective;

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public ObjectivePropertyVariable(final Instruction instruction) throws QuestException {
        super(instruction);

        final StringBuilder objectiveID = new StringBuilder();
        String next = "";
        while (instruction.hasNext()) {
            if (!objectiveID.isEmpty()) {
                objectiveID.append('.');
            }
            objectiveID.append(next);
            next = instruction.next();
        }
        this.propertyName = next;

        final ObjectiveID tempObjective;
        try {
            tempObjective = new ObjectiveID(instruction.getPackage(), objectiveID.toString());
        } catch (final ObjectNotFoundException e) {
            throw new QuestException("Error in objective property variable '" + instruction + "' " + e.getMessage(), e);
        }
        objective = tempObjective;
    }

    @Override
    public String getValue(@Nullable final Profile profile) {
        if (profile == null) {
            return "";
        }
        final Objective objective = BetonQuest.getInstance().getObjective(this.objective);
        if (objective == null) {
            return "";
        }
        return objective.containsPlayer(profile) ? objective.getProperty(propertyName, profile) : "";
    }
}
