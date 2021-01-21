package org.betonquest.betonquest.compatibility.mcmmo;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.api.SkillAPI;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;

import java.util.Locale;

/**
 * Checks if the player has specified level in an mcMMO skill.
 */
@SuppressWarnings("PMD.CommentRequired")
public class McMMOSkillLevelCondition extends Condition {

    private final String skillType;
    private final VariableNumber level;

    public McMMOSkillLevelCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        skillType = instruction.next().toUpperCase(Locale.ROOT);
        if (!SkillAPI.getSkills().contains(skillType)) {
            throw new InstructionParseException("Invalid skill name");
        }
        level = instruction.getVarNum();
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        return ExperienceAPI.getLevel(PlayerConverter.getPlayer(playerID), PrimarySkillType.valueOf(skillType.toUpperCase(Locale.ROOT))) >= level.getInt(playerID);
    }

}
