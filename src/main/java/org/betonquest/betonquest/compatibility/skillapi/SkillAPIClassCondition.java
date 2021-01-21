package org.betonquest.betonquest.compatibility.skillapi;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;

/**
 * Checks if the player has specific class
 */
@SuppressWarnings("PMD.CommentRequired")
public class SkillAPIClassCondition extends Condition {

    private final String className;
    private final boolean exact;

    public SkillAPIClassCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        className = instruction.next();
        if (!SkillAPI.isClassRegistered(className)) {
            throw new InstructionParseException("Class '" + className + "' is not registered");
        }
        exact = instruction.hasArgument("exact");
    }

    @Override
    protected Boolean execute(final String playerID) {
        final PlayerData data = SkillAPI.getPlayerData(PlayerConverter.getPlayer(playerID));
        if (exact) {
            return data.isExactClass(SkillAPI.getClass(className));
        } else {
            return data.isClass(SkillAPI.getClass(className));
        }
    }

}
