package org.betonquest.betonquest.compatibility.auraskills.condition;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.user.SkillsUser;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.util.Utils;

/**
 * Checks if the player has a certain stat level in AuraSkills.
 */
public class AuraSkillsStatsCondition implements PlayerCondition {
    /**
     * The {@link AuraSkillsApi}.
     */
    private final AuraSkillsApi auraSkillsApi;

    /**
     * The target level.
     */
    private final Variable<Number> targetLevelVar;

    /**
     * The {@link Stat} name to check.
     */
    private final Variable<String> nameVar;

    /**
     * If the actual level must be equal to the target level.
     */
    private final boolean mustBeEqual;

    /**
     * Create a new AuraSkills Stats Condition.
     *
     * @param auraSkillsApi  the {@link AuraSkillsApi}.
     * @param targetLevelVar the target level.
     * @param nameVar        the {@link Stat} name to check.
     * @param mustBeEqual    if the actual level must be equal to the target level.
     */
    public AuraSkillsStatsCondition(final AuraSkillsApi auraSkillsApi, final Variable<Number> targetLevelVar,
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
        final Stat stat = Utils.getNN(auraSkillsApi.getGlobalRegistry().getStat(namespacedId), "Invalid stat name");
        final double actualLevel = user.getStatLevel(stat);
        final double targetLevel = targetLevelVar.getValue(profile).doubleValue();

        return mustBeEqual ? actualLevel == targetLevel : actualLevel >= targetLevel;
    }
}
