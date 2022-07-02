package org.betonquest.betonquest.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Adds or removes global tags
 */
@SuppressWarnings("PMD.CommentRequired")
public class GlobalTagEvent extends TagEvent {

    public GlobalTagEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction);
    }

    @Override
    protected Void execute(final Profile profile) {
        final GlobalData globalData = BetonQuest.getInstance().getGlobalData();
        tagChanger.changeTags(globalData);
        return null;
    }
}
