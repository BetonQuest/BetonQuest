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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Checks whether a player has the required skill level.
 */
@SuppressWarnings({"PMD.CommentRequired", "PMD.PreserveStackTrace"})
public class AureliumSkillsLevelCondition extends Condition {

    private final AureliumSkills aureliumSkills;

    private final String skillName;
    private final VariableNumber targetLevelVar;
    private boolean mustBeEqual;

    public AureliumSkillsLevelCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        try {
            aureliumSkills = (AureliumSkills) Bukkit.getPluginManager().getPlugin("AureliumSkills");
        } catch (final ClassCastException exception) {
            throw new InstructionParseException("AureliumSkills wasn't able to be hooked due to: " + exception);
        }

        skillName = instruction.next();
        targetLevelVar = instruction.getVarNum();
        if (instruction.hasArgument("equal")) {
            mustBeEqual = true;
        }
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final Skill skill = aureliumSkills.getSkillRegistry().getSkill(skillName);
        if (skill == null) {
            return false;
        }
        final Player player = PlayerConverter.getPlayer(playerID);

        final int actualLevel = AureliumAPI.getSkillLevel(player, skill);
        final int targetLevel = targetLevelVar.getInt(playerID);

        return mustBeEqual ? actualLevel == targetLevel : actualLevel >= targetLevel;
    }
}
