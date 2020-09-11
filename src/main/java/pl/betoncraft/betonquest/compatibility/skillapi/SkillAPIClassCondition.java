package pl.betoncraft.betonquest.compatibility.skillapi;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Checks if the player has specific class
 */
public class SkillAPIClassCondition extends Condition {

    private String className;
    private boolean exact;

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
