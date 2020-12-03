package pl.betoncraft.betonquest.events;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.Utils;

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
