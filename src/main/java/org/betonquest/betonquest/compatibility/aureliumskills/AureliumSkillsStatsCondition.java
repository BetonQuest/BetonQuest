package org.betonquest.betonquest.compatibility.aureliumskills;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.api.AureliumAPI;
import com.archyx.aureliumskills.stats.Stat;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;

@SuppressWarnings({"PMD.CommentRequired", "PMD.PreserveStackTrace"})
public class AureliumSkillsStatsCondition extends Condition {

    private final VariableNumber targetLevelVar;
    private final Stat stat;

    private final boolean mustBeEqual;


    public AureliumSkillsStatsCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        final String statName = instruction.next();
        targetLevelVar = instruction.getVarNum();

        final AureliumSkills aureliumSkills = AureliumAPI.getPlugin();
        stat = aureliumSkills.getStatRegistry().getStat(statName);
        if (stat == null) {
            throw new InstructionParseException("Invalid stat name");
        }

        mustBeEqual = instruction.hasArgument("equal");
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final Player player = PlayerConverter.getPlayer(playerID);

        final double actualLevel = AureliumAPI.getStatLevel(player, stat);
        final double targetLevel = targetLevelVar.getDouble(playerID);

        return mustBeEqual ? actualLevel == targetLevel : actualLevel >= targetLevel;
    }
}
