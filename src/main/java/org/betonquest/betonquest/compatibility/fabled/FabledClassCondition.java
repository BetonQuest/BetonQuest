package org.betonquest.betonquest.compatibility.fabled;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerData;

/**
 * Checks if the player has specific class
 */
@SuppressWarnings("PMD.CommentRequired")
public class FabledClassCondition extends Condition {
    private final String className;

    private final boolean exact;

    public FabledClassCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        className = instruction.next();
        if (!Fabled.isClassRegistered(className)) {
            throw new InstructionParseException("Class '" + className + "' is not registered");
        }
        exact = instruction.hasArgument("exact");
    }

    @Override
    protected Boolean execute(final Profile profile) {
        final PlayerData data = Fabled.getData(profile.getPlayer());
        if (exact) {
            return data.isExactClass(Fabled.getClass(className));
        } else {
            return data.isClass(Fabled.getClass(className));
        }
    }
}
