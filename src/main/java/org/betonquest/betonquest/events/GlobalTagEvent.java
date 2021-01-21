package org.betonquest.betonquest.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Adds or removes global tags
 */
@SuppressWarnings("PMD.CommentRequired")
public class GlobalTagEvent extends TagEvent {

    public GlobalTagEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        staticness = true;
        persistent = true;
    }

    @Override
    protected Void execute(final String playerID) {
        final GlobalData globalData = BetonQuest.getInstance().getGlobalData();
        if (add) {
            for (final String tag : tags) {
                globalData.addTag(tag);
            }
        } else {
            for (final String tag : tags) {
                globalData.removeTag(tag);
            }
        }
        return null;
    }
}
