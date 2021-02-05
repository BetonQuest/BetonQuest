package org.betonquest.betonquest.compatibility.protocollib.hider;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;

@SuppressWarnings("PMD.CommentRequired")
public class UpdateVisibilityNowEvent extends QuestEvent {

    public UpdateVisibilityNowEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        NPCHider.getInstance().applyVisibility(PlayerConverter.getPlayer(playerID));
        return null;
    }
}
