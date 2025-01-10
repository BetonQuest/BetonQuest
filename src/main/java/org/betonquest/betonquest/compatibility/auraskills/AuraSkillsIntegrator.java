package org.betonquest.betonquest.compatibility.auraskills;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.exception.HookException;
import org.betonquest.betonquest.quest.registry.QuestTypeRegistries;
import org.betonquest.betonquest.quest.registry.type.ConditionTypeRegistry;

/**
 * Integrator for AuraSkills.
 */
public class AuraSkillsIntegrator implements Integrator {

    /**
     * The default constructor.
     */
    public AuraSkillsIntegrator() {
    }

    @Override
    public void hook() throws HookException {
        final QuestTypeRegistries questRegistries = BetonQuest.getInstance().getQuestRegistries();
        final ConditionTypeRegistry conditionTypes = questRegistries.condition();
        conditionTypes.register("auraskillslevel", AuraSkillsLevelCondition.class);
        conditionTypes.register("auraskillsstatslevel", AuraSkillsStatsCondition.class);

        questRegistries.event().register("auraskillsxp", AuraSkillsExperienceEvent.class);
    }

    @Override
    public void reload() {
        // Empty
    }

    @Override
    public void close() {
        // Empty
    }
}
