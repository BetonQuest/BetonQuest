package org.betonquest.betonquest.compatibility.mcmmo;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;

/**
 * Checks if the player has specified level in an mcMMO skill.
 */
public class McMMOSkillLevelCondition implements OnlineCondition {

    /**
     * Skill to check.
     */
    private final Argument<PrimarySkillType> skillType;

    /**
     * Required level in Skill.
     */
    private final Argument<Number> level;

    /**
     * Create a new level condition.
     *
     * @param skillType the type to check
     * @param level     the required level
     */
    public McMMOSkillLevelCondition(final Argument<PrimarySkillType> skillType, final Argument<Number> level) {
        this.skillType = skillType;
        this.level = level;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        return ExperienceAPI.getLevel(profile.getPlayer(),
                skillType.getValue(profile)) >= level.getValue(profile).intValue();
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
