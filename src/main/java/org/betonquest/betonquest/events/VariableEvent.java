package org.betonquest.betonquest.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableString;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.objectives.VariableObjective;

@SuppressWarnings("PMD.CommentRequired")
public class VariableEvent extends QuestEvent {
    private final ObjectiveID objectiveID;

    private final VariableString key;

    private final VariableString value;

    public VariableEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        objectiveID = instruction.getObjective();
        key = new VariableString(instruction.getPackage(), instruction.next(), true);
        value = new VariableString(instruction.getPackage(), instruction.next(), true);
    }

    @Override
    protected Void execute(final Profile profile) throws QuestRuntimeException {
        final Objective obj = BetonQuest.getInstance().getObjective(objectiveID);
        if (!(obj instanceof final VariableObjective objective)) {
            throw new QuestRuntimeException(objectiveID.getFullID() + " is not a variable objective");
        }
        final String keyReplaced = key.getString(profile);
        final String valueReplaced = value.getString(profile);
        if (!objective.store(profile, keyReplaced, valueReplaced)) {
            throw new QuestRuntimeException("Player " + profile.getProfileName() + " does not have '"
                    + objectiveID.getFullID() + "' objective, cannot store a variable.");
        }
        return null;
    }

}
