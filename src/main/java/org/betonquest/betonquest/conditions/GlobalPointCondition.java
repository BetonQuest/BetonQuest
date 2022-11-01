package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Requires a specified amount of global points (or more) in specified
 * category
 */
@SuppressWarnings("PMD.CommentRequired")
public class GlobalPointCondition extends PointCondition {

    public GlobalPointCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction);
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        return check(profile, BetonQuest.getInstance().getGlobalData().getPoints());
    }

}
