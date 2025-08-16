package org.betonquest.betonquest.compatibility.auraskills.condition;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.user.SkillsUser;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.util.Utils;

/**
 * Checks whether a player has the required skill level.
 */
public class AuraSkillsLevelCondition implements PlayerCondition {
    /**
     * The {@link AuraSkillsApi}.
     */
    private final AuraSkillsApi auraSkillsApi;

    /**
     * The target level.
     */
    private final Variable<Number> targetLevelVar;

    /**
     * The {@link Skill} to check.
     */
    private final Variable<String> nameVar;

    /**
     * If the actual level must be equal to the target level.
     */
    private final boolean mustBeEqual;

    /**
     * Create a new AuraSkills Level Condition.
     *
     * @param auraSkillsApi  the {@link AuraSkillsApi}.
     * @param targetLevelVar the target level.
     * @param nameVar        the {@link Skill} name to check.
     * @param mustBeEqual    if {@code true} the actual level must be equal to the target level.
     */
    public AuraSkillsLevelCondition(final AuraSkillsApi auraSkillsApi, final Variable<Number> targetLevelVar,
                                    final Variable<String> nameVar, final boolean mustBeEqual) {
        this.auraSkillsApi = auraSkillsApi;
        this.targetLevelVar = targetLevelVar;
        this.nameVar = nameVar;
        this.mustBeEqual = mustBeEqual;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        final SkillsUser user = auraSkillsApi.getUser(profile.getPlayerUUID());
        if (user == null) {
            return false;
        }

        final NamespacedId namespacedId = NamespacedId.fromDefault(nameVar.getValue(profile));
        final Skill skill = Utils.getNN(auraSkillsApi.getGlobalRegistry().getSkill(namespacedId), "Invalid skill name");
        final int actualLevel = user.getSkillLevel(skill);
        final int targetLevel = targetLevelVar.getValue(profile).intValue();

        return mustBeEqual ? actualLevel == targetLevel : actualLevel >= targetLevel;
    }
}
