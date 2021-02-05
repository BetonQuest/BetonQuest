package org.betonquest.betonquest.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.Utils;

/**
 * Cancels the quest for the player.
 */
@SuppressWarnings("PMD.CommentRequired")
public class CancelEvent extends QuestEvent {

    private final String canceler;

    public CancelEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        canceler = Utils.addPackage(instruction.getPackage(), instruction.next());
    }

    @Override
    protected Void execute(final String playerID) {
        BetonQuest.getInstance().getPlayerData(playerID).cancelQuest(canceler);
        return null;
    }

}
