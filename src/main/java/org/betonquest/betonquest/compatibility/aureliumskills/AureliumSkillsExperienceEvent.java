package org.betonquest.betonquest.compatibility.aureliumskills;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.api.AureliumAPI;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.skills.Skill;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AureliumSkillsExperienceEvent extends QuestEvent {

    private final AureliumSkills aureliumSkills;

    private final String skillName;
    private final VariableNumber amountVar;
    private final boolean isLevel;
    private final Skill skill;

    public AureliumSkillsExperienceEvent(Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        try {
            aureliumSkills = (AureliumSkills) Bukkit.getPluginManager().getPlugin("AureliumSkills");
        } catch (final ClassCastException exception) {
            throw new InstructionParseException("AureliumSkills wasn't able to be hooked due to: " + exception);
        }

        skillName = instruction.next();
        amountVar = instruction.getVarNum();
        isLevel = instruction.hasArgument("level");

        skill = aureliumSkills.getSkillRegistry().getSkill(skillName);

        if (skill == null) {
            throw new InstructionParseException("Invalid skill name");
        }
    }

    @Override
    protected Void execute(String playerID) throws QuestRuntimeException {
        Player player = PlayerConverter.getPlayer(playerID);
        final double amount = amountVar.getDouble(playerID);

        PlayerData playerData = aureliumSkills.getPlayerManager().getPlayerData(player);

        int currentLevel = playerData.getSkillLevel(skill);
        double currentExp = playerData.getSkillXp(skill);

        if (isLevel) {
            for (int i = 1; i <= amount; i++) {
                double expRequirements = aureliumSkills.getLeveler().getXpRequired(currentLevel + i);
                AureliumAPI.addXpRaw(player, skill, expRequirements);
            }
        } else {
            AureliumAPI.addXpRaw(player, skill, amount);
        }
        return null;
    }
}
