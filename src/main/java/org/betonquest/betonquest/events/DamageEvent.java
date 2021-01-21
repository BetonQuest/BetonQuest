package org.betonquest.betonquest.events;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;

/**
 * Damages the player
 */
@SuppressWarnings("PMD.CommentRequired")
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
