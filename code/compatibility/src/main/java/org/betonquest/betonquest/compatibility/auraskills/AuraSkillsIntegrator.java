package org.betonquest.betonquest.compatibility.auraskills;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.compatibility.auraskills.action.AuraSkillsExperienceActionFactory;
import org.betonquest.betonquest.compatibility.auraskills.condition.AuraSkillsLevelConditionFactory;
import org.betonquest.betonquest.compatibility.auraskills.condition.AuraSkillsStatsConditionFactory;
import org.betonquest.betonquest.lib.integration.IntegrationTemplate;

/**
 * Integrator for <a href="https://github.com/Archy-X/AuraSkills">AuraSkills</a>.
 */
public class AuraSkillsIntegrator extends IntegrationTemplate {

    /**
     * The default constructor.
     */
    public AuraSkillsIntegrator() {
        super();
    }

    /**
     * Get a skill from the AuraSkills API and checks that it is not null.
     *
     * @param auraSkillsApi the AuraSkills API.
     * @param namespacedId  the namespaced ID of the skill.
     * @return the skill.
     * @throws QuestException if the skill does not exist.
     */
    public static Skill getNonNullSkill(final AuraSkillsApi auraSkillsApi, final NamespacedId namespacedId) throws QuestException {
        final Skill skill = auraSkillsApi.getGlobalRegistry().getSkill(namespacedId);
        if (skill == null) {
            throw new QuestException("Skill '%s' does not exist!".formatted(namespacedId));
        }
        return skill;
    }

    @Override
    public void enable(final BetonQuestApi api) {
        final AuraSkillsApi auraSkillsApi = AuraSkillsApi.get();

        playerAction("xp", new AuraSkillsExperienceActionFactory(auraSkillsApi));

        playerCondition("level", new AuraSkillsLevelConditionFactory(auraSkillsApi));
        playerCondition("statslevel", new AuraSkillsStatsConditionFactory(auraSkillsApi));

        registerFeatures(api, "auraskills");
    }

    @Override
    public void postEnable(final BetonQuestApi api) {
        // Empty
    }

    @Override
    public void disable() {
        // Empty
    }
}
