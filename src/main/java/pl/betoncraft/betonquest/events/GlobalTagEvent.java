package pl.betoncraft.betonquest.events;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.database.GlobalData;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;

/**
 * Adds or removes global tags
 */
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
