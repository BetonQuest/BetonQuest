package org.betonquest.betonquest.compatibility.mmogroup.mmocore;

import net.Indyuce.mmocore.api.player.PlayerData;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;

@SuppressWarnings("PMD.CommentRequired")
public class MMOCoreAttributePointsEvent extends QuestEvent {

    private final VariableNumber amountVar;

    public MMOCoreAttributePointsEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        amountVar = instruction.getVarNum();
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final PlayerData data = PlayerData.get(PlayerConverter.getPlayer(playerID));
        final int amount = amountVar.getInt(playerID);
        data.giveAttributePoints(amount);
        return null;
    }
}

