package org.betonquest.betonquest.compatibility.auraskills;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.user.SkillsUser;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.util.Utils;

/**
 * Checks whether a player has the required skill level.
 */
@SuppressWarnings("PMD.CommentRequired")
public class AuraSkillsLevelCondition extends Condition {

    private final AuraSkillsApi auraSkills = AuraSkillsApi.get();

    private final VariableNumber targetLevelVar;

    private final Skill skill;

    private final boolean mustBeEqual;

    public AuraSkillsLevelCondition(final Instruction instruction) throws QuestException {
        super(instruction, true);

        final String skillName = instruction.next();
        targetLevelVar = instruction.get(VariableNumber::new);
        mustBeEqual = instruction.hasArgument("equal");

        final NamespacedId namespacedId = NamespacedId.fromDefault(skillName);
        skill = Utils.getNN(auraSkills.getGlobalRegistry().getSkill(namespacedId), "Invalid skill name");
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestException {
        final SkillsUser user = auraSkills.getUser(profile.getPlayerUUID());

        if (user == null) {
            return false;
        }

        final int actualLevel = user.getSkillLevel(skill);
        final int targetLevel = targetLevelVar.getInt(profile);

        return mustBeEqual ? actualLevel == targetLevel : actualLevel >= targetLevel;
    }
}
