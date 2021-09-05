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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AureliumSkillsStatsCondition extends Condition {

    private final AureliumSkills aureliumSkills;

    private final String statName;
    private final VariableNumber targetLevelVar;
    private final Stat stat;

    private boolean mustBeEqual;


    public AureliumSkillsStatsCondition(Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        try {
            aureliumSkills = (AureliumSkills) Bukkit.getPluginManager().getPlugin("AureliumSkills");
        } catch (final ClassCastException exception) {
            throw new InstructionParseException("AureliumSkills wasn't able to be hooked due to: " + exception);
        }

        statName = instruction.next();
        targetLevelVar = instruction.getVarNum();

        stat = aureliumSkills.getStatRegistry().getStat(statName);

        if (stat == null) {
            throw new InstructionParseException("Invalid stat name");
        }

        if (instruction.hasArgument("equal")) {
            mustBeEqual = true;
        }
    }

    @Override
    protected Boolean execute(String playerID) throws QuestRuntimeException {
        if (stat == null) {
            return false;
        }
        final Player player = PlayerConverter.getPlayer(playerID);

        final double actualLevel = AureliumAPI.getStatLevel(player,stat);
        final double targetLevel = targetLevelVar.getDouble(playerID);

        return mustBeEqual ? actualLevel == targetLevel : actualLevel >= targetLevel;
    }
}
