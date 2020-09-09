package pl.betoncraft.betonquest.compatibility.mmogroup.mmocore;

import net.Indyuce.mmocore.api.player.PlayerData;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

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

