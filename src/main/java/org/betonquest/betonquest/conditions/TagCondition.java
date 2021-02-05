package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.Utils;

/**
 * Requires the player to have specified tag
 */
@SuppressWarnings("PMD.CommentRequired")
public class TagCondition extends Condition {

    protected final String tag;

    public TagCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        tag = Utils.addPackage(instruction.getPackage(), instruction.next());
    }

    @Override
    protected Boolean execute(final String playerID) {
        return BetonQuest.getInstance().getPlayerData(playerID).hasTag(tag);
    }

}
