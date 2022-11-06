package org.betonquest.betonquest.events;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

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
    protected Void execute(final Profile profile) throws QuestRuntimeException {
        profile.getOnlineProfile().get().getPlayer().damage(Math.abs(damage.getDouble(profile)));
        return null;
    }

}
