package pl.betoncraft.betonquest.conditions;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Requires the player to have specified tag
 */
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
