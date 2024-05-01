package org.betonquest.betonquest.compatibility.aureliumskills;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.api.AureliumAPI;
import com.archyx.aureliumskills.skills.Skill;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.entity.Player;

/**
 * Checks whether a player has the required skill level.
 */
@SuppressWarnings("PMD.CommentRequired")
public class AureliumSkillsLevelCondition extends Condition {

    private final VariableNumber targetLevelVar;

    private final Skill skill;

    private final boolean mustBeEqual;

    public AureliumSkillsLevelCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        final String skillName = instruction.next();
        targetLevelVar = instruction.getVarNum();
        mustBeEqual = instruction.hasArgument("equal");

        final AureliumSkills aureliumSkills = AureliumAPI.getPlugin();
        skill = Utils.getNN(aureliumSkills.getSkillRegistry().getSkill(skillName), "Invalid skill name");
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        final Player player = profile.getOnlineProfile().get().getPlayer();

        final int actualLevel = AureliumAPI.getSkillLevel(player, skill);
        final int targetLevel = targetLevelVar.getInt(profile);

        return mustBeEqual ? actualLevel == targetLevel : actualLevel >= targetLevel;
    }
}
