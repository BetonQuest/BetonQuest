package org.betonquest.betonquest.compatibility.aureliumskills;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.api.AureliumAPI;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.skills.Skill;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.entity.Player;

@SuppressWarnings({"PMD.CommentRequired", "PMD.PreserveStackTrace"})
public class AureliumSkillsExperienceEvent extends QuestEvent {

    private final AureliumSkills aureliumSkills = AureliumAPI.getPlugin();

    private final VariableNumber amountVar;
    private final boolean isLevel;
    private final Skill skill;

    public AureliumSkillsExperienceEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        final String skillName = instruction.next();
        amountVar = instruction.getVarNum();
        isLevel = instruction.hasArgument("level");

        skill = aureliumSkills.getSkillRegistry().getSkill(skillName);
        if (skill == null) {
            throw new InstructionParseException("Invalid skill name");
        }
    }

    @Override
    protected Void execute(final Profile profile) throws QuestRuntimeException {
        final Player player = profile.getOnlineProfile().getOnlinePlayer();
        final PlayerData playerData = aureliumSkills.getPlayerManager().getPlayerData(player);

        if (playerData == null) {
            return null;
        }

        final int amount = amountVar.getInt(profile);

        if (isLevel) {
            final int currentLevel = playerData.getSkillLevel(skill);
            for (int i = 1; i <= amount; i++) {
                final double requiredXP = aureliumSkills.getLeveler().getXpRequirements().getXpRequired(skill, currentLevel + i);
                AureliumAPI.addXpRaw(player, skill, requiredXP);
            }
        } else {
            AureliumAPI.addXpRaw(player, skill, amount);
        }
        return null;
    }
}
