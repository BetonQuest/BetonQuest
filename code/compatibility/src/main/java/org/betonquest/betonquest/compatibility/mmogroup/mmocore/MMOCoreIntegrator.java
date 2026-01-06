package org.betonquest.betonquest.compatibility.mmogroup.mmocore;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.quest.QuestTypeRegistries;
import org.betonquest.betonquest.api.quest.action.ActionRegistry;
import org.betonquest.betonquest.api.quest.condition.ConditionRegistry;
import org.betonquest.betonquest.api.quest.objective.ObjectiveRegistry;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.condition.MMOCoreAttributeConditionFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.condition.MMOCoreClassConditionFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.condition.MMOCoreProfessionLevelConditionFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.event.MMOCoreAttributePointsActionFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.event.MMOCoreAttributeReallocationPointsActionFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.event.MMOCoreClassExperienceActionFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.event.MMOCoreClassPointsActionFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.event.MMOCoreProfessionExperienceActionFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.event.MMOCoreSkillPointsActionFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.objective.MMOCoreBreakCustomBlockObjectiveFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.objective.MMOCoreChangeClassObjectiveFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.objective.MMOCoreProfessionObjectiveFactory;

/**
 * Integrator for MMO CORE.
 */
public class MMOCoreIntegrator implements Integrator {

    /**
     * The default constructor.
     */
    public MMOCoreIntegrator() {
    }

    @Override
    public void hook(final BetonQuestApi api) {
        final QuestTypeRegistries questRegistries = api.getQuestRegistries();
        final ConditionRegistry conditionRegistry = questRegistries.condition();
        conditionRegistry.register("mmoclass", new MMOCoreClassConditionFactory());
        conditionRegistry.register("mmoattribute", new MMOCoreAttributeConditionFactory());
        conditionRegistry.register("mmoprofession", new MMOCoreProfessionLevelConditionFactory());

        final ObjectiveRegistry objectiveRegistry = questRegistries.objective();
        objectiveRegistry.register("mmoprofessionlevelup", new MMOCoreProfessionObjectiveFactory());
        objectiveRegistry.register("mmochangeclass", new MMOCoreChangeClassObjectiveFactory());
        objectiveRegistry.register("mmocorebreakblock", new MMOCoreBreakCustomBlockObjectiveFactory());

        final ActionRegistry eventRegistry = questRegistries.event();
        eventRegistry.register("mmoclassexperience", new MMOCoreClassExperienceActionFactory());
        eventRegistry.register("mmoprofessionexperience", new MMOCoreProfessionExperienceActionFactory());
        eventRegistry.register("mmocoreclasspoints", new MMOCoreClassPointsActionFactory());
        eventRegistry.register("mmocoreattributepoints", new MMOCoreAttributePointsActionFactory());
        eventRegistry.register("mmocoreattributereallocationpoints", new MMOCoreAttributeReallocationPointsActionFactory());
        eventRegistry.register("mmocoreskillpoints", new MMOCoreSkillPointsActionFactory());
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
