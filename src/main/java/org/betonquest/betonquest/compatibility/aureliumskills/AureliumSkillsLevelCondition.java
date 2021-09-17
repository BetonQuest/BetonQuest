package org.betonquest.betonquest.compatibility.aureliumskills;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.api.AureliumAPI;
import com.archyx.aureliumskills.skills.Skill;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;

/**
 * Checks whether a player has the required skill level.
 */
@SuppressWarnings({"PMD.CommentRequired", "PMD.PreserveStackTrace"})
public class AureliumSkillsLevelCondition extends Condition {

    private final VariableNumber targetLevelVar;
    private final Skill skill;
    private boolean mustBeEqual;

    public AureliumSkillsLevelCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        final String skillName = instruction.next();
        targetLevelVar = instruction.getVarNum();
        if (instruction.hasArgument("equal")) {
            mustBeEqual = true;
        }

        final AureliumSkills aureliumSkills = AureliumAPI.getPlugin();
        skill = aureliumSkills.getSkillRegistry().getSkill(skillName);
        if (skill == null) {
            throw new InstructionParseException("Invalid skill name");
        }
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final Player player = PlayerConverter.getPlayer(playerID);

        final int actualLevel = AureliumAPI.getSkillLevel(player, skill);
        final int targetLevel = targetLevelVar.getInt(playerID);

        return mustBeEqual ? actualLevel == targetLevel : actualLevel >= targetLevel;
    }
}
