package org.betonquest.betonquest.compatibility.mcmmo;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.api.SkillAPI;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

import java.util.Locale;

/**
 * Checks if the player has specified level in an mcMMO skill.
 */
@SuppressWarnings("PMD.CommentRequired")
public class McMMOSkillLevelCondition extends Condition {
    private final String skillType;

    private final VariableNumber level;

    public McMMOSkillLevelCondition(final Instruction instruction) throws QuestException {
        super(instruction, true);
        skillType = instruction.next().toUpperCase(Locale.ROOT);
        if (!SkillAPI.getSkills().contains(skillType)) {
            throw new QuestException("Invalid skill name");
        }
        level = instruction.getVarNum();
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestException {
        return ExperienceAPI.getLevel(profile.getOnlineProfile().get().getPlayer(),
                PrimarySkillType.valueOf(skillType.toUpperCase(Locale.ROOT))) >= level.getInt(profile);
    }
}
