package pl.betoncraft.betonquest.events;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Damages the player
 */
public class DamageEvent extends QuestEvent {

    private final VariableNumber damage;

    public DamageEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        damage = instruction.getVarNum();
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        PlayerConverter.getPlayer(playerID).damage(Math.abs(damage.getDouble(playerID)));
        return null;
    }

}
