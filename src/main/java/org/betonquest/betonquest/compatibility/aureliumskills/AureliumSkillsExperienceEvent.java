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
import org.bukkit.entity.Player;

@SuppressWarnings({"PMD.CommentRequired", "PMD.PreserveStackTrace"})
public class AureliumSkillsExperienceEvent extends QuestEvent {

    private final AureliumSkills aureliumSkills;

    private final VariableNumber amountVar;
    private final boolean isLevel;
    private final Skill skill;

    public AureliumSkillsExperienceEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        final String skillName = instruction.next();
        amountVar = instruction.getVarNum();
        isLevel = instruction.hasArgument("level");

        aureliumSkills = AureliumSkillsIntegrator.getAureliumPlugin();

        skill = aureliumSkills.getSkillRegistry().getSkill(skillName);
        if (skill == null) {
            throw new InstructionParseException("Invalid skill name");
        }
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Player player = PlayerConverter.getPlayer(playerID);
        final PlayerData playerData = aureliumSkills.getPlayerManager().getPlayerData(player);

        if (playerData == null) {
            return null;
        }

        final int amount = amountVar.getInt(playerID);

        if (isLevel) {
            final int targetLevel = playerData.getSkillLevel(skill) + amount;
            playerData.setSkillLevel(skill, targetLevel);
        } else {
            AureliumAPI.addXpRaw(player, skill, amount);
        }
        return null;
    }
}
