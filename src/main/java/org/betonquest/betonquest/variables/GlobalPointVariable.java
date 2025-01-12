package org.betonquest.betonquest.variables;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.jetbrains.annotations.Nullable;

/**
 * Allows you to display total amount of global points or amount of global points remaining to
 * some other amount.
 */
@SuppressWarnings("PMD.CommentRequired")
public class GlobalPointVariable extends PointVariable {

    public GlobalPointVariable(final Instruction instruction) throws QuestException {
        super(instruction);
        staticness = true;
    }

    @Override
    public String getValue(@Nullable final Profile profile) {
        return getValue(BetonQuest.getInstance().getGlobalData().getPoints());
    }

}
