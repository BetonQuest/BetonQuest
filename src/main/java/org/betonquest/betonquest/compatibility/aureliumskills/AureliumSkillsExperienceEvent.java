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

@SuppressWarnings({"PMD.CommentRequired", "PMD.PreserveStackTrace"})
public class AureliumSkillsExperienceEvent extends QuestEvent {

    private final AureliumSkills aureliumSkills;

    private final VariableNumber amountVar;
    private final boolean isLevel;
    private Skill skill;

    public AureliumSkillsExperienceEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        try {
            aureliumSkills = (AureliumSkills) Bukkit.getPluginManager().getPlugin("AureliumSkills");
        } catch (final ClassCastException exception) {
            throw new InstructionParseException("AureliumSkills wasn't able to be hooked due to: " + exception);
        }

        final String skillName = instruction.next();
        amountVar = instruction.getVarNum();
        isLevel = instruction.hasArgument("level");

        if (aureliumSkills != null) {
            skill = aureliumSkills.getSkillRegistry().getSkill(skillName);
            if (skill == null) {
                throw new InstructionParseException("Invalid skill name");
            }
        }
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Player player = PlayerConverter.getPlayer(playerID);
        final double amount = amountVar.getDouble(playerID);

        final PlayerData playerData = aureliumSkills.getPlayerManager().getPlayerData(player);

        if (playerData != null) {
            final int currentLevel = playerData.getSkillLevel(skill);

            if (isLevel) {
                for (int i = 1; i <= amount; i++) {
                    final double expRequirements = aureliumSkills.getLeveler().getXpRequired(currentLevel + i);
                    AureliumAPI.addXpRaw(player, skill, expRequirements);
                }
            } else {
                AureliumAPI.addXpRaw(player, skill, amount);
            }
        }
        return null;
    }
}
