package pl.betoncraft.betonquest.compatibility.protocollib;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

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
