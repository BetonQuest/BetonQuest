package pl.betoncraft.betonquest.compatibility.protocollib.hider;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

@SuppressWarnings("PMD.CommentRequired")
public class UpdateVisibilityNowEvent extends QuestEvent {
    private static PlayerHider hider;

    public UpdateVisibilityNowEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        NPCHider.getInstance().applyVisibility(PlayerConverter.getPlayer(playerID));
        if(hider != null) {
            hider.updateVisibility();
        }
        return null;
    }

    public static void setHider(final PlayerHider playerHider) {
        hider = playerHider;
    }
}
