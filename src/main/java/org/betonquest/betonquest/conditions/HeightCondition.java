package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.location.CompoundLocation;

/**
 * Checks Y height player is at (must be below)
 */
@SuppressWarnings("PMD.CommentRequired")
public class HeightCondition extends Condition {

    private final VariableNumber height;

    public HeightCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        final String string = instruction.next();
        final String packName = instruction.getPackage().getPackagePath();
        if (string.matches("\\-?\\d+\\.?\\d*")) {
            try {
                height = new VariableNumber(packName, string);
            } catch (final InstructionParseException e) {
                throw new InstructionParseException("Could not parse height", e);
            }
        } else {
            try {
                height = new VariableNumber(new CompoundLocation(packName, string).getLocation(null).getY());
            } catch (final QuestRuntimeException e) {
                throw new InstructionParseException("Could not parse height", e);
            }
        }
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        return profile.getOnlineProfile().getOnlinePlayer().getLocation().getY() < height.getDouble(profile);
    }

}
